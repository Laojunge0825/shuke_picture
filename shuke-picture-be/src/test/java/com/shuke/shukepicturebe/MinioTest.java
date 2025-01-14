package com.shuke.shukepicturebe;

import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;

/**
 * @ClassName: MinioTest
 * @Description:
 * @author: 舒克、舒克
 * @Date: 2025/1/6 15:21
 */
public class MinioTest {

    @Test
    void uploadtest(){
        try{
            String endPoint = "http://192.168.147.128";
            int port = 9000;
            String accessKey = "NX1fILq4uwJtMcVQBwZ8";
            String secretKey = "G90IaqsJrHYcWzCsAwlccuQC22t2SBgG5WXmJ1g5";
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endPoint,port,false)
                    .credentials(accessKey,secretKey)
                    .build();
            // 定义桶名和对象名称
            String bucketName = "shuke-pic-bucket";
            String objectName = "test.jpg";
            String filePath = "C:\\Users\\16422\\Desktop/logo.png"; // 本地文件路径
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(filePath)
                            .build());
            System.out.println("上传测试成功........");
        }catch (Exception e){
            System.out.println("Error" + e);
        }
    }

    @Test
    void downLoad(){
        try {
            String endPoint = "http://192.168.147.128";
            int port = 9000;
            String accessKey = "NX1fILq4uwJtMcVQBwZ8";
            String secretKey = "G90IaqsJrHYcWzCsAwlccuQC22t2SBgG5WXmJ1g5";
            // 创建 MinIO 客户端
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endPoint,port,false)
                    .credentials(accessKey, secretKey)
                    .build();

            String bucketName = "shuke-pic-bucket";
            String objectName = "test.jpg";
            String filePath = "C:\\Users\\16422\\Desktop/logo1.png"; // 本地文件路径
            // 下载文件
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(filePath)
                            .build()
            );
            System.out.println("文件下载成功: " + filePath);

        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }


}
