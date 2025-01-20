package com.shuke.shukepicturebe.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: PictureReviewDTO
 * @Description: 管理员审核实体类
 * @author: 舒克、舒克
 * @Date: 2025/1/16 16:30
 */
@Data
public class PictureReviewDTO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人Id
     */
    private Long reviewerId;

    private static final long serialVersionUID = 1L;
}
