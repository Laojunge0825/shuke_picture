package com.shuke.shukepicturebe.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 舒克、舒克
 * @date 2025/2/20 17:13
 * @description: 空间使用排行分析请求
 */
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    /**
     * 排名前 N 的空间
     */
    private Integer topN = 10;

    private static final long serialVersionUID = 1L;
}

