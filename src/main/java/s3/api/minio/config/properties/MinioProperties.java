package storage.minio.config.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    private String endpoint;
    private Credentials credentials;

    @Data
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }
}
