package com.shuke.shukepicturebe.model.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 用户权限枚举类
 * @author: 舒克、舒克
 * @Date: 2025/1/2 15:30
 */
@Getter
public enum UserRoleEnum {
    USER(0,"user"),
    ADMIN(1,"admin")
    ;


    private final  Integer value;

    private final String text;

    UserRoleEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    private static final Map<Integer, String> valueTextMap = new HashMap<>();

    /**
     *   将所有的枚举值存到Map中，只在类加载的时候执行一次就行，之后可以直接通过value获取text的值
     */
    static {
        for (UserStatusEnum status : UserStatusEnum.values()) {
            valueTextMap.put(status.getValue(), status.getText());
        }
    }

    public static String getTextByValue(Integer value) {
        return valueTextMap.get(value);
    }
}
