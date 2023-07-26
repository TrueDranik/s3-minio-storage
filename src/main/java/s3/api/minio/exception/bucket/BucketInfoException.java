package s3.api.minio.exception.bucket;

public class BucketInfoException extends ServerBucketException {

    public static final String GET_BUCKETS_ERROR_MESSAGE = "Не удалось получить корзины!";

    public BucketInfoException() {
        super(GET_BUCKETS_ERROR_MESSAGE);
    }
}
