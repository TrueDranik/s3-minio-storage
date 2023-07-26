package storage.minio.exception.file;

public class UploadFileException extends ServerFileException {

    public static final String UPLOAD_ERROR_MESSAGE = "Ошибка загрузки!";

    public UploadFileException() {
        super(UPLOAD_ERROR_MESSAGE);
    }
}
