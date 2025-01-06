package com.shuke.shukepicturebe.model.dto;

import com.shuke.shukepicturebe.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: UserQueryDTO
 * @Description: 用户查询DTO
 * @author: 舒克、舒克
 * @Date: 2025/1/3 11:47
 */
@Data
public class UserQueryDTO extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

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
