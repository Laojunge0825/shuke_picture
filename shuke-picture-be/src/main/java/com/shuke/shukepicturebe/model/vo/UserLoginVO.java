package com.shuke.shukepicturebe.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: UserLoginVO
 * @Description:  用户登录VO
 * @author: 舒克、舒克
 * @Date: 2025/1/2 17:36
 */
@Data
public class UserLoginVO {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 id
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
