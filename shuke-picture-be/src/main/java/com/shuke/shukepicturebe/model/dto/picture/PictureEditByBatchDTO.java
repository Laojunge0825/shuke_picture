package com.shuke.shukepicturebe.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: PictureEditByBatchDTO
 * @Description: 图片批量编辑DTO
 * @author: 舒克、舒克
 * @Date: 2025/1/14 10:00
 */
@Data
public class PictureEditByBatchDTO implements Serializable {

    /**
     * 图片id列表
     */
    private List<Long> pictureIdList;

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 命名规则
     */
    private String nameRule;

    /**
     * 图片分类
     */
    private String category;

    /**
     * 图片标签
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}
