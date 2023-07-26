package s3.api.minio.exception.file;

public class ListObjectException extends ServerFileException{

    public static final String GET_FILES_INFO_ERROR_MESSAGE = "Ошибка получения информации о файлах";

    public ListObjectException() {
        super(GET_FILES_INFO_ERROR_MESSAGE);
    }
}
