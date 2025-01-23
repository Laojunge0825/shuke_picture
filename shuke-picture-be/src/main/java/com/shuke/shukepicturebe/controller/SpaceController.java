package com.shuke.shukepicturebe.controller;

import com.shuke.shukepicturebe.annotation.AuthCheck;
import com.shuke.shukepicturebe.common.BaseResponse;
import com.shuke.shukepicturebe.common.ResultUtils;
import com.shuke.shukepicturebe.constant.UserConstant;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;
import com.shuke.shukepicturebe.exception.ThrowUtils;
import com.shuke.shukepicturebe.model.dto.space.SpaceLevel;
import com.shuke.shukepicturebe.model.dto.space.SpaceUpdateDTO;
import com.shuke.shukepicturebe.model.entity.Space;
import com.shuke.shukepicturebe.model.enums.SpaceLevelEnum;
import com.shuke.shukepicturebe.service.SpaceService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: SpaceController
 * @Description: 空间模块 controller
 * @author: 舒克、舒克
 * @Date: 2025/1/23 11:39
 */
@RestController
@RequestMapping("/space")
public class SpaceController {

    @Resource
    private SpaceService spaceService;


    /**
     * 更新空间信息
     * @param spaceUpdateDTO
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateDTO spaceUpdateDTO) {
        if (spaceUpdateDTO == null || spaceUpdateDTO.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateDTO, space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);
        // 数据校验
        spaceService.validSpace(space, false);
        // 判断是否存在
        long id = spaceUpdateDTO.getId();
        Space oldSpace = spaceService.getById(id);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 获取空间级别列表
     * @return
     */
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        // 获取所有枚举
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values())
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
        return ResultUtils.success(spaceLevelList);
    }


}
