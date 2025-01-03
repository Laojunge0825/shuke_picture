package com.shuke.shukepicturebe.service;

import com.shuke.shukepicturebe.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 舒克、舒克
* @description 针对表【user】的数据库操作Service
* @createDate 2025-01-02 15:09:46
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 对密码加盐加密
     *
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);




}