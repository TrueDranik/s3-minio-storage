package s3.api.minio.exception.file;

public class UpdateFileException extends ServerFileException {

    public static final String UPDATE_FILE_ERROR_MESSAGE = "Ошибка обновления файла!";

    public UpdateFileException() {
        super(UPDATE_FILE_ERROR_MESSAGE);
    }
}
