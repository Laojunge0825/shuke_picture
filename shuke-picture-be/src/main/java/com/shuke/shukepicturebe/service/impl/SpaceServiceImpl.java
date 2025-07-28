package com.shuke.shukepicturebe.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;
import com.shuke.shukepicturebe.exception.ThrowUtils;
import com.shuke.shukepicturebe.manager.sharding.DynamicShardingManager;
import com.shuke.shukepicturebe.mapper.SpaceMapper;
import com.shuke.shukepicturebe.model.dto.space.SpaceAddDTO;
import com.shuke.shukepicturebe.model.dto.space.SpaceQueryDTO;
import com.shuke.shukepicturebe.model.entity.Space;
import com.shuke.shukepicturebe.model.entity.SpaceUser;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.enums.SpaceLevelEnum;
import com.shuke.shukepicturebe.model.enums.SpaceRoleEnum;
import com.shuke.shukepicturebe.model.enums.SpaceTypeEnum;
import com.shuke.shukepicturebe.model.vo.SpaceVO;
import com.shuke.shukepicturebe.model.vo.UserVO;
import com.shuke.shukepicturebe.service.SpaceService;
import com.shuke.shukepicturebe.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
* @author 舒克、舒克
* @description 针对表【space】的数据库操作Service实现
* @createDate 2025-01-23 09:36:54
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService {

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private UserService userService;

    @Resource
    private SpaceUserServiceImpl spaceUserService;

    @Resource
    @Lazy
    private DynamicShardingManager dynamicShardingManager;

//    @Resource
//    private ConcurrentHashMap<Long, ReentrantLock> concurrentHashMap;

    private final ConcurrentHashMap<String, Object> concurrentHashMap = new ConcurrentHashMap<>();

    @Override
    public long addSpace(SpaceAddDTO spaceAddDTO, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddDTO, space);
        // 默认值
        if (StrUtil.isBlank(space.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        if (space.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        if (space.getSpaceType() == null) {
            space.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        // 填充数据 空间级别 和 空间类型
        this.buildInsertSpace(spaceAddDTO, loginUser);
        // 数据校验
        this.validSpace(space, true);
        Long userId = loginUser.getId();
        space.setUserId(userId);
        // 权限校验  普通用户 只能创建普通版的空间模块,以及一个团队空间
        if (SpaceLevelEnum.COMMON.getValue() != space.getSpaceLevel() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NOT_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        // 针对用户进行加锁
//        String lock = String.valueOf(userId).intern();
//        synchronized (lock) {
//            Long newSpaceId = transactionTemplate.execute(status -> {
//                boolean exists = this.lambdaQuery().eq(Space::getUserId, userId).exists();
//                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有空间");
//                // 写入数据库
//                boolean result = this.save(space);
//                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
//                // 返回新写入的数据 id
//                return space.getId();
//            });
//            // 返回结果是包装类，可以做一些处理
//            return Optional.ofNullable(newSpaceId).orElse(-1L);
//        }

        // 针对用户加锁
        // 根据userId 获取concurrentHashMap 里面对应的RenntrantLock 对象，如果不存在就新建一个
        ReentrantLock reentrantLock = (ReentrantLock) concurrentHashMap.computeIfAbsent(String.valueOf(userId), k -> new ReentrantLock());
        reentrantLock.lock();
        try {
            Long newSpaceId = transactionTemplate.execute(status -> {
                // 校验是否已经有该类型的空间
                boolean exists = this.lambdaQuery()
                        .eq(Space::getUserId, userId)
                        .eq(Space::getSpaceType, space.getSpaceType())
                        .exists();
                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有空间");
                // 写入数据库
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

                // 创建成功后 如果是团队空间  关联一条团队成员记录
                if(SpaceTypeEnum.TEAM.getValue() == space.getSpaceType()) {
                    SpaceUser spaceUser = new SpaceUser();
                    spaceUser.setSpaceId(space.getId());
                    spaceUser.setUserId(userId);
                    spaceUser.setSpaceRole(SpaceRoleEnum.ADMIN.getValue());
                    boolean save = spaceUserService.save(spaceUser);
                    ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR,"创建团队成员记录失败");
                }

                // 在创建新的团队空间时候 创建分表
                dynamicShardingManager.createSpacePictureTable( space);


                // 返回新写入的数据 id
                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        } finally {
            reentrantLock.unlock();
        }

    }

    @Override
    public void validSpace(Space space, boolean add) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        Integer spaceType = space.getSpaceType();
        SpaceLevelEnum.getEnumByValue(spaceLevel);
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getEnumByValue(spaceType);
        // 要创建
        if (add) {
            if (StrUtil.isBlank(spaceName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            }
            // 修改数据时，如果要改空间级别
            if (spaceType != null && spaceTypeEnum == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类型不存在");
            }
        }
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
    }



    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVO(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVo(user);
            spaceVO.setUserVO(userVO);
        }
        return spaceVO;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // 对象列表 => 封装对象列表
        List<SpaceVO> spaceVOList = spaceList.stream()
                .map(SpaceVO::objToVO)
                .collect(Collectors.toList());
        // 1. 关联查询用户信息
        // 1,2,3,4
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        // 1 => user1, 2 => user2
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUserVO(userService.getUserVo(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryDTO spaceQueryDTO) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryDTO == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryDTO.getId();
        Long userId = spaceQueryDTO.getUserId();
        String spaceName = spaceQueryDTO.getSpaceName();
        Integer spaceLevel = spaceQueryDTO.getSpaceLevel();
        String sortField = spaceQueryDTO.getSortField();
        String sortOrder = spaceQueryDTO.getSortOrder();
        Integer spaceType = spaceQueryDTO.getSpaceType();
        // 拼接查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "user_id", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "space_name", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "space_level", spaceLevel);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceType), "space_type", spaceType);

        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        /** 根据空间级别，自动填充限额 */
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if( ObjUtil.isNotNull(spaceLevelEnum) ){
            space.setMaxSize(spaceLevelEnum.getMaxSize());
            space.setMaxCount(spaceLevelEnum.getMaxCount());
        }
    }

    @Override
    public void checkSpaceAuth(User loginUser, Space space) {

    }

    /**
     * 修复新建时的空间信息
     * @param spaceAddDTO
     * @param loginUser
     * @return
     */
    private Space buildInsertSpace(SpaceAddDTO spaceAddDTO, User loginUser) {
        Space space = new Space();
        // 默认值
        if (StrUtil.isBlank(spaceAddDTO.getSpaceName())) {
            spaceAddDTO.setSpaceName("默认空间");
        }
        if (spaceAddDTO.getSpaceLevel() == null) {
            spaceAddDTO.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        if (spaceAddDTO.getSpaceType() == null) {
            spaceAddDTO.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        }
        BeanUtils.copyProperties(spaceAddDTO, space);
        space.setUserId(loginUser.getId());
        this.fillSpaceBySpaceLevel(space);
        return space;
    }

}




