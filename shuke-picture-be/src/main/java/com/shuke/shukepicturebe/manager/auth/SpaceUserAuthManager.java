package com.shuke.shukepicturebe.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shuke.shukepicturebe.manager.auth.model.SpaceUserAuthConfig;
import com.shuke.shukepicturebe.manager.auth.model.SpaceUserRole;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 舒克、舒克
 * @date 2025/2/25 11:02
 * @description: 读取Json文件中的权限列表和角色列表
 */
public class SpaceUserAuthManager {

    private static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthManage.json");
        SPACE_USER_AUTH_CONFIG  = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据角色获取权限列表
     */
    public List<String> getPermissionByRole(String role) {
        if (StrUtil.isEmpty(role)) {
            return new ArrayList<>();
        }
        // 遍历找到匹配的角色
        SpaceUserRole userRole = SPACE_USER_AUTH_CONFIG.getRoles()
                .stream()
                .filter(r -> role.equals(r.getKey()))
                .findFirst()
                .orElse(null);
        if(userRole == null) {
            return new ArrayList<>();
        }

        return userRole.getPermissions();

    }
}
