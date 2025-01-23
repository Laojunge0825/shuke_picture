package com.shuke.shukepicturebe.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: UserRegistorDTO
 * @Description: 用户注册DTO
 * @author: 舒克、舒克
 * @Date: 2025/1/2 16:01
 */
@Data
public class UserRegistorDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;

}
