package com.shuke.shukepicturebe.service;

import com.shuke.shukepicturebe.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuke.shukepicturebe.model.vo.UserLoginVO;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 用户登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 返回脱敏后的数据
     */
    UserLoginVO userLogin(String userAccount , String userPassword , HttpServletRequest request);

    /**
     * 对接收的用户信息进行脱敏
     * @param user
     * @return
     */
    UserLoginVO getLoginUserVo (User user);

    /**
     * 获取当前已登录用户
     * @param request
     * @return
     */
    UserLoginVO getLoginUser (HttpServletRequest request);


    /**
     * 用户注销 ，退出登录
     * @param request
     * @return
     */
    boolean userLogOut(HttpServletRequest request);




}