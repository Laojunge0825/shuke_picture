package com.shuke.shukepicturebe.manager.upload;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import com.shuke.shukepicturebe.config.CosClientConfig;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;
import com.shuke.shukepicturebe.manager.CosManager;
import com.shuke.shukepicturebe.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: PictureUploadTemplate
 * @Description:  图片上传模块  兼容 本地上传图片 和 url 上传图片
 * @author: 舒克、舒克
 * @Date: 2025/1/20 10:25
 */
@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    protected CosManager cosManager;

    @Resource
    protected CosClientConfig cosClientConfig;

    /**
     * 模板方法，定义上传流程
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 1. 校验图片
        validPicture(inputSource);

        // 2. 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originFilename = getOriginFilename(inputSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);

        File file = null;
        try {
            // 3. 创建临时文件
            file = File.createTempFile(uploadPath, null);
            // 处理文件来源（本地或 URL）
            processFile(inputSource, file);

            // 4. 上传图片到对象存储
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 上传完压缩图后  删除原图（如果下载要下载原图的话，就注释这一行）
            cosManager.deleteObject(uploadPath);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();

            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                // 获取压缩之后得到的文件信息
                CIObject compressedCiObject = objectList.get(0);
//                // 缩略图默认等于压缩图
//                CIObject thumbnailCiObject = compressedCiObject;
//                // 有生成缩略图，才获取缩略图
//                if (objectList.size() > 1) {
//                    thumbnailCiObject = objectList.get(1);
//                }
                // 封装压缩图的返回结果
                return buildResult(originFilename, compressedCiObject ,null);
            }
            // 5. 获取图片信息 封装返回结果
            return buildResult(originFilename, file, uploadPath, imageInfo);
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 6. 清理临时文件
            deleteTempFile(file);
        }
    }

    /**
     * 校验输入源（本地文件或 URL）
     * @param inputSource
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取输入源的原始文件名
     * @param inputSource
     * @return
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 处理输入源并生成本地临时文件
     * @param inputSource
     * @param file
     * @throws Exception
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;

    /**
     * 封装返回结果
     */
    private UploadPictureResult buildResult(String originFilename, File file, String uploadPath, ImageInfo imageInfo) {
        // 创建 UploadPictureResult 对象
        var uploadPictureResult = new UploadPictureResult();
        // 获取图片的宽度和高度
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        // 计算并保留两位小数的宽高比例
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        // 使用方法链设置 UploadPictureResult 的属性
        return uploadPictureResult
                .setPicName(FileUtil.mainName(originFilename))
                .setPicWidth(picWidth)
                .setPicHeight(picHeight)
                .setPicScale(picScale)
                .setPicFormat(imageInfo.getFormat())
                .setPicSize(FileUtil.size(file))
                .setUrl(cosClientConfig.getHost() + "/" + uploadPath);
    }

    /**
     *   封装压缩后的返回结果
     * @param originFilename
     * @param compressedCiObject
     * @return
     */
    private UploadPictureResult buildResult(String originFilename, CIObject compressedCiObject ,CIObject thumbnailCiObject) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFilename))
        .setPicWidth(picWidth)
        .setPicHeight(picHeight)
        .setPicScale(picScale)
        .setPicFormat(compressedCiObject.getFormat())
        .setPicSize(compressedCiObject.getSize().longValue());
        // 设置图片为压缩后的地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
//        // 设置缩略图地址
//        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        return uploadPictureResult;
    }


    /**
     * 删除临时文件
     * @param file
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}

