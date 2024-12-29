package com.shuke.shukepicturebe.common;

import com.shuke.shukepicturebe.exception.ErrorCode;

/**
 * @ClassName: ResultUtils
 * @Description:
 * @author: 舒克、舒克
 * @Date: 2024/12/29 19:24
 */
public class ResultUtils {

    /**
     * 成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data){
        return  new BaseResponse<>(0,data,"ok");
    }

    /**
     * 失败
     * @param errorCode
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<?> fail(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<?> fail(int code, String message){
        return new BaseResponse<>(code,null,message);
    }

    /**
     * 失败
     * @param errorCode
     * @param message
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<?> fail(ErrorCode errorCode, String message){
        return new BaseResponse<>(errorCode.getCode(),null,message);
    }
}
