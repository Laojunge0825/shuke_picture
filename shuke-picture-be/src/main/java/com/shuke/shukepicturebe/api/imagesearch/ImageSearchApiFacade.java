package com.shuke.shukepicturebe.api.imagesearch;

import com.shuke.shukepicturebe.api.imagesearch.model.ImageSearchResult;
import com.shuke.shukepicturebe.api.imagesearch.sub.GetImageFirstUrlApi;
import com.shuke.shukepicturebe.api.imagesearch.sub.GetImageListApi;
import com.shuke.shukepicturebe.api.imagesearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 *  门面模式   通过一个统一的接口来简化多个接口的调用，简化调用过程
 */
@Slf4j
public class ImageSearchApiFacade {

    public static List<ImageSearchResult> searchImage(String imageUrl){
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        return GetImageListApi.getImageList(imageFirstUrl);
    }

    public static void main(String[] args) {
        String imageUrl = "https://www.codefather.cn/logo.png";
        List<ImageSearchResult> imageSearchResults = searchImage(imageUrl);
        System.out.println("结果列表："+imageSearchResults);
    }
}
