package com.shuke.shukepicturebe.model.enums;

import cn.hutool.core.util.ObjUtil;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;
import lombok.Getter;

import java.util.Objects;

/**
 * @author 舒克、舒克
 * @date 2025/2/24 15:04
 * @description: 空间类型枚举类
 */
@Getter
public enum SpaceTypeEnum {

    PRIVATE(0,"私有空间"),
    TEAM(1,"团队空间");

    private final Integer value;

    private final String text;

    SpaceTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 根据枚举值获取枚举
     */
    public static SpaceTypeEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceTypeEnum spaceTypeEnum : values()) {
            if (Objects.equals(value, spaceTypeEnum.getValue())) {
                return spaceTypeEnum;
            }
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR,"空间类别不存在");
    }
}
