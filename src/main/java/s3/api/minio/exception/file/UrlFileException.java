package storage.minio.exception.file;

public class UrlFileException extends ServerFileException {

    public static final String LINK_GENERATION_ERROR_MESSAGE = "Ошибка генерации ссылки!";

    public UrlFileException() {
        super(LINK_GENERATION_ERROR_MESSAGE);
    }
}
