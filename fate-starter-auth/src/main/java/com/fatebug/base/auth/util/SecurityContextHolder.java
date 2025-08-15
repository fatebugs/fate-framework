package com.fatebug.base.auth.util;

import cn.hutool.core.convert.Convert;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.fatebug.base.core.constants.SysConstants;
import com.fatebug.base.auth.user.TokenInfo;
import com.fatebug.base.utils.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取当前线程变量中的 用户id、用户名称、Token等信息 注意： 必须在网关通过请求头的方法传入，同时在HeaderInterceptor拦截器设置值。 否则这里无法获取
 *
 * @author fatebug
 */
public class SecurityContextHolder {
    private static final TransmittableThreadLocal<Map<String, Object>> THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = getLocalMap();
        map.put(key, value == null ? StringUtils.EMPTY : value);
    }

    public static String get(String key) {
        Map<String, Object> map = getLocalMap();
        return Convert.toStr(map.getOrDefault(key, StringUtils.EMPTY));
    }

    public static <T> T getByObj(String key) {
        Map<String, Object> map = getLocalMap();
        return StringUtils.cast(map.getOrDefault(key, null));
    }

    public static Map<String, Object> getLocalMap() {
        Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap<String, Object>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, Object> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }

    public static Long getUserId() {
        return Convert.toLong(get(SysConstants.TOKEN_USERID), 0L);
    }

    public static void setUserId(Long account) {
        set(SysConstants.TOKEN_USERID, account);
    }

    public static String getUserName() {
        return get(SysConstants.TOKEN_USERNAME);
    }

    public static void setUserName(String username) {
        set(SysConstants.TOKEN_USERNAME, username);
    }

    public static String getUserKey() {
        return get(SysConstants.ACCESS_TOKEN);
    }

    public static void setUserKey(String userKey) {
        set(SysConstants.ACCESS_TOKEN, userKey);
    }

    public static String getPermission() {
        return get(SysConstants.ROLE_PERMISSION);
    }

    public static void setPermission(String permissions) {
        set(SysConstants.ROLE_PERMISSION, permissions);
    }

    public static void setUser(TokenInfo<?> user) {
        set(SysConstants.LOGIN_USER, user);
    }

    public static <T> TokenInfo<T> getUser() {
        return getByObj(SysConstants.LOGIN_USER);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }
}
