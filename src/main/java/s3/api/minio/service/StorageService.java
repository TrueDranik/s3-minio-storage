package storage.minio.service;

import io.minio.messages.Bucket;

import java.util.List;

public interface StorageService {

    List<Bucket> getBuckets();

    void tryCreateBucket(String bucketName);

}
