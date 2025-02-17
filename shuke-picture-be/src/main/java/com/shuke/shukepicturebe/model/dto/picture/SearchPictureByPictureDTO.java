package com.shuke.shukepicturebe.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 以图搜图
 */
@Data
public class SearchPictureByPictureDTO implements Serializable {

    private Long pictureId;

    private static final Long serialVersionUID = 1L;
}
