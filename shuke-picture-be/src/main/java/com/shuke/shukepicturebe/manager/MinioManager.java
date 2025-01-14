package com.shuke.shukepicturebe.manager;

import com.shuke.shukepicturebe.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @ClassName: MinioManager
 * @Description: Minio 管理
 * @author: 舒克、舒克
 * @Date: 2025/1/6 16:36
 */
@Component
public class MinioManager {

    @Resource
    private MinioConfig minioConfig;

    @Resource
    private MinioClient minioClient;

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     */
    public ObjectWriteResponse putObject(String key, File file) throws Exception {
        try {
            ensureBucketExists();
            return minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(key)
                    .filename(file.getAbsolutePath())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("上传对象失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     * @param key
     * @return
     * @throws Exception
     */
    public InputStream getObject(String key) throws Exception {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get object: " + e.getMessage());
        }
    }

    /**
     * 检查存储桶是否存在 不存在则创建
     * @throws Exception
     */
    private void ensureBucketExists() throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfig.getBucketName()).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfig.getBucketName()).build());
        }
    }
}
