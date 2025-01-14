package com.shuke.shukepicturebe.controller;

import com.shuke.shukepicturebe.annotation.AuthCheck;
import com.shuke.shukepicturebe.common.BaseResponse;
import com.shuke.shukepicturebe.common.ResultUtils;
import com.shuke.shukepicturebe.constant.UserConstant;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;
import com.shuke.shukepicturebe.manager.MinioManager;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @ClassName: FileController
 * @Description:
 * @author: 舒克、舒克
 * @Date: 2025/1/13 17:30
 */
@RestController
@Slf4j
public class FileController {

    @Resource
    private MinioManager minioManager;


    /**
     * 测试文件上传
     *
     * @param multipartFile
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        // 文件目录
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            minioManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }


    /**
     * 测试文件下载
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        InputStream inputStream = null;
        try {
            // 从 MinioManager 获取文件的输入流
            inputStream = minioManager.getObject(filepath);
            
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            // 对文件名进行编码，防止中文乱码
            String encodedFileName = URLEncoder.encode(filepath, "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);

            // 将文件内容写入响应输出流
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer))!= -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (MinioException e) {
            // 处理 MinIO 异常
            // 这里可以自定义异常处理逻辑，例如记录日志或返回错误信息
            throw new IOException("文件下载失败", e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream!= null) {
                inputStream.close();
            }
        }
    }

}
