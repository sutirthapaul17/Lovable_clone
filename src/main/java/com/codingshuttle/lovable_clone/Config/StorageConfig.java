package com.codingshuttle.lovable_clone.Config;


import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")//we have two ways to fetch the value, either we can use @Value("${..}") or we can do this . It will search for all the configuration files for the value and get the value
@Data
public class StorageConfig {
    private String url;
    private String accessKey;
    private String secretKey;

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}
