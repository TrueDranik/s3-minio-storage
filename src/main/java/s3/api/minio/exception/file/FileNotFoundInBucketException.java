package s3.api.minio.exception.file;

public class FileNotFoundInBucketException extends RuntimeException {

    public FileNotFoundInBucketException(String message) {
        super(message);
    }
}
