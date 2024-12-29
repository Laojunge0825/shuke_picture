package com.shuke.shukepicturebe.controller;

import com.shuke.shukepicturebe.common.BaseResponse;
import com.shuke.shukepicturebe.common.ResultUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MainController
 * @Description:
 * @author: 舒克、舒克
 * @Date: 2024/12/29 21:26
 */
@RestController
@RequestMapping("/main")
public class MainController {

    /**
     *  健康检查
     */
    @RequestMapping("/health")
    public BaseResponse<String> health(){
        return ResultUtils.success("ok");
    }
}
