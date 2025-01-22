package com.shuke.shukepicturebe.model.dto.file;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: UploadPictureResult
 * @Description: 用于接收图片解析信息的包装类
 * @author: 舒克、舒克
 * @Date: 2025/1/14 10:21
 */
@Data
/**
 使用该注解后 set方法返回this 对象，而不是void 方便使用方法链 设置对象的属性。
 */
@Accessors(chain = true)
public class UploadPictureResult {
    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 图片主色调
     */
    private String picColor;

    /**
     * 缩略图 url
     */
    private String thumbnailUrl;

}
