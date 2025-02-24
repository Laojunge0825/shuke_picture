package com.shuke.shukepicturebe.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: SpaceAddDTO
 * @Description:  创建空间
 * @author: 舒克、舒克
 * @Date: 2025/1/23 9:47
 */
@Data
public class SpaceAddDTO implements Serializable {

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间类型：0-私有 1-团队
     */
    private Integer spaceType;



    private static final long serialVersionUID = 1L;
}

