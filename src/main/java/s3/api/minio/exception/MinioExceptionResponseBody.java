package storage.minio.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class MinioExceptionResponseBody {
    private String message;
    private final LocalDateTime dateTime = LocalDateTime.now();
}
