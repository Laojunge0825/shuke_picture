package com.shuke.shukepicturebe.manager.auth.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 舒克、舒克
 * @date 2025/2/25 10:51
 * @description: 空间角色权限
 */
@Data
public class SpaceUserPermission implements Serializable {

    /**
     *  权限键
     */
    private String key;

    /**
     *  权限名
     */
    private String name;

    /**
     *  权限描述
     */
    private String description;

    private static final long serialVersionUID = 1L;
}
