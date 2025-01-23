package com.shuke.shukepicturebe.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuke.shukepicturebe.model.dto.space.SpaceAddDTO;
import com.shuke.shukepicturebe.model.dto.space.SpaceQueryDTO;
import com.shuke.shukepicturebe.model.entity.Space;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.vo.SpaceVO;
import com.shuke.shukepicturebe.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;


/**
 * @author 舒克、舒克
 * @description 针对表【space】的数据库操作Service
 * @createDate 2025-01-23 09:36:54
 */
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     * @param spaceAddDTO
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddDTO spaceAddDTO , User loginUser);

    /**
     * 校验空间
     * @param space
     * @param add
     */
    void validSpace(Space space , boolean add);

    /**
     * 获取空间封装类  单条数据
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space , HttpServletRequest request);

    /**
     * 获取空间封装类  分页
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage , HttpServletRequest request);

    /**
     * 将查询条件转为QueryWrapper 对象
     * @param spaceQueryDTO
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryDTO spaceQueryDTO);

    /**
     * 根据空间级别填充空间对象
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 校验空间权限
     * @param loginUser
     * @param space
     */
    void checkSpaceAuth(User loginUser , Space space);
}
