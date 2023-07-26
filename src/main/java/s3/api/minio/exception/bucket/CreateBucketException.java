package storage.minio.exception.bucket;

public class CreateBucketException extends ServerBucketException {

    public CreateBucketException(String message) {
        super(message);
    }
}
