package com.shuke.shukepicturebe.manager.auth.annotation;


import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

/**
 * @author 舒克、舒克
 * @date 2025/5/20 14:43
 * @description 重写SaToken 默认的注解处理器
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    // 注册SaToken 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }
    @PostConstruct
    public void rewriteSaStrategy(){
        // 重写Sa-Token的注解处理器，增加注解合并功能
        SaAnnotationStrategy.instance.getAnnotation = AnnotatedElementUtils::getMergedAnnotation;

    }
}
