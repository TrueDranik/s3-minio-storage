package s3.api.minio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("s3.api.minio.config.properties")
@SpringBootApplication
public class S3MinioStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(S3MinioStorageApplication.class, args);
    }

}
