package com.shuke.shukepicturebe.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @ClassName: PictureReviewStatusEnum
 * @Description: 图片审核状态枚举类
 * @author: 舒克、舒克
 * @Date: 2025/1/16 16:25
 */
@Getter
public enum PictureReviewStatusEnum {
    REVIEWING("待审核",0),
    PASS("通过",1),
    REJECT("拒绝",2)
    ;

    private final String text;
    private final int value;

     PictureReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value 获取枚举
     * @param value
     * @return
     */
    public static PictureReviewStatusEnum getEnumByValue(Integer value){
         if(ObjUtil.isEmpty(value)){
             return null;
         }
         for(PictureReviewStatusEnum pictureReviewStatusEnum : PictureReviewStatusEnum.values()){
             if(pictureReviewStatusEnum.value == value){
                 return pictureReviewStatusEnum;
             }
         }

        return null;
    }
}
