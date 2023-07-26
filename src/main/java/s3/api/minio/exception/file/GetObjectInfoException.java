package s3.api.minio.exception.file;

public class GetObjectInfoException extends ServerFileException {

    public static final String GET_OBJECT_INFO_ERROR_MESSAGE = "Ошибка получаения файла!";

    public GetObjectInfoException() {
        super(GET_OBJECT_INFO_ERROR_MESSAGE);
    }
}
