package s3.api.minio.exception.file;

public class FileExistInBucketException extends ServerFileException {

    public FileExistInBucketException(String message) {
        super(message);
    }
}
