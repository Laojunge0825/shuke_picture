package com.shuke.shukepicturebe.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: MinioConfig
 * @Description: minio配置
 * @author: 舒克、舒克
 * @Date: 2025/1/6 16:07
 */
@Configuration
@Data
public class MinioConfig {
    /**
     * endpoint 是访问minio服务器的地址
     */
    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     *  minio 新建AccessKey时候的密钥 相当于是 用户名
     */
    @Value("${minio.accessKey}")
    private String accessKey;

    /**
     *  minio 新建AccessKey时候的密钥 相当于是 密码
     */
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     *  minio 默认存储桶
     */
    @Value("${minio.bucket.name}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
        return minioClient;
    }

}
