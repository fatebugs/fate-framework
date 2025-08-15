package com.fatebug.base.security.autoConfigure;

import com.fatebug.base.security.filter.AuthorizationInterceptor;
import com.fatebug.base.security.resolver.LoginUserResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.Resource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.List;


/**
 * SpringMVC 配置
 *
 * @author fatebug
 */
@Slf4j
@AutoConfiguration
public class WebMvcConfiguration implements WebMvcConfigurer {


    @Resource
    private LoginUserResolver loginUserResolver;
    /**
     * Token参数解析
     *
     * @param argumentResolvers 解析类
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //注入用户信息
        argumentResolvers.add(loginUserResolver);
        log.info("=======>添加用户信息参数解析器");
//        argumentResolvers.removeIf(resolver -> resolver.getClass().isAssignableFrom(WebMvcConfigurer.class));
    }

    /**
     * token 拦截器
     */
    @Resource
    private AuthorizationInterceptor authorizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/error/**",
                        "/v3/api-docs/**"
                );
        log.info("=======>添加登录鉴权拦截器");
    }
}
