package com.shuke.shukepicturebe.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: SpaceUpdateDTO
 * @Description: 空间修改 仅管理员使用  可以修改空间的级别和限额
 * @author: 舒克、舒克
 * @Date: 2025/1/23 9:48
 */
@Data
public class SpaceUpdateDTO implements Serializable {

    /**
     *  空间id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别
     */
    private Integer spaceLevel;

    /**
     * 空间图片最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片最大数量
     */
    private Long maxCount;

    private static final long serialVersionUID = 1L;
}

