package com.shuke.shukepicturebe.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;
import com.shuke.shukepicturebe.exception.ThrowUtils;
import com.shuke.shukepicturebe.manager.CosManager;
import com.shuke.shukepicturebe.manager.upload.FilePictureUpload;
import com.shuke.shukepicturebe.manager.upload.PictureUploadTemplate;
import com.shuke.shukepicturebe.manager.upload.UrlPictureUpload;
import com.shuke.shukepicturebe.model.dto.picture.*;
import com.shuke.shukepicturebe.model.dto.file.UploadPictureResult;
import com.shuke.shukepicturebe.model.entity.Picture;
import com.shuke.shukepicturebe.model.entity.Space;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.enums.PictureReviewStatusEnum;
import com.shuke.shukepicturebe.model.vo.PictureVO;
import com.shuke.shukepicturebe.model.vo.UserVO;
import com.shuke.shukepicturebe.service.PictureService;
import com.shuke.shukepicturebe.mapper.PictureMapper;
import com.shuke.shukepicturebe.service.SpaceService;
import com.shuke.shukepicturebe.service.UserService;
import com.shuke.shukepicturebe.utils.ColorSimilarUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 16422
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-01-14 09:41:40
*/
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private CosManager cosManager;

    /**
     * 图片上传
     * @param inputSource  文件输入源
     * @param pictureUploadDTO 图片上传DTO
     * @param loginUser 当前登录用户
     * @return PictureVO
     */
    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadDTO pictureUploadDTO, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_AUTH_ERROR);
        // 校验空间是否存在
        Long spaceId = pictureUploadDTO.getSpaceId();
        if(ObjUtil.isNotNull(spaceId)){
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(ObjUtil.isNull(space),ErrorCode.NOT_FOUND_ERROR,"空间不存在");
            // 只有空间创建人才可以上传
            ThrowUtils.throwIf(!ObjUtil.equals(loginUser.getId(),space.getUserId()),ErrorCode.NOT_AUTH_ERROR,"仅空间创建人可操作");
            ThrowUtils.throwIf(space.getTotalCount() >= space.getMaxCount(),ErrorCode.OPERATION_ERROR,"空间条数已达上限");
            ThrowUtils.throwIf(space.getTotalSize() >= space.getMaxSize() , ErrorCode.OPERATION_ERROR,"空间大小不足");
        }

        // 用于判断是新增还是更新图片
        Long pictureId;
        pictureId = pictureUploadDTO.getId();
        // 如果是更新图片，需要校验图片是否存在
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            // 仅本人或管理员可编辑
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NOT_AUTH_ERROR);
            }
            // 校验空间是否一致
            spaceId = ObjUtil.isNotNull(spaceId) ? spaceId : oldPicture.getSpaceId();
            ThrowUtils.throwIf(ObjUtil.notEqual(spaceId,oldPicture.getSpaceId()),ErrorCode.PARAMS_ERROR,"空间Id不一致");
        }
        // 上传图片，得到信息
        // 按照用户 id 划分目录  ==>  按照空间划分目录
        String uploadPathPrefix;
        if( ObjUtil.isNull(spaceId) ){
            uploadPathPrefix = String.format("public/%s", loginUser.getId());
        }else{
            uploadPathPrefix = String.format("space/%s", loginUser.getId());
        }

        //根据文件输入源的类型  区分上传方式
        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if ( inputSource instanceof String ){
            pictureUploadTemplate = urlPictureUpload;
        }
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        // 构造要入库的图片信息
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
        String picName = uploadPictureResult.getPicName();
        if(ObjUtil.isNotNull(pictureUploadDTO) && StrUtil.isNotBlank(pictureUploadDTO.getPicName())){
            picName = pictureUploadDTO.getPicName();
        }
        picture.setPicName(picName);
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setPicColor(uploadPictureResult.getPicColor());
        picture.setUserId(loginUser.getId());
        picture.setSpaceId(spaceId);
        // 补充审核参数
        fillReviewParams(picture, loginUser);
        // 如果 pictureId 不为空，表示更新，否则是新增
        if (pictureId != null) {
            // 如果是更新，需要补充 id 和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        // 开启事务
        Long finalSpaceId = spaceId;
        transactionTemplate.execute(status -> {
            boolean result = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
            // 公共空间没有spaceId
            if (finalSpaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, finalSpaceId)
                        .setSql("total_size = total_size + " + picture.getPicSize())
                        .setSql("total_count = total_count + 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            return picture;
        });

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
     * @param picture 图片
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
     * @param pictureQueryDTO 查询请求
     * @return QueryWrapper
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
        Long spaceId = pictureQueryDTO.getSpaceId();
        Date startEditTime = pictureQueryDTO.getStartEditTime();
        Date endEditTime = pictureQueryDTO.getEndEditTime();


        boolean nullSpaceId = pictureQueryDTO.isNullSpaceId();
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
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId),"space_id",spaceId);
        queryWrapper.isNull(nullSpaceId,"space_id");
        queryWrapper.ge(ObjUtil.isNotEmpty(startEditTime), "editTime", startEditTime);
        queryWrapper.lt(ObjUtil.isNotEmpty(endEditTime), "editTime", endEditTime);
        // JSON 数组查询  
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序  
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), "ascend".equals(sortOrder), sortField);
        return queryWrapper;
    }

    /**
     * 审核图片
     * @param pictureReviewDTO 审核图片DTO
     * @param loginUser 当前登录用户
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
        Picture updatePicture = new Picture();
        BeanUtils.copyProperties(pictureReviewDTO,updatePicture);
        updatePicture.setReviewStatus(pictureReviewDTO.getReviewStatus());
        updatePicture.setReviewerId(pictureReviewDTO.getReviewerId());
        updatePicture.setReviewTime(new Date());
        boolean result = this.updateById(updatePicture);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
    }

    /**
     * 补充审核参数
     * @param picture 图片参数
     * @param loginUser 当前登录用户
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

    /**
     * 批量抓取上传图片
     * @param pictureUploadByBatchDTO 批量抓取上传图片DTO
     * @param loginUser 当前登录用户
     * @return int
     */
    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchDTO pictureUploadByBatchDTO, User loginUser) {
        String searchText = pictureUploadByBatchDTO.getSearchText();
        String namePrefix = pictureUploadByBatchDTO.getNamePrefix();
        if(StrUtil.isBlank(namePrefix)){
            namePrefix = searchText;
        }
        // 格式化数量
        Integer count = pictureUploadByBatchDTO.getCount();
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR,"最多抓取30条");
        // 要抓取的地址
        // 获取图片数据的接口  https://cn.bing.com/images/async?q=%25s&mmasync=1
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1" , searchText);

        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("获取页面失败",e);
            throw   new BusinessException(ErrorCode.OPERATION_ERROR,"获取页面失败");
        }
        Element div =document.getElementsByClass("dgControl").first();
        if(ObjUtil.isNull(div)){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"获取页面元素失败");
        }

        // img.ming 下面是处理过的图片 是缩略图  宽度都是474
        // Elements imgElementList = div.select("img").addClass("ming");
        //  原图在 a iusc  下面找
        Elements imgElementList = div.select("a.iusc");
        int uploadCount = 0;
        for(Element imgElement : imgElementList){
            // String fileUrl = imgElement.attr("src");
            String dataM = imgElement.attr("m");
            // 解析JSON字符串
            JSONObject jsonObject = JSONUtil.parseObj(dataM);
            String fileUrl = jsonObject.getStr("murl");
            if(StrUtil.isBlank(fileUrl)){
                log.info("当前链接为空，直接跳过：{}",fileUrl);
                continue;
            }
            // 处理图片上传地址 ， 防止出现转义问题
            int questionMarkIndex = fileUrl.indexOf("?");
            if(questionMarkIndex > -1){
                fileUrl = fileUrl.substring(0,questionMarkIndex);
            }
            //上传图片
            PictureUploadDTO pictureUploadDTO = new PictureUploadDTO();
            // 设置图片名称 序号连续增加
            pictureUploadDTO.setPicName(namePrefix + (uploadCount+1));
            try{
                PictureVO pictureVO = this.uploadPicture(fileUrl,pictureUploadDTO,loginUser);
                log.info("图片上传成功，id={}" , pictureVO.getId());
                uploadCount++;
            }catch (Exception e){
                log.info("图片上传失败",e);
                continue;
            }
            if(uploadCount >= count){
                break;
            }
        }

        return uploadCount;
    }

    /**
     *  删除图片
     * @param id 图片id
     * @param loginUser 当前登录用户
     */
    @Override
    public void deletePicture(long id, User loginUser) {
        // 判断是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(ObjUtil.isNull(oldPicture), ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        ThrowUtils.throwIf(ObjUtil.notEqual(oldPicture.getUserId(),loginUser.getId()),
                ErrorCode.NOT_AUTH_ERROR,"仅本人或管理员可删除");
        // 校验空间权限
        checkPictureAuth(loginUser,oldPicture);
        // 开启事务
        transactionTemplate.execute(status -> {
            // 操作数据库
            boolean result = this.removeById(id);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            // 释放额度
            Long spaceId = oldPicture.getSpaceId();
            if (spaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, spaceId)
                        .setSql("total_size = total_size - " + oldPicture.getPicSize())
                        .setSql("total_count = total_count - 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            return true;
        });
        // 清除存储桶中的图片
        this.clearPictureFile(oldPicture);
    }

    /**
     * 删除对象存储的图片
     * @param picture 图片信息
     */
    @Async
    @Override
    public void clearPictureFile(Picture picture) {
        // 判断该图片是否被多条记录使用
        String pictureUrl = picture.getUrl();
        long count = this.lambdaQuery().eq(Picture::getUrl, pictureUrl).count();

        // 有不止一条记录用到了该图片  不清理
        if (count > 1) {
            return;
        }

        // 数据库里面存的url包含了域名 实际上只需要传key值（存储路径）
        String key = null;
        try {
            key = new URL(pictureUrl).getPath();
        } catch (MalformedURLException e) {
            log.error("解析 URL 时发生异常，URL: {}", pictureUrl, e);
        }
        cosManager.deleteObject(key);
    }

    @Override
    public void editPicture(PictureEditDTO pictureEditDTO, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditDTO, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditDTO.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        this.validPicture(picture);
        // 判断是否存在
        long id = pictureEditDTO.getId();
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(ObjUtil.isNull(oldPicture), ErrorCode.NOT_FOUND_ERROR);
        // 校验空间权限，已经改为使用注解鉴权
        checkPictureAuth(loginUser, oldPicture);
        // 补充审核参数
        this.fillReviewParams(picture, loginUser);
        // 操作数据库
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    @Override
    public void checkPictureAuth(User loginUser, Picture picture){
        long spaceId = picture.getSpaceId();
        if(ObjUtil.isNull(spaceId)){
            ThrowUtils.throwIf( (ObjUtil.notEqual(picture.getSpaceId(),spaceId) && !userService.isAdmin(loginUser)) ,
                    ErrorCode.NOT_AUTH_ERROR,"仅本人和管理员可操作");
        }else{
            ThrowUtils.throwIf( ObjUtil.notEqual(picture.getSpaceId(),spaceId) ,
                    ErrorCode.NOT_AUTH_ERROR,"仅空间创建者可操作" );
        }
    }

    /**
     * 通过颜色主色调查询
     * @param spaceId
     * @param picColor
     * @param loginUser
     * @return
     */
    @Override
    public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser) {
        // 1. 校验参数
        ThrowUtils.throwIf(spaceId == null || StrUtil.isBlank(picColor),ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null,ErrorCode.NOT_AUTH_ERROR);
        // 2. 校验空间权限
        Space space = spaceService.getById(spaceId);
        ThrowUtils.throwIf(ObjectUtil.isNull(space),ErrorCode.NOT_FOUND_ERROR,"空间不存在");
        if(!loginUser.getId().equals(space.getUserId())){
            throw new BusinessException(ErrorCode.NOT_AUTH_ERROR,"无空间访问权限");
        }
        // 3. 查询该空间下所有的图片（主色调不为空）
        List<Picture> pictureList = this.lambdaQuery()
                .eq(Picture::getSpaceId,spaceId)
                .isNotNull(Picture::getPicColor)
                .list();
        // 4. 没有图片的话返回空列表
        if(CollUtil.isEmpty(pictureList)){
            return Collections.emptyList();
        }
        // 将目标颜色转换为 Color 对象
        Color targetColor = Color.decode(picColor);
        // 5 . 计算图片相似度并排序
        List<Picture> sortedPictures = pictureList.stream()
                .sorted(Comparator.comparingDouble(picture -> {
                    // 提取图片主色调
                    String hexColor = picture.getPicColor();
                    // 没有主色调的图片放到最后
                    if(StrUtil.isBlank(hexColor)){
                        return Double.MAX_VALUE;
                    }
                    Color pictureColor = Color.decode(hexColor);
                    // 计算颜色相似度
                    return ColorSimilarUtils.calculateSimilarity(targetColor,pictureColor);
                }))
                // 取12个
                .limit(12)
                .collect(Collectors.toList());

        // 转换为pictureVo
        return sortedPictures.stream()
                .map(PictureVO::objToVo)
                .collect(Collectors.toList());
    }


}




