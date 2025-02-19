package com.shuke.shukepicturebe.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shuke.shukepicturebe.api.aliyun.model.CreateOutPaintingTaskResponse;
import com.shuke.shukepicturebe.model.dto.picture.*;
import com.shuke.shukepicturebe.model.entity.Picture;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 舒克、舒克
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-01-14 09:41:40
*/
public interface PictureService extends IService<Picture> {


    /**
     * 上传图片
     *
     * @param inputSource  文件输入源
     * @param pictureUploadDTO
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadDTO pictureUploadDTO,
                            User loginUser);


    /**
     * 将单张图片转换为封装类
     * @param picture
     * @param httpServletRequest
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest httpServletRequest);

    /**
     * 分页将图片转换成封装类
     * @param picturePage
     * @param httpServletRequest
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage,HttpServletRequest httpServletRequest);


    /**
     * 图片校验
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 将请求条件转成QuerryWrapper的形式
     * @param pictureQueryDTO
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryDTO pictureQueryDTO);

    /**
     * 图片审核
     * @param pictureReviewDTO
     * @param loginUser
     */
    void doPictureReview(PictureReviewDTO pictureReviewDTO, User loginUser);

    /**
     * 补充审核参数
     * @param picture
     * @param loginUser
     */
    void fillReviewParams(Picture picture , User loginUser);


    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchDTO
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(PictureUploadByBatchDTO pictureUploadByBatchDTO, User loginUser);

    /**
     * 删除图片
     * @param id
     * @param loginUser
     */
    void deletePicture(long id,User loginUser);

    /**
     * 删除对象
     * @param picture
     */
    void clearPictureFile(Picture picture);

    /**
     * 编辑图片
     * @param pictureEditDTO
     * @param loginUser
     */
    void editPicture(PictureEditDTO pictureEditDTO, User loginUser);

    /**
     * 空间权限校验
     * @param loginUser
     * @param picture
     */
    void checkPictureAuth(User loginUser,Picture picture);


    /**
     *  通过图片主色调查询
     * @param spaceId
     * @param picColor
     * @param loginUser
     * @return
     */
    List<PictureVO> searchPictureByColor(Long spaceId ,String picColor , User loginUser);

    /**
     * 批量修改图片
     * @param pictureEditByBatchDTO
     * @param loginUser
     */
    void editPictureByBatch(PictureEditByBatchDTO pictureEditByBatchDTO, User loginUser);

    /**
     *  批量修改图片 数据量大的时候 使用线程池+分批+并发处理
     * @param pictureEditByBatchDTO
     * @param loginUser
     */
    void editPictureMetadata(PictureEditByBatchDTO pictureEditByBatchDTO, User loginUser);

    /**
     * 填充图片名称
     * @param pictureList
     * @param nameRule
     */
    void fillPictureWithNameRule( List<Picture> pictureList,String nameRule);


    /**
     * 创建扩图任务
     *
     * @param createPictureOutPaintingTaskDTO
     * @param loginUser
     * @return
     */
    CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskDTO createPictureOutPaintingTaskDTO, User loginUser);
}
