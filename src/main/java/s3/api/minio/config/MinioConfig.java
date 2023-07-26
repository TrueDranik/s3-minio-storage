package s3.api.minio.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import s3.api.minio.config.properties.MinioProperties;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getCredentials().getAccessKey(), minioProperties.getCredentials().getSecretKey())
                .build();
    }
}
