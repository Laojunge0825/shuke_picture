package com.shuke.shukepicturebe.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 舒克、舒克
 * @date 2025/2/24 16:41
 * @description: 空间权限枚举类
 */
@Getter
public enum SpaceRoleEnum {
    VIEWER("浏览", "viewer"),
    EDITOR("编辑", "editor"),
    ADMIN("管理", "admin");
    private final String text;
    private final String value;
    SpaceRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }


    /**
     * 根据 value 获取枚举
     */
    public static SpaceRoleEnum getByValue(String value) {
        for (SpaceRoleEnum e : values()) {
            if (e.value.equals(value)) {
                return e;
            }

        }
        return null;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的 value
     * @return 枚举值
     */
    public static SpaceRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceRoleEnum anEnum : SpaceRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 获取所有枚举的文本列表
     *
     * @return 文本列表
     */
    public static List<String> getAllTexts() {
        return Arrays.stream(SpaceRoleEnum.values())
                .map(SpaceRoleEnum::getText)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有枚举的值列表
     *
     * @return 值列表
     */
    public static List<String> getAllValues() {
        return Arrays.stream(SpaceRoleEnum.values())
                .map(SpaceRoleEnum::getValue)
                .collect(Collectors.toList());
    }
}
