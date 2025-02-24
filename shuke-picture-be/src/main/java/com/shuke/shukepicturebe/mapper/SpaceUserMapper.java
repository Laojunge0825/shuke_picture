package com.shuke.shukepicturebe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.shuke.shukepicturebe.model.entity.SpaceUser;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 16422
* @description 针对表【space_user(空间用户关联)】的数据库操作Mapper
* @createDate 2025-02-24 16:29:37
*/
@Mapper
public interface SpaceUserMapper extends BaseMapper<SpaceUser> {

}




