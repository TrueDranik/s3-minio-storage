package s3.api.minio.exception.bucket;

public class ServerBucketException extends RuntimeException {

    public ServerBucketException(String message) {
        super(message);
    }
}
