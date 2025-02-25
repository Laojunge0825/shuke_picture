package com.shuke.shukepicturebe.manager.auth;

import cn.hutool.core.util.StrUtil;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 舒克、舒克
 * @date 2025/2/25 17:15
 * @description: 上下文数据管理
 */
public class SaTokenContextHolder {

    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    // 设置上下文数据
    public static void set(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    // 获取上下文数据
    public static Object get(String key) {
        if (StrUtil.isEmpty(key)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return CONTEXT.get().get(key);
    }

    // 清理上下文数据（防止内存泄漏）
    public static void clear() {
        CONTEXT.remove();
    }
}


