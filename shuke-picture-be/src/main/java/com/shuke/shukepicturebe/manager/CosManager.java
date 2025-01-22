package com.shuke.shukepicturebe.manager;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.shuke.shukepicturebe.config.CosClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: CosManager
 * @Description:  文件上传下载的通用方法 与业务无关
 * @author: 舒克、舒克
 * @Date: 2025/1/14 14:33
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key  唯一键   本地文件所在路径
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }


    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }


    /**
     * 上传对象（附带图片信息）
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);

        // 1. 图片压缩 (webp格式)
        String webpKey = FileUtil.mainName(key)+".webp";
        // 构建压缩规则
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 图片处理规则列表
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setRule("imageMogr2/format/webp");
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(webpKey);
        rules.add(compressRule);
        // 2. 缩略图处理，仅对 > 20 KB 的图片生成缩略图
        // 暂时不处理缩略图  缩略图比压缩图大
//        if (file.length() > 2 * 1024) {
//            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
//            // 拼接缩略图的路径
//            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
//            thumbnailRule.setFileId(thumbnailKey);
//            thumbnailRule.setBucket(cosClientConfig.getBucket());
//            // 缩放规则 /thumbnail/<Width>x<Height>>（如果大于原图宽高，则不处理）
//            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 256, 256));
//            rules.add(thumbnailRule);
//        }
        // 构造处理参数
        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }


    /**
     * 删除对象
     * @param url
     */
    public void deleteObject(String url){
        // 数据库里面存的url包含了域名 实际上只需要传key值（存储路径）
//        try {
//            System.out.println(new URL(url).getPath());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
        String key = url.replace(cosClientConfig.getHost(),"");
        cosClient.deleteObject(cosClientConfig.getBucket(),key);
    }




}
