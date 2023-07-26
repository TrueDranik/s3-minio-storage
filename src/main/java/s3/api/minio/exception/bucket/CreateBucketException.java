package s3.api.minio.exception.bucket;

public class CreateBucketException extends ServerBucketException {

    public CreateBucketException(String message) {
        super(message);
    }
}
