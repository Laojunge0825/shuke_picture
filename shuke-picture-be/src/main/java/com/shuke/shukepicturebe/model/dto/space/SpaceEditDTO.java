package com.shuke.shukepicturebe.model.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: SpaceEditDTO
 * @Description: 编辑空间 提供给用户使用
 * @author: 舒克、舒克
 * @Date: 2025/1/23 9:50
 */
@Data
public class SpaceEditDTO implements Serializable {

    /**
     * 空间id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    private static final long serialVersionUID = 1L;
}
