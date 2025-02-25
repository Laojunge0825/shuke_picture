package com.shuke.shukepicturebe.manager.auth;

import com.shuke.shukepicturebe.model.entity.Picture;
import com.shuke.shukepicturebe.model.entity.Space;
import com.shuke.shukepicturebe.model.entity.SpaceUser;
import com.shuke.shukepicturebe.model.entity.User;
import lombok.Data;

import java.util.List;

/**
 * @author 舒克、舒克
 * @date 2025/2/25 14:40
 * @description: 空间角色上下文类 用户在特定的空间授权上下文，包括关联的图片、空间、用户信息等
 */
@Data
public class SpaceUserAuthContext {

    /**
     *  临时参数  根据请求 可能是空间id、图片id或用户id
     */
    private Long id;

    /**
     *  权限列表
     */
    List<String> permissionList;

    /**
     *  空间Id
     */
    private Long SpaceId;

    /**
     * 用户Id
     */
    private Long UserId;

    /**
     *  图片Id
     */
    private Long PictureId;

    /**
     * 空间信息
     */
    private Space space;

    /**
     * 空间用户信息
     */
    private SpaceUser spaceUser;

    /**
     * 图片信息
     */
    private Picture picture;
}
