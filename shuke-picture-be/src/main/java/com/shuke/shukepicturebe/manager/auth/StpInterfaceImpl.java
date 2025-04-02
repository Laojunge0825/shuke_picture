package com.shuke.shukepicturebe.manager.auth;


import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author 舒克、舒克
 * @date 2025/4/2 16:34
 * @description  重写StpInterface接口   获取账号的权限列表，通过权限校验
 * 因为 getPermissionList  只提供了 loginId 和 loginType 两个参数 , 但确定一个用户是否拥有某个空间的操作权限，
 * 除了这两个之外，还需要获取空间id，图片的操作也是一样，需要图片id。
 * 编写 getAuthContextByRequest 方法 ，通过请求获取上下文对象 从而获取id，并进行区分
 */
@Service
public class StpInterfaceImpl implements StpInterface {
    @Value("${server.servlet.context-path}")
    private  String contentPath ;

    /**
     * 获取某个账号的权限列表
     * @param loginId  用户登录id
     * @param loginType  用户登录角色类型
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    /**
     * 返回某账号 拥有的所有角色用户    目前项目中没用到
     */
    @Override
    public List<String> getRoleList(Object o, String s) {
        return Collections.emptyList();
    }

    /**
     *  从请求中获取上下文对象
     */
    private SpaceUserAuthContext getAuthContextByRequest() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String contentType01  = request.getContentType();
        String contentType= request.getHeader(Header.CONTENT_TYPE.getValue());
        System.out.println("contentType01:"+contentType01 + " contentType:"+contentType);
        SpaceUserAuthContext authContext;

        // 根据请求参数区分如何获取上下文对象
        // 请求参数是JSON格式
        if(ContentType.JSON.getValue().equals(contentType)){
            String body01 = request.getParameter("body");
            String body = ServletUtil.getBody(request);
            System.out.println("body01:"+body01 + " body:"+body);
           authContext = JSONUtil.toBean(body, SpaceUserAuthContext.class);
        } else {
            // 请求参数是普通格式
            Map<String, String> paramMap = ServletUtil.getParamMap(request);
            authContext = BeanUtil.toBean(paramMap, SpaceUserAuthContext.class);
        }

        Long id = authContext.getId();
        // 根据请求路径区分 id 的含义
        if (ObjUtil.isNotNull(id)){
            // 请求全路径  /api/space/xxx
            String requestURI = request.getRequestURI();
            // 请求部分路径    space/xxx
            String partURI = requestURI.replace(contentPath+"/", "");
            // 模块名    space  获取第一个 / 前的字符串
            String moduleName = StrUtil.subBefore(partURI, "/", false);

            switch (moduleName){
                case "space":
                    authContext.setSpaceId(id);
                    break;
                case "spaceUser":
                    authContext.setSpaceUserId(id);
                    break;
                case "picture":
                    authContext.setPictureId(id);
                    break;
                default:
            }
        }

        return authContext;
    }

}
