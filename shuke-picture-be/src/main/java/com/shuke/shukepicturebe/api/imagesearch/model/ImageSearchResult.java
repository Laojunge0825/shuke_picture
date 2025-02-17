package com.shuke.shukepicturebe.api.imagesearch.model;

import lombok.Data;

/**
 * @ClassName: IMageSearchResult
 * @Description: 图片搜索结果
 * @author: 舒克、舒克
 * @Date: 2025/1/15 10:03
 */
@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 原图地址
     */
    private String fromUrl;
}
