package com.shuke.shukepicturebe.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shuke.shukepicturebe.annotation.AuthCheck;
import com.shuke.shukepicturebe.common.BaseResponse;
import com.shuke.shukepicturebe.common.DeleteRequest;
import com.shuke.shukepicturebe.common.ResultUtils;
import com.shuke.shukepicturebe.constant.UserConstant;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;
import com.shuke.shukepicturebe.exception.ThrowUtils;
import com.shuke.shukepicturebe.model.dto.*;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.vo.UserLoginVO;
import com.shuke.shukepicturebe.model.vo.UserVO;
import com.shuke.shukepicturebe.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: UserController
 * @Description: 用户接口
 * @author: 舒克、舒克
 * @Date: 2025/1/2 17:47
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegistorDTO userRegisterDTO) {
        ThrowUtils.throwIf(userRegisterDTO == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     * @param userLoginRequestDTO
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<UserLoginVO> userLogin(@RequestBody UserLoginDTO userLoginRequestDTO, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequestDTO == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequestDTO.getUserAccount();
        String userPassword = userLoginRequestDTO.getUserPassword();
        UserLoginVO userLoginVo = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(userLoginVo);
    }

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) {
        return ResultUtils.success(userService.getLoginUser(request));
    }

    /**
     * 用户注销，退出登录
     * @param request
     * @return
     */
    @GetMapping("/get/loginOut")
    public BaseResponse<Boolean> userLogOut(HttpServletRequest request) {
        return ResultUtils.success(userService.userLogOut(request));
    }

    /**
     * 新增用户
     * @param userAddDTO
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddDTO userAddDTO) {
        ThrowUtils.throwIf(userAddDTO == null, ErrorCode.PARAMS_ERROR);
        return userService.addUser(userAddDTO);
    }

    /**
     * 根据Id查询用户信息 不脱敏
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        return userService.getUserById(id);
    }

    /**
     * 根据Id查询用户信息 脱敏
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        return userService.getUserVoById(id);
    }

    /**
     * 删除用户信息
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.deleteUser(deleteRequest.getId());
    }

    /**
     * 修改用户信息
     * @param userUpdateDTO
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO == null || userUpdateDTO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userService.updateUser(userUpdateDTO);
    }

    /**
     * 分页查询用户信息 脱敏
     * @param userQueryDTO
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryDTO userQueryDTO) {
        ThrowUtils.throwIf(userQueryDTO == null, ErrorCode.PARAMS_ERROR);
        return userService.listUserVoByPage(userQueryDTO);
    }


}
