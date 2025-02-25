package com.shuke.shukepicturebe.manager.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 舒克、舒克
 * @date 2025/2/25 10:48
 * @description: 空间角色权限配置类
 */
@Data
public class SpaceUserAuthConfig implements Serializable {

    private List<SpaceUserPermission> permissions;

    private List<SpaceUserRole> roles;

    private static final long serialVersionUID = 1L;
}
