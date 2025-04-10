package com.shuke.shukepicturebe.manager.auth;


import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import com.shuke.shukepicturebe.constant.UserConstant;
import com.shuke.shukepicturebe.exception.ErrorCode;
import com.shuke.shukepicturebe.exception.ThrowUtils;
import com.shuke.shukepicturebe.manager.auth.model.SpaceUserPermission;
import com.shuke.shukepicturebe.manager.auth.model.SpaceUserPermissionConstant;
import com.shuke.shukepicturebe.model.entity.Picture;
import com.shuke.shukepicturebe.model.entity.Space;
import com.shuke.shukepicturebe.model.entity.SpaceUser;
import com.shuke.shukepicturebe.model.entity.User;
import com.shuke.shukepicturebe.model.enums.SpaceTypeEnum;
import com.shuke.shukepicturebe.service.PictureService;
import com.shuke.shukepicturebe.service.SpaceService;
import com.shuke.shukepicturebe.service.SpaceUserService;
import com.shuke.shukepicturebe.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;



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

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceUserService  spaceUserService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager ;

    /**
     * 获取某个账号的权限列表
     * @param loginId  用户登录id
     * @param loginType  用户登录角色类型
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {

        // 1.校验登录类型:如果 loginType不是"space”，直接返回空权限列表,
         if( !StpKit.SPACE_TYPE.equals(loginType)){
             return Collections.emptyList();
         }
        //2.管理员权限处理:如果当前用户为管理员，直接返回管理员权限列表。
        List<String> ADMIN_PERMISSIONS = spaceUserAuthManager.getPermissionByRole("admin");

        //3.获取上下文对象:从请求中获取 spaceuserAuthcontext 上下文，检查上下文字段是否为空。如果上下文中所有字段均为空(如没有空间或图片信息)，视为公共图库操作，直接返回管理员权限列表。4.校验登录状态:通过 1oginId 获取当前登录用户信息。如果用户未登录，抛出未授权异常;否则获取用户的唯一标识 userId ，用于后续权限判断。
        SpaceUserAuthContext authContext = getAuthContextByRequest();
        if( isAllFieldsNull(authContext)) {
            return ADMIN_PERMISSIONS;
        }

        //4.校验登录状态:通过 1ogind 获取当前登录用户信息。如果用户未登录，抛出未授权异常;
        User loginUser = (User) StpKit.SPACE.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(ObjUtil.isNull(loginUser), ErrorCode.NOT_LOGIN_ERROR);

        // 否则获取用户的唯一标识 userid，用于后续权限判断。
        Long userId = loginUser.getId();

        //5.从上下文中优先获取 Spaceuser 对象:如果上下文中存在 spaceuser 对象，直接根据其角色获取权限码列表
        SpaceUser spaceUser = authContext.getSpaceUser();
        if(ObjUtil.isNotNull(spaceUser)){
            return spaceUserAuthManager.getPermissionByRole(spaceUser.getSpaceRole());
        }

        // 6.如果没有 SpaceUser 对象   通过 spaceUserId 获取空间用户信息:如果上下文中存在 spaceuserId :
        Long spaceUserId = authContext.getSpaceUserId();
        if(spaceUserId != null){
            //6.1 查询对应的 spaceuser 数据。如果未找到，抛出数据未找到异常
            SpaceUser spaceuser = spaceUserService.getById(spaceUserId);
            ThrowUtils.throwIf(ObjUtil.isNull(spaceuser), ErrorCode.NOT_FOUND_ERROR,"未找到空间用户信息");

            //6.2 校验当前登录用户是否属于该空间，如果不是，返回空权限列表。
            SpaceUser loginSpaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceuser.getUserId())
                    .eq(SpaceUser::getUserId, userId)
                   .one();
            if( loginSpaceUser == null){
                return Collections.emptyList();
            }
            //6.3 否则，根据登录用户在该空间的角色，返回相应的权限码列表。
            return spaceUserAuthManager.getPermissionByRole(loginSpaceUser.getSpaceRole());
        }

        //7.如果上下文中不存在 spaceuserId 通过 spaceId 或 pictureId 获取空间或图片信息
        Long spaceId = authContext.getSpaceId();
        if(spaceId == null){
            // 7.1 spaceId 不存在:使用 pictureId 查询图片信息，并通过图片的 spaceId 继续判断权限;
            Long pictureId = authContext.getPictureId();
            //如果 如果 pictureId和 spaceld 均为空，默认视为管理员权限。
            if( pictureId == null){
                return ADMIN_PERMISSIONS;
            }
            //7.2 查询 picture 数据。如果未找到，抛出数据未找到异常
            Picture picture = pictureService.getById(pictureId);
            ThrowUtils.throwIf(ObjUtil.isNull(picture), ErrorCode.NOT_FOUND_ERROR,"未找到图片信息");
            //7.3 获取图片的 spaceId，继续判断权限
            spaceId = picture.getSpaceId();
            //当spaceId 不存在时,图片属于公共图库，仅上传者和管理员可以操作
            if( spaceId == null){
                // 如果当前登录用户是该图片的上传者或者是管理员，返回管理员权限列表,负责返回仅允许查看的权限码
                if( picture.getUserId().equals(userId) || ADMIN_PERMISSIONS.contains("admin")){
                    return ADMIN_PERMISSIONS;
                }else{
                    return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
                }
            }
        }

        //8 如果上下文中存在spaceId,获取 space 对象并判断空间类型
        Space space = spaceService.getById(spaceId);
        ThrowUtils.throwIf(ObjUtil.isNull(space), ErrorCode.NOT_FOUND_ERROR,"未找到空间信息");

        //8.1 如果 space 是私有空间，根据当前登录用户的角色返回相应的权限码列表。
        if( space.getSpaceType() == SpaceTypeEnum.PRIVATE.getValue()){
            //8.1.1  如果当前登录用户是空间的创建者，返回管理员权限列表。负责返回空权限
            if( space.getUserId().equals(userId) || userService.isAdmin(loginUser)){
                return ADMIN_PERMISSIONS;
            }else{
                return Collections.emptyList();
            }
        }else{
            //8.1.2 如果 space 是团队空间，查询spaceUser ,并获取角色和权限码列表
             spaceUser = spaceUserService.lambdaQuery()
                    .eq(SpaceUser::getSpaceId, spaceId)
                    .eq(SpaceUser::getUserId, userId)
                    .one();
             if(spaceUser != null){
                 return spaceUserAuthManager.getPermissionByRole(spaceUser.getSpaceRole());
             }
             return Collections.emptyList();
        }

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

    /**
     * 检查对象的所有字段是否都为空
     * @param object 要检查的对象
     * @return 如果所有字段都为空，则返回 true，否则返回 false
     */

    private boolean isAllFieldsNull(Object object) {
        if (object == null) {
            return true; // 对象本身为空
        }
        // 获取所有字段并判断是否所有字段都为空
        return Arrays.stream(ReflectUtil.getFields(object.getClass()))
                // 获取字段值
                .map(field -> ReflectUtil.getFieldValue(object, field))
                // 检查是否所有字段都为空
                .allMatch(ObjectUtil::isEmpty);
    }


}
