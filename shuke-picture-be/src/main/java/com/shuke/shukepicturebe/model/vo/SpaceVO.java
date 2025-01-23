package com.shuke.shukepicturebe.model.vo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.shuke.shukepicturebe.model.entity.Space;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: SpaceVO
 * @Description:  空间的视图包装类
 * @author: 舒克、舒克
 * @Date: 2025/1/23 10:01
 */
@Data
public class SpaceVO implements Serializable {

    /**
     * 空间id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别
     */
    private Integer spaceLevel;

    /**
     * 空间图片最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片最大数量
     */
    private Long maxCount;

    /**
     * 空间当前的图片总大小
     */
    private Long totalSize;

    /**
     * 空间当前的图片总数量
     */
    private Long totalCount;

    /**
     * 创建空间的用户id
     */
    private Long userId;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date creatTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 创建空间的用户信息
     */
    private UserVO userVO;

    private static final long serialVersionUID = 1L;

    /**
     * 封装类装对象
     * @param spaceVO
     * @return
     */
    public static Space voToObj(SpaceVO spaceVO){
        if(ObjUtil.isNull(spaceVO)){
            return null;
        }
        Space space = new Space();
        BeanUtil.copyProperties(spaceVO,space);
        return space;
    }

    /**
     *  对象装封装类
     * @param space
     * @return
     */
    public static SpaceVO objToVO(Space space){
        if(ObjUtil.isNull(space)){
            return null;
        }
        SpaceVO spaceVO = new SpaceVO();
        BeanUtil.copyProperties(space,spaceVO);
        return spaceVO;
    }
}
