package com.shuke.shukepicturebe.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shuke.shukepicturebe.exception.ErrorCode;
import com.shuke.shukepicturebe.exception.ThrowUtils;
import com.shuke.shukepicturebe.model.dto.space.analyze.*;
import com.shuke.shukepicturebe.model.entity.Picture;
import com.shuke.shukepicturebe.model.entity.Space;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.vo.space.analyze.SpaceCategoryAnalyzeResponse;
import com.shuke.shukepicturebe.model.vo.space.analyze.SpaceSizeAnalyzeResponse;
import com.shuke.shukepicturebe.model.vo.space.analyze.SpaceTagAnalyzeResponse;
import com.shuke.shukepicturebe.model.vo.space.analyze.SpaceUsageAnalyzeResponse;
import com.shuke.shukepicturebe.service.PictureService;
import com.shuke.shukepicturebe.service.SpaceAnalyzeService;
import com.shuke.shukepicturebe.service.SpaceService;
import com.shuke.shukepicturebe.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 舒克、舒克
 * @date 2025/2/20 15:03
 * @description: 空间分析业务实现类
 */
@Service
public class SpaceAnalyzeServiceImpl implements SpaceAnalyzeService {

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private PictureService pictureService;

    /**
     * 校验空间分析权限
     * @param request
     * @param loginUser
     */
    @Override
    public void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest request, User loginUser) {
        if(request.isQueryAll() || request.isQueryPublic()){
            ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NOT_AUTH_ERROR);
        }
        if(request.getSpaceId() != null && request.getSpaceId() > 0){
            Space space = spaceService.getById(request.getSpaceId());
            ThrowUtils.throwIf(!space.getUserId().equals(loginUser.getId()), ErrorCode.NOT_AUTH_ERROR,"非本人");
        }
    }

    /**
     * 空间使用分析
     * @param spaceUsageAnalyzeRequest  请求参数
     * @param loginUser                当前登录用户
     * @return
     */
    @Override
    public SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser) {
        checkSpaceAnalyzeAuth(spaceUsageAnalyzeRequest, loginUser);
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        /// 只查询一个字段 提高查询效率
        queryWrapper.select("pic_size");
        fillQueryWrapper(spaceUsageAnalyzeRequest, queryWrapper);
        List<Object> pictureList = pictureService.getBaseMapper().selectObjs(queryWrapper);
        // 当前图片数量
        long usedCount = pictureList.size();
        //已使用大小
        long usedSize = pictureList.stream().filter(Objects::nonNull)
                .mapToLong(res -> res instanceof Long ? (long) res : 0L).sum();

        /// 公共图库和所有图库没有上限、比例这些参数
        if (spaceUsageAnalyzeRequest.isQueryAll() || spaceUsageAnalyzeRequest.isQueryPublic()) {
            return SpaceUsageAnalyzeResponse.builder()
                    .usedCount(usedCount)
                    .usedSize(usedSize)
                    .build();
        }
        /// 针对某个具体的空间
        if(spaceUsageAnalyzeRequest.getSpaceId() != null && spaceUsageAnalyzeRequest.getSpaceId() > 0){
            Space space = spaceService.getById(spaceUsageAnalyzeRequest.getSpaceId());
            ThrowUtils.throwIf(space==null,ErrorCode.NOT_FOUND_ERROR,"空间不存在");
            /// 校验空间权限 仅空间所有者可以访问
            spaceService.checkSpaceAuth(loginUser,space);

            double sizeUsageRatio = space.getTotalSize() * 100.0 / space.getMaxSize();
            double countUsageRatio = space.getMaxCount() * 100.0 / space.getMaxCount();
            return SpaceUsageAnalyzeResponse.builder()
                    .maxCount(space.getMaxCount())
                    .maxSize(space.getMaxSize())
                    .usedCount(usedCount)
                    .usedSize(usedSize)
                    .sizeUsageRatio(sizeUsageRatio)
                    .countUsageRatio(countUsageRatio)
                    .build();

        }
        return new SpaceUsageAnalyzeResponse();
    }

    /**
     * 空间使用排行分析
     * @param spaceRankAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 仅管理员可查看空间排行
        ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NOT_AUTH_ERROR, "无权查看空间排行");

        // 构造查询条件
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "space_name", "user_id", "total_size")
                .orderByDesc("total_size")
                .last("LIMIT " + spaceRankAnalyzeRequest.getTopN()); // 取前 N 名

        // 查询结果
        return spaceService.list(queryWrapper);
    }




    /**
     * 按照 分类 分组查询图片表的数据
     * @param spaceCategoryAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceCategoryAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        // 根据分析范围补充查询条件
        fillQueryWrapper(spaceCategoryAnalyzeRequest, queryWrapper);

        // 使用 MyBatis-Plus 分组查询
        queryWrapper.select("category AS category",
                        "COUNT(*) AS count",
                        "SUM(pic_size) AS total_size")
                .groupBy("category");

        // 查询并转换结果
        return pictureService.getBaseMapper().selectMaps(queryWrapper)
                .stream()
                .map(result -> {
                    String category = result.get("category") != null ? result.get("category").toString() : "未分类";
                    Long count = ((Number) result.get("count")).longValue();
                    Long totalSize = ((Number) result.get("total_size")).longValue();
                    return new SpaceCategoryAnalyzeResponse(category, count, totalSize);
                })
                .collect(Collectors.toList());
    }

    /**
     * 按照 标签 获取空间照片数据
     * @param spaceTagAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceTagAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillQueryWrapper(spaceTagAnalyzeRequest, queryWrapper);

        // 查询所有符合条件的标签
        queryWrapper.select("tags");
        List<String> tagsJsonList = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .filter(ObjUtil::isNotNull)
                .map(Object::toString)
                .collect(Collectors.toList());

        // 合并所有标签并统计使用次数
        Map<String, Long> tagCountMap = tagsJsonList.stream()
                /**
                 * flatMap 会对集合中的每个 JSON 字符串执行以下操作：
                 * JSONUtil.toList(tagsJson, String.class)：使用 JSONUtil 工具将 JSON 字符串解析成一个 List<String>，也就是把 JSON 格式的标签列表字符串转换为实际的字符串列表。
                 * .stream()：将解析后的 List<String> 转换为一个流。
                 * flatMap 会把这些由每个 JSON 字符串解析得到的小流合并成一个大的流，这样就可以把所有的标签都放到一个流中进行后续处理。
                 * 如["校园","素材"] , ["素材"], ["风景"] 合并为 ["校园","素材","素材","风景"]
                 */
                .flatMap(tagsJson -> JSONUtil.toList(tagsJson, String.class).stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));

        // 转换为响应对象，按使用次数降序排序
        return tagCountMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 降序排列  升序改成第一个值减第二个值
                .map(entry -> new SpaceTagAnalyzeResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 按照 图片大小 分段查询图片表的数据
     * @param spaceSizeAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceSizeAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillQueryWrapper(spaceSizeAnalyzeRequest, queryWrapper);

        // 查询所有符合条件的图片大小
        queryWrapper.select("pic_size");
        List<Long> picSizes = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .map(size -> ((Number) size).longValue())
                .collect(Collectors.toList());

        // 定义分段范围，注意使用有序 Map
        Map<String, Long> sizeRanges = new LinkedHashMap<>();
        picSizes.forEach(picSize -> {
            if (picSize < 100 * 1024) {
                sizeRanges.put("<100KB", sizeRanges.getOrDefault("<100KB", 0L) + 1);
            }else if (picSize < 500 * 1024) {
                sizeRanges.put("100KB-500KB", sizeRanges.getOrDefault("100KB-500KB", 0L) + 1);
            }else if (picSize < 1 * 1024 * 1024) {
                sizeRanges.put("500KB-1MB", sizeRanges.getOrDefault("500KB-1MB", 0L) + 1);
            }else if (picSize < 2 * 1024 * 1024) {
                sizeRanges.put(">1MB", sizeRanges.getOrDefault(">1MB", 0L) + 1);
            }
        });

        // 转换为响应对象
        return sizeRanges.entrySet().stream()
                .map(entry -> new SpaceSizeAnalyzeResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }



    /**
     * 填充空间查询范围
     * @param request
     * @param queryWrapper
     */
    private static void fillQueryWrapper(SpaceAnalyzeRequest request, QueryWrapper<Picture> queryWrapper) {
        if(request.isQueryAll()){
            return;
        }
        if(request.isQueryPublic()){
            queryWrapper.isNull("space_id");
            return;
        }
        Long spaceId = request.getSpaceId();
        if(spaceId != null){
            queryWrapper.eq("space_id",spaceId);
            return;
        }
        throw new RuntimeException("参数错误");
    }

}
