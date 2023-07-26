package s3.api.minio.rest.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import s3.api.minio.exception.MinioExceptionResponseBody;
import s3.api.minio.exception.bucket.ServerBucketException;
import s3.api.minio.exception.file.FileNotFoundInBucketException;
import s3.api.minio.exception.file.InputFileException;
import s3.api.minio.exception.file.ServerFileException;

@ResponseBody
@ControllerAdvice
public class MinioExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ServerFileException.class)
    public ResponseEntity<Object> handleServerFileException(ServerFileException e) {
        return new ResponseEntity<>(getExceptionResponseBody(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileNotFoundInBucketException.class)
    public ResponseEntity<Object> handleFileNotFoundInBucketException(FileNotFoundInBucketException e) {
        return new ResponseEntity<>(getExceptionResponseBody(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InputFileException.class)
    public ResponseEntity<Object> handleInputFileException(InputFileException e) {
        return new ResponseEntity<>(getExceptionResponseBody(e), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(ServerBucketException.class)
    public ResponseEntity<Object> handleServerBucketException(ServerBucketException e) {
        return new ResponseEntity<>(getExceptionResponseBody(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private MinioExceptionResponseBody getExceptionResponseBody(Exception e) {
        return MinioExceptionResponseBody.builder()
                .message(e.getMessage())
                .build();
    }
}
