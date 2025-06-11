package com.shuke.shukepicturebe.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shuke.shukepicturebe.manager.auth.model.SpaceUserAuthConfig;
import com.shuke.shukepicturebe.manager.auth.model.SpaceUserRole;
import com.shuke.shukepicturebe.model.entity.Space;
import com.shuke.shukepicturebe.model.entity.SpaceUser;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.enums.SpaceRoleEnum;
import com.shuke.shukepicturebe.model.enums.SpaceTypeEnum;
import com.shuke.shukepicturebe.service.SpaceUserService;
import com.shuke.shukepicturebe.service.UserService;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 舒克、舒克
 * @date 2025/2/25 11:02
 * @description: 读取Json文件中的权限列表和角色列表
 */
@Configuration
public class SpaceUserAuthManager {

    private static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    @Resource
    private UserService userService;

    @Resource
    private SpaceUserService spaceUserService;

    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
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

    /**
     * 获取权限列表 区分公共图库，私有图库，团队图库
     * @param space
     * @param loginUser
     * @return
     */
    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return new ArrayList<>();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSIONS = getPermissionByRole(SpaceRoleEnum.ADMIN.getValue());
        // 公共图库
        if (space == null) {
            if (userService.isAdmin(loginUser)) {
                return ADMIN_PERMISSIONS;
            }
            return new ArrayList<>();
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return new ArrayList<>();
        }
        // 根据空间获取对应的权限
        switch (spaceTypeEnum) {
            case PRIVATE:
                // 私有空间，仅本人或管理员有所有权限
                if (space.getUserId().equals(loginUser.getId()) || userService.isAdmin(loginUser)) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return new ArrayList<>();
                }
            case TEAM:
                // 团队空间，查询 SpaceUser 并获取角色和权限
                SpaceUser spaceUser = spaceUserService.lambdaQuery()
                        .eq(SpaceUser::getSpaceId, space.getId())
                        .eq(SpaceUser::getUserId, loginUser.getId())
                        .one();
                if (spaceUser == null) {
                    return new ArrayList<>();
                } else {
                    return getPermissionByRole(spaceUser.getSpaceRole());
                }
        }
        return new ArrayList<>();
    }

}
