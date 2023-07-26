package storage.minio.exception.file;

public class DeleteFileFromBucketException extends ServerFileException {

    public static final String DELETE_FILE_ERROR_MESSAGE = "Ошибка удаления файла!";

    public DeleteFileFromBucketException() {
        super(DELETE_FILE_ERROR_MESSAGE);
    }
}
