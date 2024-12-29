package com.shuke.shukepicturebe.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: DeleteRequest
 * @Description: 删除请求通用类
 * @author: 舒克、舒克
 * @Date: 2024/12/29 21:19
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}

