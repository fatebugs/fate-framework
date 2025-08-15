/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.fatebug.base.security.resolver;


import com.fatebug.base.auth.util.SecurityUtils;
import com.fatebug.base.security.anno.LoginUser;
import com.fatebug.base.auth.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.validation.constraints.NotNull;

/**
 * 有@LoginUser注解的方法参数，注入当前登录用户
 */
@Component
@Slf4j
public class LoginUserResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(LoginUser.class) &&
                parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request, WebDataBinderFactory factory) {
        // 获取用户token
        String token = SecurityUtils.getToken();
        // 获取用户登录信息
        return TokenUtil.getTokenInfo(token);
    }

}
