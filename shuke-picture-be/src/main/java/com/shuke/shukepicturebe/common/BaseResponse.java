package com.shuke.shukepicturebe.common;

import com.shuke.shukepicturebe.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: BaseResponse
 * @Description: 通用响应类
 * @author: 舒克、舒克
 * @Date: 2024/12/29 19:10
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data){
        this(code,data,"");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage());
    }
}
