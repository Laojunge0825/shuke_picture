package com.shuke.shukepicturebe.model.dto.picture;

import com.shuke.shukepicturebe.api.aliyun.model.CreateOutPaintingTaskRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 舒克、舒克
 * @date 2025/2/19 11:25
 * @description: AI扩图请求类 -- 创建任务
 */
@Data
public class CreatePictureOutPaintingTaskDTO implements Serializable {

    /**
     * 图片 id
     */
    private Long pictureId;

    /**
     * 扩图参数
     */
    private CreateOutPaintingTaskRequest.Parameters parameters;

    private static final long serialVersionUID = 1L;
}

