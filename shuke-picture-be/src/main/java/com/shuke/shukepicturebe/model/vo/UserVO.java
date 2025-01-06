package com.shuke.shukepicturebe.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: UserVO
 * @Description: 用户信息VO
 * @author: 舒克、舒克
 * @Date: 2025/1/3 14:21
 */
@Data
public class UserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
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

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}

