package com.shuke.shukepicturebe.manager.auth.model;

import org.springframework.stereotype.Component;

/**
 * @author 舒克、舒克
 * @date 2025/2/25 10:56
 * @description: 空间角色管理常量
 */
@Component
public interface SpaceUserPermissionConstant {

    /**
     *  空间用户管理权限
     */
     String SPACE_USER_PERMISSION = "spaceUser:manager";

    /**
     *  空间图片查看权限
     */

    String PICTURE_VIEW = "picture:view";

    /**
     *  空间图片上传权限
     */
    String PICTURE_UPLOAD = "picture:upload";

    /**
     *  空间图片编辑权限
     */
    String PICTURE_EDIT = "picture:edit";

    /**
     *  空间图片修改权限
     */
    String PICTURE_DELETE = "picture:delete";

}
