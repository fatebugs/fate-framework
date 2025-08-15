package com.fatebug.base.redis.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author fatebug
 * @since 2020/11/09
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "fate.cache-manager")
public class CacheManagerProperties {
    private List<CacheConfig> configs;

    @Data
    public static class CacheConfig {
        /**
         * cache key
         */
        private String key;
        /**
         * 过期时间，sec
         */
        private long second = 60;
    }
}
