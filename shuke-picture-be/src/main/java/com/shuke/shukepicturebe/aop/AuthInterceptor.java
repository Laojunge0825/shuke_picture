package com.shuke.shukepicturebe.aop;

import com.shuke.shukepicturebe.annotation.AuthCheck;
import com.shuke.shukepicturebe.constant.UserConstant;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.enums.UserRoleEnum;
import com.shuke.shukepicturebe.model.vo.UserLoginVO;
import com.shuke.shukepicturebe.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: AuthInterceptor
 * @Description: 权限校验AOP
 * @author: 舒克、舒克
 * @Date: 2025/1/3 11:03
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    UserService userService;

    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes)attributes).getRequest();
        // 获取当前登录用户
        UserLoginVO user = userService.getLoginUser(request);
        // 获取当前登录用户权限
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(user.getUserRole());
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 不需要权限 放行
        if(mustRoleEnum == null){
            return joinPoint.proceed();
        }
        // 权限为空 拒绝
        if(userRoleEnum == null){
            throw new  BusinessException(ErrorCode.NOT_AUTH_ERROR);
        }
        if(UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return  joinPoint.proceed();
    }
}
