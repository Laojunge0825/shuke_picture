package com.shuke.shukepicturebe.common;

import lombok.Data;

/**
 * @ClassName: PageRequest
 * @Description: 分页基础类
 * @author: 舒克、舒克
 * @Date: 2024/12/29 21:17
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}

