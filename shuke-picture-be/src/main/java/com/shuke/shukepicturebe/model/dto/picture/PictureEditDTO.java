package com.shuke.shukepicturebe.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: PictureEditDTO
 * @Description:
 * @author: 舒克、舒克
 * @Date: 2025/1/15 9:44
 */
@Data
public class PictureEditDTO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}

