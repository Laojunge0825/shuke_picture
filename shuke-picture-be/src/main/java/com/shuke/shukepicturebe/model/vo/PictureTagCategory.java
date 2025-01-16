package com.shuke.shukepicturebe.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: PictureTagCategory
 * @Description: 图片标签分类
 * @author: 舒克、舒克
 * @Date: 2025/1/15 11:55
 */
@Data
public class PictureTagCategory {

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */
    private List<String> categoryList;
}
