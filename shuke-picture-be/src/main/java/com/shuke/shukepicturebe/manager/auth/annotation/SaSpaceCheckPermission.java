package com.shuke.shukepicturebe.manager.auth.annotation;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.annotation.AliasFor;
import com.shuke.shukepicturebe.manager.auth.StpKit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 舒克、舒克
 * @date 2025/5/20 15:09
 * @description  空间权限校验注解 用于校验用户是否有指定的权限
 * 注意：
 * 1. 此注解只能用于方法上，且方法必须是public修饰的
 * 2. 此注解只能用于Controller层，不能用于Service层
 *
 */
@SaCheckPermission( type = StpKit.SPACE_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Target(  { ElementType.METHOD, ElementType.TYPE })
public @interface SaSpaceCheckPermission {

    /**
     * 需要校验的权限码
     *
     * @return 需要校验的权限码
     */
    @AliasFor(annotation = SaCheckPermission.class)
    String[] value() default {};

    /**
     * 验证模式：AND | OR，默认AND
     *
     * @return 验证模式
     */
    @AliasFor(annotation = SaCheckPermission.class)
    SaMode mode() default SaMode.AND;

    /**
     * 在权限校验不通过时的次要选择，两者只要其一校验成功即可通过校验
     *
     * <p>
     * 例1：@SaCheckPermission(value="user-add", orRole="admin")，
     * 代表本次请求只要具有 user-add权限 或 admin角色 其一即可通过校验。
     * </p>
     *
     * <p>
     * 例2： orRole = {"admin", "manager", "staff"}，具有三个角色其一即可。 <br>
     * 例3： orRole = {"admin, manager, staff"}，必须三个角色同时具备。
     * </p>
     *
     * @return /
     */
    @AliasFor(annotation = SaCheckPermission.class)
    String[] orRole() default {};
}
