package com.fatebug.base.security.filter;

import com.fatebug.base.core.constants.SysConstants;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

public class loginInterceptor implements HandlerInterceptor {
    @Override
    //preHandle:在方法调用前使用
    public boolean preHandle(HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        //判断用户是否登录，未登录重定向到登录页面
        if (request.getSession().getAttribute(SysConstants.ACCESS_TOKEN) == null){
            response.sendRedirect("/error/token");
            return false;
        }
        return true;
    }
}
