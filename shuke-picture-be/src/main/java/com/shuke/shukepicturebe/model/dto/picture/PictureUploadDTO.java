package com.shuke.shukepicturebe.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: PictureUploadDTO
 * @Description:  图片上传DTO 包括图片的修改 即重新上传
 * @author: 舒克、舒克
 * @Date: 2025/1/14 9:49
 */
@Data
public class PictureUploadDTO implements Serializable {

    /**
     * 图片 id (用于修改 重新上传)
     */
    private Long id;

    /**
     * 文件url地址
     */
    private String fileUrl;

    /**
     * 图片空间 id
     */
    private Long spaceId;

    /**
     * 图片名称
     */
    private String picName;

    private static final long serialVersionUID = 1L;
}
