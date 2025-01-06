package com.shuke.shukepicturebe.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: UserAddDTO
 * @Description: 用户新增DTO
 * @author: 舒克、舒克
 * @Date: 2025/1/3 11:46
 */
@Data
public class UserAddDTO implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}
