package com.shuke.shukepicturebe.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @ClassName: SpaceLevelEnum
 * @Description: 空间级别枚举类  定义每个空间级别 对应的空间限额
 * @author: 舒克、舒克
 * @Date: 2025/1/23 10:14
 */
@Getter
public enum SpaceLevelEnum {

    COMMON("普通版", 0, 100, 100L * 1024 * 1024),
    PROFESSIONAL("专业版", 1, 1000, 1000L * 1024 * 1024),
    FLAGSHIP("旗舰版", 2, 10000, 10000L * 1024 * 1024);

    /** 级别名称 */
    private final String text;
    /** 级别值 */
    private final int value;
    /** 最大数量 */
    private final long maxCount;
    /** 最大空间大小，单位为字节 */
    private final long maxSize;

    SpaceLevelEnum(String text, int value, long maxCount, long maxSize) {
        this.text = text;
        this.value = value;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    /**
     * 根据value值获取枚举
     * @param value
     * @return
     */
    public static SpaceLevelEnum getEnumByValue(Integer value){
        if(ObjUtil.isNull(value)){
            return null;
        }
        for(SpaceLevelEnum spaceLevelEnum : SpaceLevelEnum.values()){
            if(spaceLevelEnum.value == value){
                return spaceLevelEnum;
            }
        }
        return  null;
    }
}
