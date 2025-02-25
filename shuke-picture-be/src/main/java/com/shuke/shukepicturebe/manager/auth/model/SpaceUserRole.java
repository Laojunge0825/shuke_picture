package com.shuke.shukepicturebe.manager.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 舒克、舒克
 * @date 2025/2/25 10:53
 * @description: 空间角色
 */
@Data
public class SpaceUserRole implements Serializable {

    /**
     *  角色键
     */
    private String key;

    /**
     *  角色名
     */
    private String name;

    /**
     *  角色权限列表
     */
    private List<String> permissions;

    /**
     *  角色描述
     */
    private String description;

    private static final long serialVersionUID = 1L;
}
