package com.fatebug.base.auth.util;


import com.fatebug.base.core.constants.SysConstants;
import com.fatebug.base.auth.user.TokenInfo;
import com.fatebug.base.utils.ServletUtils;
import com.fatebug.base.utils.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * 权限获取工具类
 * 使用该工具类必须在用户信息解析器中使用
 *
 * @author fatebug
 * @see SecurityContextHolder
 * 添加信息
 */
public class SecurityUtils {

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return SecurityContextHolder.getUserId();
    }

    /**
     * 获取用户名称
     */
    public static String getUsername() {
        return SecurityContextHolder.getUserName();
    }

    /**
     * 获取用户key
     */
    public static String getUserKey() {
        return SecurityContextHolder.getUserKey();
    }


    /**
     * 获取登录用户信息
     */
    public static <T> TokenInfo<T> getLoginUser() {

        return SecurityContextHolder.getByObj(SysConstants.LOGIN_USER);
    }

    /**
     * 获取请求token
     */
    public static String getToken() {
        return getToken(Objects.requireNonNull(ServletUtils.getRequest()));
    }

    /**
     * 通过request获取请求token
     */
    public static String getToken(HttpServletRequest request) {
        // 1.从header中获取token
        String token = request.getHeader(SysConstants.ACCESS_TOKEN);
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }

        // 2.如果header中不存在token，则从参数中获取token
        token = request.getParameter(SysConstants.ACCESS_TOKEN);
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }

        // 3. 从cookie 中获取token
        token = ServletUtils.getCookieVal(request, SysConstants.ACCESS_TOKEN);

        return token;
    }


    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
