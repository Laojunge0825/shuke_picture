package com.shuke.shukepicturebe.model.dto.spaceUser;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 舒克、舒克
 * @date 2025/2/24 16:37
 * @description: 空间-用户信息的查询
 */
@Data
public class SpaceUserQueryRequest implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}


