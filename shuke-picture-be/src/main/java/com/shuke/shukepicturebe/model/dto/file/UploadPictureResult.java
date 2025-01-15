package com.shuke.shukepicturebe.model.dto.file;

import lombok.Data;

/**
 * @ClassName: UploadPictureResult
 * @Description: 用于接收图片解析信息的包装类
 * @author: 舒克、舒克
 * @Date: 2025/1/14 10:21
 */
@Data
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

}
