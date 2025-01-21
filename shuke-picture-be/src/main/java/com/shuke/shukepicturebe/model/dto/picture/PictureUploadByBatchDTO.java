package com.shuke.shukepicturebe.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: PictureUploadByBatchDTO
 * @Description: 图片批量抓取
 * @author: 舒克、舒克
 * @Date: 2025/1/21 9:34
 */
@Data
public class PictureUploadByBatchDTO implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;

    /**
     * 图片名称前缀
     */
    private String namePrefix;

    private static final long serialVersionUID = 1L;

}
