package storage.minio.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.messages.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import storage.minio.exception.bucket.BucketInfoException;
import storage.minio.exception.bucket.CreateBucketException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    public static final String GET_BUCKETS_ERROR_MESSAGE = "Ошибка получения корзин";
    public static final String CREATE_BUCKET_ERROR_MESSAGE = "Не удалось создать корзину!";

    private final MinioClient minioClient;

    @Override
    public List<Bucket> getBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            log.error(GET_BUCKETS_ERROR_MESSAGE, e);
            throw new BucketInfoException();
        }
    }

    @Override
    public void tryCreateBucket(String bucketName) {
        boolean bucketIsExists = bucketExists(bucketName);
        if (bucketIsExists) {
            throw new CreateBucketException("Корзина с именем %s уже сущетсвует".formatted(bucketName));
        } else {
            createBucket(bucketName);
        }
    }

    private boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error(GET_BUCKETS_ERROR_MESSAGE, e);
            throw new BucketInfoException();
        }
    }

    private void createBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error(CREATE_BUCKET_ERROR_MESSAGE, e);
            throw new CreateBucketException(CREATE_BUCKET_ERROR_MESSAGE);
        }
    }
}
