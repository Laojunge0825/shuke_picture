package com.shuke.shukepicturebe.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: UserUpdateDTO
 * @Description: 用户修改DTO
 * @author: 舒克、舒克
 * @Date: 2025/1/3 11:50
 */
@Data
public class UserUpdateDTO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 用户状态 0 正常  ， 1 封禁
     */
    private Integer userStatus;

    private static final long serialVersionUID = 1L;
}
