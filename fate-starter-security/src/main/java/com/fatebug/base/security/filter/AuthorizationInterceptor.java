package com.fatebug.base.security.filter;

import cn.hutool.core.util.ObjectUtil;
import com.fatebug.base.auth.util.SecurityContextHolder;
import com.fatebug.base.auth.util.SecurityUtils;
import com.fatebug.base.core.constants.RedisConstant;
import com.fatebug.base.core.constants.SysConstants;
import com.fatebug.base.core.exception.AuthException;
import com.fatebug.base.utils.StringUtils;
import com.fatebug.base.redis.util.RedisUtil;
import com.fatebug.base.security.anno.*;
import com.fatebug.base.auth.user.TokenInfo;
import com.fatebug.base.auth.util.TokenUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;


/**
 * 认证信息
 *
 * @author fatebug
 */
@Component
@Slf4j
public class AuthorizationInterceptor extends loginInterceptor {

    @Resource
    private RedisUtil redisUtil;

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        NoLogin noLogin = null;
        InnerAuth innerAuth = null;
        if (handler instanceof ResourceHttpRequestHandler) {
            return true;
        }
        if (handler instanceof HandlerMethod) {
            noLogin = AnnotationUtils.findAnnotation(((HandlerMethod) handler).getBeanType(), NoLogin.class);
            if (noLogin == null) {
                noLogin = ((HandlerMethod) handler).getMethodAnnotation(NoLogin.class);
            }
            innerAuth = AnnotationUtils.findAnnotation(((HandlerMethod) handler).getBeanType(), InnerAuth.class);
            if (innerAuth == null) {
                innerAuth = ((HandlerMethod) handler).getMethodAnnotation(InnerAuth.class);
            }
        }
        // 校验非登录接口注解
        if (ObjectUtil.isNotEmpty(noLogin)) {
            return true;
        }
        //如果是内部请求，且不需要登录验证，直接放行
        if (ObjectUtil.isNotNull(innerAuth) && !innerAuth.isUser()) {
            //判断是否是外部请求，如果是外部请求，则抛出异常
            String source = request.getHeader(SysConstants.FROM_SOURCE);
            if (
//                    !SysConstants.GATEWAY_SWAGGER_REQUEST_FLAG.equals(source) &&
                    !SysConstants.INNER.equals(source)
            ) {
                throw new AuthException("没有内部访问权限，不允许访问");
            }
            return true;
        }

        // 只需要有一个注解，进行登录验证，校验token
        // 获取token
        String token = SecurityUtils.getToken(request);

        // token为空
        if (StringUtils.isEmpty(token)) {
            throw new AuthException("未登录");
        }
        if (TokenUtil.isTokenExpired(token)) {
            throw new AuthException("token已过期，请重新登录");
        }

        //获取token中的userId
        String tokenUserId = TokenUtil.getUserDetailsFromToken(token, SysConstants.TOKEN_USERID, String.class);

        //根据userId获取Redis中储存的用户信息
        TokenInfo<?> redisUser = redisUtil.getCacheObject(RedisConstant.USER_REDIS_KEY + SysConstants.LOGIN_USER + SysConstants.COLON + tokenUserId);
        if (ObjectUtil.isEmpty(redisUser)) {
            throw new AuthException("用户信息已过期，请重新登录");
        }

        // 设置token到request里，后续根据token，获取用户信息
        request.setAttribute(SysConstants.ACCESS_TOKEN, token);

        //将Header数据封装到线程变量中方便获取
        SecurityContextHolder.setUser(redisUser);
        SecurityContextHolder.setUserId(redisUser.getUserId());
        SecurityContextHolder.setUserName(redisUser.getUserName());
        SecurityContextHolder.setUserKey(redisUser.getAccessToken());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SecurityContextHolder.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
