package com.shuke.shukepicturebe.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;
import com.shuke.shukepicturebe.exception.ThrowUtils;
import com.shuke.shukepicturebe.manager.FileManager;
import com.shuke.shukepicturebe.model.dto.picture.PictureQueryDTO;
import com.shuke.shukepicturebe.model.dto.picture.PictureReviewDTO;
import com.shuke.shukepicturebe.model.dto.picture.PictureUploadDTO;
import com.shuke.shukepicturebe.model.dto.file.UploadPictureResult;
import com.shuke.shukepicturebe.model.entity.Picture;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.enums.PictureReviewStatusEnum;
import com.shuke.shukepicturebe.model.vo.PictureVO;
import com.shuke.shukepicturebe.model.vo.UserVO;
import com.shuke.shukepicturebe.service.PictureService;
import com.shuke.shukepicturebe.mapper.PictureMapper;
import com.shuke.shukepicturebe.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 16422
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-01-14 09:41:40
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;

    /**
     * 图片上传
     * @param multipartFile
     * @param pictureUploadDTO
     * @param loginUser
     * @return
     */
    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadDTO pictureUploadDTO, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_AUTH_ERROR);
        // 用于判断是新增还是更新图片
        Long pictureId = null;
        if (pictureUploadDTO != null ) {
            pictureId = pictureUploadDTO.getId();
        }
        // 如果是更新图片，需要校验图片是否存在
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            // 仅本人或管理员可编辑
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NOT_AUTH_ERROR);
            }
        }
        // 上传图片，得到信息
        // 按照用户 id 划分目录
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
        // 构造要入库的图片信息
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setPicName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        // 补充审核参数
        fillReviewParams(picture, loginUser);
        // 如果 pictureId 不为空，表示更新，否则是新增
        if (pictureId != null) {
            // 如果是更新，需要补充 id 和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
        return PictureVO.objToVo(picture);
    }

    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVo(user);
            pictureVO.setUserVO(userVO);
        }
        return pictureVO;
    }


    /**
     * 分页获取图片封装
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        // 发送一次请求，获取到需要查询的用户id列表
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        //根据用户id列表查询到相关的用户信息
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUserVO(userService.getUserVo(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    /**
     * 图片校验
     * @param picture
     */
    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null,ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        ThrowUtils.throwIf(ObjectUtil.isNull(id),ErrorCode.PARAMS_ERROR,"id不能为空");
        if(StrUtil.isNotBlank(url)){
            ThrowUtils.throwIf(url.length() > 1024 , ErrorCode.PARAMS_ERROR,"url 过长");
        }
        if(StrUtil.isNotBlank(introduction)){
            ThrowUtils.throwIf(introduction.length() > 1024 , ErrorCode.PARAMS_ERROR,"简介过长");
        }
    }


    /**
     * 将查询请求转换成 QueryWrapper对象
     * @param pictureQueryDTO
     * @return
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryDTO pictureQueryDTO) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryDTO == null) {
            return queryWrapper;
        }
        // 从对象中取值  
        Long id = pictureQueryDTO.getId();
        String name = pictureQueryDTO.getPicName();
        String introduction = pictureQueryDTO.getIntroduction();
        String category = pictureQueryDTO.getCategory();
        List<String> tags = pictureQueryDTO.getTags();
        Long picSize = pictureQueryDTO.getPicSize();
        Integer picWidth = pictureQueryDTO.getPicWidth();
        Integer picHeight = pictureQueryDTO.getPicHeight();
        Double picScale = pictureQueryDTO.getPicScale();
        String picFormat = pictureQueryDTO.getPicFormat();
        String searchText = pictureQueryDTO.getSearchText();
        Long userId = pictureQueryDTO.getUserId();
        String sortField = pictureQueryDTO.getSortField();
        String sortOrder = pictureQueryDTO.getSortOrder();
        Integer reviewStatus = pictureQueryDTO.getReviewStatus();
        String reviewMessage = pictureQueryDTO.getReviewMessage();
        Long reviewerId = pictureQueryDTO.getReviewerId();

        // 从多字段中搜索  
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件  
            queryWrapper.and(qw -> qw.like("pic_name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "user_id", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "pic_name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "pic_format", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "pic_width", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "pic_height", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "pic_size", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "pic_scale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "review_status", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "review_message", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewer_id", reviewerId);
        // JSON 数组查询  
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序  
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 审核图片
     * @param pictureReviewDTO
     * @param loginUser
     */
    @Override
    public void doPictureReview(PictureReviewDTO pictureReviewDTO, User loginUser) {
        Long id = pictureReviewDTO.getId();
        Integer reviewStatus = pictureReviewDTO.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        if(id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 已审核
        if(oldPicture.getReviewStatus().equals(reviewStatus)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"请勿重复审核");
        }
        // 更新审核状态
        Picture updatePictue = new Picture();
        BeanUtils.copyProperties(pictureReviewDTO,updatePictue);
        updatePictue.setReviewStatus(pictureReviewDTO.getReviewStatus());
        updatePictue.setReviewerId(pictureReviewDTO.getReviewerId());
        updatePictue.setReviewTime(new Date());
        boolean result = this.updateById(updatePictue);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
    }

    /**
     * 补充审核参数
     * @param picture
     * @param loginUser
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            // 管理员自动过审
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage("管理员自动过审");
            picture.setReviewTime(new Date());
        } else {
            // 非管理员，创建或编辑都要改为待审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }


}




