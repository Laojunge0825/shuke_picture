package com.shuke.shukepicturebe.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuke.shukepicturebe.common.BaseResponse;
import com.shuke.shukepicturebe.model.dto.user.UserAddDTO;
import com.shuke.shukepicturebe.model.dto.user.UserQueryDTO;
import com.shuke.shukepicturebe.model.dto.user.UserUpdateDTO;
import com.shuke.shukepicturebe.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuke.shukepicturebe.model.vo.UserLoginVO;
import com.shuke.shukepicturebe.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
     * 对当前登录的用户信息进行脱敏
     * @param user
     * @return
     */
    UserLoginVO getLoginUserVo (User user);

    /**
     * 获取当前已登录用户
     * @param request
     * @return
     */
    User getLoginUser (HttpServletRequest request);


    /**
     * 用户注销 ，退出登录
     * @param request
     * @return
     */
    boolean userLogOut(HttpServletRequest request);

    /**
     * 对单个用户信息脱敏
     * @param user
     * @return
     */
    UserVO getUserVo(User user);

    /**
     * 对多个用户信息脱敏
     * @param uList
     * @return
     */
    List<UserVO> getUserVoList(List<User> uList);

    /**
     * 新增用户
     * @param userAddDTO
     * @return
     */
    BaseResponse<Long> addUser(UserAddDTO userAddDTO);

    /**
     * 删除用户
     * @param id
     * @return
     */
    BaseResponse<Boolean> deleteUser(long id);

    /**
     * 修改用户
     * @param userUpdateDTO
     * @return
     */
    BaseResponse<Boolean> updateUser(UserUpdateDTO userUpdateDTO);

    /**
     * 分页查询用户信息
     * @param userQueryDTO
     * @return
     */
    BaseResponse<Page<UserVO>> listUserVoByPage(UserQueryDTO userQueryDTO);

    /**
     * 根据ID查询用户信息
     * @param id
     * @return
     */
    BaseResponse<User> getUserById(long id);

    /**
     * 根据ID查询脱敏用户信息
     * @param id
     * @return
     */
    BaseResponse<UserVO> getUserVoById(long id);

    /**
     * 将查询请求 转换成QueryWrapper
     * @param userQueryDTO
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryDTO userQueryDTO);


    /**
     * 判断用户是否为管理员
     * @param user
     * @return
     */
    boolean isAdmin(User user);

}