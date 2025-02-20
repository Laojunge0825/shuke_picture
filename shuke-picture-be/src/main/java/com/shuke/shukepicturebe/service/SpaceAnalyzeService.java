package com.shuke.shukepicturebe.service;

import com.shuke.shukepicturebe.model.dto.space.analyze.*;
import com.shuke.shukepicturebe.model.entity.Space;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.vo.space.analyze.SpaceCategoryAnalyzeResponse;
import com.shuke.shukepicturebe.model.vo.space.analyze.SpaceSizeAnalyzeResponse;
import com.shuke.shukepicturebe.model.vo.space.analyze.SpaceTagAnalyzeResponse;
import com.shuke.shukepicturebe.model.vo.space.analyze.SpaceUsageAnalyzeResponse;

import java.util.List;

/**
 * @author 舒克、舒克
 * @date 2025/2/20 15:02
 * @description: 空间分析业务类
 */
public interface SpaceAnalyzeService {

    /**
     * 校验空间分析权限
     * @param request
     * @param loginUser
     */
    void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest request, User loginUser);

    /**
     * 获取空间使用分析数据
     *
     * @param spaceUsageAnalyzeRequest  请求参数
     * @param loginUser                当前登录用户
     * @return SpaceUsageAnalyzeResponse 分析结果
     */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    /**
     * 空间使用排行 仅管理员可用
     *
     * @param spaceRankAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);




    /**
     * 按照分类 分组查询图片表的数据
     * @param spaceCategoryAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser);

    /**
     * 按照标签 查询图片表的数据
     * @param spaceTagAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    /**
     * 按照图片大小 分组查询图片表的数据
     * @param spaceSizeAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);
}
