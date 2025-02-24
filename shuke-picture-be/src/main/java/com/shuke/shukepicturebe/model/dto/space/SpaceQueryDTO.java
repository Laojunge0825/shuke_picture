package com.shuke.shukepicturebe.model.dto.space;

import com.shuke.shukepicturebe.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: SpaceQueryDTO
 * @Description: 空间查询
 * @author: 舒克、舒克
 * @Date: 2025/1/23 9:47
 */
@Data
public class SpaceQueryDTO extends PageRequest implements Serializable {

    /**
     *  空间id
     */
    private Long id;

    /**
     *  用户id
     */
    private Long userId;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别
     */
    private Integer spaceLevel;

    /**
     * 空间类型：0-私有 1-团队
     */
    private Integer spaceType;



    private static final long serialVersionUID = 1L;
}
