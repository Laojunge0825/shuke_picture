package com.shuke.shukepicturebe.model.dto.spaceUser;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 舒克、舒克
 * @date 2025/2/24 16:36
 * @description: 空间-用户的管理修改
 */
@Data
public class SpaceUserEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    private static final long serialVersionUID = 1L;
}


