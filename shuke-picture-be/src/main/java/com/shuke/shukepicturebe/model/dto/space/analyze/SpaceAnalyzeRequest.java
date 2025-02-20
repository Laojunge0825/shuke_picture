package com.shuke.shukepicturebe.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 舒克、舒克
 * @date 2025/2/20 10:07
 * @description: 公共图库分析封装类
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 是否查询公共图库
     */
    private boolean queryPublic;

    /**
     * 是否查询所有图库
     */
    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}
