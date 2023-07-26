package s3.api.minio.service;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SnowballObject;
import io.minio.StatObjectArgs;
import io.minio.UploadSnowballObjectsArgs;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import s3.api.minio.exception.file.DeleteFileFromBucketException;
import s3.api.minio.exception.file.FileExistInBucketException;
import s3.api.minio.exception.file.FileNotFoundInBucketException;
import s3.api.minio.exception.file.GetObjectInfoException;
import s3.api.minio.exception.file.InputFileException;
import s3.api.minio.exception.file.ListObjectException;
import s3.api.minio.exception.file.UpdateFileException;
import s3.api.minio.exception.file.UploadFileException;
import s3.api.minio.exception.file.UrlFileException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    public static final String FILE_EXIST = "Файл %s уже существует!";
    public static final String FAILED_TO_GET_FILE = "Не удалось получить файл %s!";
    public static final int DEFAULT_TIME_LIFE = (int) TimeUnit.DAYS.toSeconds(7L);
    public static final String FILE_NOT_FOUND = "Файл с именем %s не найден";

    private final MinioClient minioClient;

    @Override
    public void uploadFiles(String bucketName, @Nullable String packageName, MultipartFile[] files) {
        List<SnowballObject> snowballObjects = createSnowballs(bucketName, files, packageName);
        uploadFiles(bucketName, snowballObjects);
    }

    private List<SnowballObject> createSnowballs(String bucketName, MultipartFile[] files, String packageName) {
        var snowballs = new ArrayList<SnowballObject>(files.length);
        for (MultipartFile file : files) {
            try (var input = file.getInputStream()) {
                byte[] bytes = input.readAllBytes();
                String fullFileName = createFullFileName(packageName, file.getOriginalFilename());
                if (!fileExists(bucketName, fullFileName)) {
                    snowballs.add(
                            new SnowballObject(
                                    fullFileName, new ByteArrayInputStream(bytes),
                                    file.getSize(), null
                            )
                    );
                } else {
                    String errorMessage = FILE_EXIST.formatted(fullFileName);
                    log.info(errorMessage);
                    throw new FileExistInBucketException(errorMessage);
                }
            } catch (IOException e) {
                String errorMessage = FAILED_TO_GET_FILE.formatted(file.getOriginalFilename());
                log.error(errorMessage, e);
                throw new InputFileException(errorMessage);
            }
        }
        return snowballs;
    }

    private void uploadFiles(String bucketName, List<SnowballObject> snowballObjects) {
        try {
            log.info("Началась загрузка файлов в корзину '{}'", bucketName);
            ObjectWriteResponse uploadSnowballObjects = minioClient.uploadSnowballObjects(
                    UploadSnowballObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(snowballObjects)
                            .build());
            log.info("Архив '{}' с тегом '{}' добавлен в корзину '{}'", uploadSnowballObjects.object(),
                    uploadSnowballObjects.etag(), uploadSnowballObjects.bucket());
        } catch (Exception e) {
            log.error("Ошибка во время загрузки файлов!", e);
            throw new UploadFileException();
        }
    }

    @Override
    public String getUrl(String bucketName, @Nullable String packageName, String fileName, @Nullable Integer seconds) {
        String fullFileName = createFullFileName(packageName, fileName);
        int expiry = seconds != null ? seconds : DEFAULT_TIME_LIFE;
        return tryToGetUrl(bucketName, fullFileName, expiry);
    }

    private String tryToGetUrl(String bucketName, String fullFileName, int expiry) {
        try {
            if (fileExists(bucketName, fullFileName)) {
                return minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.GET)
                                .bucket(bucketName)
                                .object(fullFileName)
                                .expiry(expiry)
                                .build());
            } else {
                String errorMessage = FILE_NOT_FOUND.formatted(fullFileName);
                log.info(errorMessage);
                throw new FileNotFoundInBucketException(errorMessage);
            }
        } catch (Exception e) {
            log.error("Ошибка генерации ссылки!", e);
            throw new UrlFileException();
        }
    }

    @Override
    public void deleteFile(String bucketName, @Nullable String packageName, String fileName) {
        String fullFileName = createFullFileName(packageName, fileName);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullFileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Ошибка удаления файла!", e);
            throw new DeleteFileFromBucketException();
        }
    }

    @Override
    public List<Item> getFilesInfoFromBucket(String bucketName) {
        Iterable<Result<Item>> filesFromBucket = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .recursive(true)
                .build());

        List<Item> fileList = new ArrayList<>();
        for (Result<Item> result : filesFromBucket) {
            Item item;
            try {
                item = result.get();
            } catch (Exception e) {
                log.error("Ошибка получения информации о файлах", e);
                throw new ListObjectException();
            }
            fileList.add(item);
        }
        return fileList;
    }

    private boolean fileExists(String bucketName, String fullFileName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fullFileName)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void updateFile(String bucketName, @Nullable String packageName, String fileName, MultipartFile file) {
        String fullFileName = createFullFileName(packageName, fileName);
        if (fileExists(bucketName, fullFileName)) {
            try (InputStream inputStream = file.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fullFileName)
                        .stream(new ByteArrayInputStream(bytes), file.getSize(), -1)
                        .build());
            } catch (Exception e) {
                log.error("Ошибка обновления файла!", e);
                throw new UpdateFileException();
            }
        } else {
            throw new FileNotFoundInBucketException(FILE_NOT_FOUND.formatted(fullFileName));
        }
    }

    @Override
    public InputStream getObject(String bucketName, @Nullable String packageName, String fileName) {
        String fullFileName = createFullFileName(packageName, fileName);
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fullFileName)
                    .build());
        } catch (Exception e) {
            log.error("Ошибка получаения файла!", e);
            throw new GetObjectInfoException();
        }
    }

    @NotNull
    private String createFullFileName(String packageName, String fileName) {
        String pack = packageName != null ? packageName + "/" : "";
        return pack + fileName;
    }
}
