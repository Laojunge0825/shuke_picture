package com.shuke.shukepicturebe.api.imagesearch.sub;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.shuke.shukepicturebe.exception.BusinessException;
import com.shuke.shukepicturebe.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: GetImagePageUrlApi
 * @Description:  获取图片搜索结果的页面地址
 * @author: 舒克、舒克
 * @Date: 2025/1/2 15:14
 */
@Slf4j
public class GetImagePageUrlApi {

    public static String getImagePageUrl(String imageUrl) {
        // 1.准备请求参数   构造接口需要的参数
        Map<String,Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn","pc");
        formData.put("from","pc");
        formData.put("image_source","PC_UPLOAD_URL");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime="+uptime;

        try {
            // 2. 发送POST请求到百度图片搜索  用HuTool的HTTP请求工具
            HttpResponse response = HttpRequest.post(url)
                    .form(formData)
                    .header("Acs-token","1739507458753_1739521098053_upOjW4Jvl8dNM73bQQprz+ZsN8XUc9pJc7fFNCD6TJHrx51ZlJ3zRW8PrjMRUhPjHSM1JyGuE4yL09r2P3g6Q0ApqPoL9br1/YTXiZ1Bjj+gokUi4OX2mYvNKQfLOGWnFZ+Z1sFXWlUDb0ZEERCG2hMo7x70i6I+M28PvZSrXDTfxSq4S5Yqa01DwQ5DB5SQr747QTRYUU5qW4AgSKxsPFNG2d1F9GrTEEqb+GuwlEy8UL1UiiIYdjLFsVaBCyfkDq3XPh+lU0tbHgxk3DTQXGrYZd1E7MSqh9V94J0onXy0qbjpfA6QzFg35P4wZ6oKwq6htNu3v8dXtveaDPElkHGbH2ej3w1QpL5ZwiOw7vFca2Y05q+BNgl9+dKssl+CykDRxGmtJ4i/AJh91SRbEJHvJRpDVeaNAgHjc5gISRchaguVncsfvwqWb6AXv8aM30Cd00j+lgY68euhYUsj3w==")
                    .timeout(5000)
                    .execute();
            // 判断响应状态
            if(response.getStatus() != HttpStatus.HTTP_OK ){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"接口调用失败");
            }
            //解析响应结果
            String responseBody = response.body();
            Map<String,Object> result = JSONUtil.toBean(responseBody, Map.class);

            //3.处理响应结果
            if(result == null || !Integer.valueOf(0).equals(result.get("status"))){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"status :" + result.get("status") + "接口调用失败");
            }
            Map<String,Object> data = (Map<String, Object>) result.get("data");
            String rawUrl = (String) data.get("url");
            //对 rawUrl进行解码  有转义
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            if(searchResultUrl == null){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"未返回有效结果");
            }
            return searchResultUrl;

        } catch (Exception e) {
            log.error("搜索失败",e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"接口调用失败");
        }
    }

    public static void main(String[] args) {
        //测试以图搜图
        String imageUrl = getImagePageUrl("https://img2.baidu.com/it/u=2071896545,749187183&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=666");
        System.out.println("结果---------"+imageUrl);
    }
}
