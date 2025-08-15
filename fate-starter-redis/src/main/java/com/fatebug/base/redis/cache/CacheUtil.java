package com.fatebug.base.redis.cache;

import cn.hutool.core.util.ObjectUtil;
import com.fatebug.base.core.constants.SysConstants;
import com.fatebug.base.utils.Fate;
import com.fatebug.base.utils.SpringUtils;
import com.fatebug.base.utils.reflect.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

/**
 * 缓存工具类
 */
@Slf4j
public class CacheUtil {
    private static CacheManager cacheManager;

    private static final Boolean TENANT_MODE = Boolean.FALSE;

    public static CacheManager getCacheManager() {
        if (cacheManager == null) {
            cacheManager = SpringUtils.getBean(CacheManager.class);
        }
        return cacheManager;
    }

    /**
     * 获取缓存对象
     *
     * @param cacheName  缓存名
     * @param tenantMode 租户模式
     * @return Cache
     */
    public static Cache getCache(String cacheName, Boolean tenantMode) {
        return getCacheManager().getCache(cacheName);
    }

    /**
     * 获取缓存
     *
     * @param cacheName   缓存名
     * @param keyPrefix   缓存键前缀
     * @param key         缓存键值
     * @param valueLoader 重载对象
     * @param <T>         泛型
     * @return T
     */
    @Nullable
    public static <T> T get(String cacheName, String keyPrefix, Object key, Callable<T> valueLoader) {
        return get(cacheName, keyPrefix, key, valueLoader, TENANT_MODE);
    }


    /**
     * 获取缓存
     *
     * @param cacheName   缓存名
     * @param keyPrefix   缓存键前缀
     * @param key         缓存键值
     * @param valueLoader 重载对象
     * @param tenantMode  租户模式
     * @param <T>         泛型
     * @return T
     */
    @Nullable
    public static <T> T get(String cacheName, String keyPrefix, Object key, Callable<T> valueLoader, Boolean tenantMode) {
        if (Fate.hasEmpty(cacheName, keyPrefix, key)) {
            return null;
        }
        try {
            Cache.ValueWrapper valueWrapper = getCache(cacheName, tenantMode).get(keyPrefix.concat(String.valueOf(key)));
            Object value = null;
            if (valueWrapper == null) {
                T call = valueLoader.call();
                if (ObjectUtil.isNotEmpty(call)) {
                    Field field = ReflectUtils.getFieldValue(call.getClass(), SysConstants.DB_PRIMARY_KEY);
                    boolean codeFlag=false;
                    if (ObjectUtil.isEmpty(field)){
                        field = ReflectUtils.getFieldValue(call.getClass(), SysConstants.DB_PRIMARY_KEY_CODE);
                        codeFlag=true;
                    }
                    if (!codeFlag) {
                        if (ObjectUtil.isNotEmpty(field)
                                && ObjectUtil.isEmpty(
                                ClassUtils.getMethod(
                                        call.getClass(),
                                        SysConstants.DB_PRIMARY_KEY_METHOD
                                )
                                        .invoke(call))
                        ) {
                            return null;
                        }
                    }else {
                        if (ObjectUtil.isNotEmpty(field)
                                && ObjectUtil.isEmpty(
                                ClassUtils.getMethod(
                                        call.getClass(),
                                        SysConstants.DB_PRIMARY_KEY_CODE_METHOD
                                )
                                        .invoke(call))
                        ) {
                            return null;
                        }
                    }
                    getCache(cacheName, tenantMode).put(keyPrefix.concat(String.valueOf(key)), call);
                    value = call;
                }
            } else {
                value = valueWrapper.get();
            }
            return (T) value;
        } catch (Exception ex) {
            log.error("获取缓存失败", ex);
            return null;
        }
    }

//    /**
//     * 根据租户信息格式化缓存名
//     *
//     * @param cacheName  缓存名
//     * @param tenantMode 租户模式
//     * @return String
//     */
//    public static String formatCacheName(String cacheName, Boolean tenantMode) {
//        if (!tenantMode) {
//            return cacheName;
//        }
//        return formatCacheName(cacheName, AuthUtil.getTenantId());
//    }
//    /**
//     * 根据租户信息格式化缓存名
//     *
//     * @param cacheName 缓存名
//     * @param tenantId  租户ID
//     * @return String
//     */
//    public static String formatCacheName(String cacheName, String tenantId) {
//        return (StringUtils.isBlank(tenantId) ? cacheName : tenantId.concat(StringPool.COLON).concat(cacheName));
//    }

    /**
     * 设置缓存
     *
     * @param cacheName 缓存名
     * @param keyPrefix 缓存键前缀
     * @param key       缓存键值
     * @param value     缓存值
     */
    public static void set(String cacheName, String keyPrefix, Object key, Object value) {
        set(cacheName, keyPrefix, key, value, TENANT_MODE);
    }

    /**
     * 设置缓存
     *
     * @param cacheName   缓存名
     * @param keyPrefix   缓存键前缀
     * @param key         缓存键值
     * @param value       缓存值
     * @param tenantMode  租户模式
     */
    public static void set(String cacheName, String keyPrefix, Object key, Object value, Boolean tenantMode) {
        if (Fate.hasEmpty(cacheName, keyPrefix, key)) {
            return;
        }
        getCache(cacheName, tenantMode).put(keyPrefix.concat(String.valueOf(key)), value);
    }

    /**
     * 清除缓存
     *
     * @param cacheName 缓存名
     * @param keyPrefix 缓存键前缀
     * @param key       缓存键值
     */
    public static void evict(String cacheName, String keyPrefix, Object key) {
        evict(cacheName, keyPrefix, key, TENANT_MODE);
    }

    /**
     * 清除缓存
     *
     * @param cacheName  缓存名
     * @param keyPrefix  缓存键前缀
     * @param key        缓存键值
     * @param tenantMode 租户模式
     */
    public static void evict(String cacheName, String keyPrefix, Object key, Boolean tenantMode) {
        if (Fate.hasEmpty(cacheName, keyPrefix, key)) {
            return;
        }
        getCache(cacheName, tenantMode).evict(keyPrefix.concat(String.valueOf(key)));
    }

    /**
     * 清空缓存
     *
     * @param cacheName 缓存名
     */
    public static void clear(String cacheName) {
        clear(cacheName, TENANT_MODE);
    }

    /**
     * 清空缓存
     *
     * @param cacheName  缓存名
     * @param tenantMode 租户模式
     */
    public static void clear(String cacheName, Boolean tenantMode) {
        getCache(cacheName, tenantMode).clear();
    }

//    /**
//     * 清空缓存
//     *
//     * @param cacheName 缓存名
//     * @param tenantId  租户ID
//     */
//    public static void clear(String cacheName, String tenantId) {
//        if (Fate.isEmpty(cacheName)) {
//            return;
//        }
//        getCache(cacheName, tenantId).clear();
//    }
//    /**
//     * 清空缓存
//     *
//     * @param cacheName 缓存名
//     * @param tenantIds 租户ID集合
//     */
//    public static void clear(String cacheName, List<String> tenantIds) {
//        if (Fate.isEmpty(cacheName)) {
//            return;
//        }
//        tenantIds.forEach(tenantId -> getCache(cacheName, tenantId).clear());
//    }

}
