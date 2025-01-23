package com.shuke.shukepicturebe.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName: SpaceLevel
 * @Description: 空间级别
 * @author: 舒克、舒克
 * @Date: 2025/1/23 17:45
 */
@Data
@AllArgsConstructor
public class SpaceLevel {

    /**
     * 值
     */
    private int value;

    /**
     * 中文
     */
    private String text;

    /**
     * 最大数量
     */
    private long maxCount;

    /**
     * 最大容量
     */
    private long maxSize;

}
