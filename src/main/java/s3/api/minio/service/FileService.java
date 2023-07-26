package storage.minio.service;

import io.minio.messages.Item;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface FileService {

    void uploadFiles(String bucketName, @Nullable String packageName, MultipartFile[] files);

    String getUrl(String bucketName, @Nullable String packageName, String fileName, @Nullable Integer seconds);

    void deleteFile(String bucketName, @Nullable String packageName, String fileName);

    List<Item> getFilesInfoFromBucket(String bucketName);

    void updateFile(String bucketName, @Nullable String packageName, String fileName, MultipartFile file);

    InputStream getObject(String bucketName, @Nullable String packageName, String fileName);

}
