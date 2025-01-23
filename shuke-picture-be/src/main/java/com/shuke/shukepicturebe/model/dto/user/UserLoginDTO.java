package com.shuke.shukepicturebe.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: UserLoginDTO
 * @Description: 用户登录DTO
 * @author: 舒克、舒克
 * @Date: 2025/1/2 16:04
 */
@Data
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;


}
