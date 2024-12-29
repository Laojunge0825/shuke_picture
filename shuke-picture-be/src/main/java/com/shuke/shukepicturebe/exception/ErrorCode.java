package com.shuke.shukepicturebe.exception;

import lombok.Getter;

/**
 * @ClassName: ErrorCode
 * @Description: 错误码枚举类
 * @author: 舒克、舒克
 * @Date: 2024/12/29 18:51
 */
@Getter
public enum ErrorCode {
    PARAMS_ERROR(40000,"请求参数错误"),
    NOT_LOGIN_ERROR(40100,"未登录"),
    NOT_AUTH_ERROR(40101,"无权限"),
    NOT_FOUND_ERROR(40400,"请求数据不存在"),
    FORBIDDEN_ERROR(40400,"禁止访问"),
    SYSTEM_ERROR(40400,"系统内部异常"),
    OPERATION_ERROR(40400,"操作失败"),
    ;

    /**
     *  状态码
     */
    private final int code;

    /**
     *  错误信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
