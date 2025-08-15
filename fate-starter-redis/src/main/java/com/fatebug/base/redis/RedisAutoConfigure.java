package com.fatebug.base.redis;

import com.fatebug.base.redis.lock.RedisDistributedLock;
import com.fatebug.base.redis.properties.CacheManagerProperties;
import com.fatebug.base.redis.template.RedisRepository;
import com.fatebug.base.redis.util.FastJsonRedisSerializer;
import com.fatebug.base.redis.util.MyStringRedisTemplate;
import com.fatebug.base.redis.util.RedisObjectSerializer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * redis 配置类
 *
 * @author fatebug
 */
@EnableConfigurationProperties({RedisProperties.class, CacheManagerProperties.class})
@EnableCaching
@Configuration
@Slf4j
@SuppressWarnings("unchecked")
public class RedisAutoConfigure {
    @Resource
    private CacheManagerProperties cacheManagerProperties;

    @Bean
    @ConditionalOnMissingBean(RedisDistributedLock.class)
    public RedisDistributedLock redisDistributedLock(RedisTemplate<String, Object> redisTemplate) {
        return new RedisDistributedLock(redisTemplate);
    }


    /**
     * RedisTemplate<String, String>配置
     */
    @Bean
    public MyStringRedisTemplate myStringRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(Object.class);

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.afterPropertiesSet();
        return new MyStringRedisTemplate(redisTemplate);
    }

    /**
     * RedisTemplate配置
     *
     * @param factory
     */
//    @Bean("redisTemp")
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        log.info("RedisTemplate init");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        RedisSerializer stringSerializer = new StringRedisSerializer();
        RedisSerializer redisObjectSerializer = new RedisObjectSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(redisObjectSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redis repository redis repository.
     *
     * @param redisTemplate the redis template
     * @return the redis repository
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisRepository redisRepository(RedisTemplate<String, Object> redisTemplate) {
        return new RedisRepository(redisTemplate);
    }

    /**
     * 配置redis缓存管理器
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "cacheManager")
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 生成一个缓存配置， 设置缓存的默认过期时间(有效期一小时)，也是使用Duration设置
        RedisCacheConfiguration redisCacheConfiguration = getDefConf().entryTtl(Duration.ofHours(1));

        //自定义的缓存过期时间配置
        int configSize = cacheManagerProperties.getConfigs() == null ? 0 : cacheManagerProperties.getConfigs().size();
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>(configSize);
        if (configSize > 0) {
            cacheManagerProperties.getConfigs().forEach(e -> {
                RedisCacheConfiguration conf = getDefConf().entryTtl(Duration.ofSeconds(e.getSecond()));
                redisCacheConfigurationMap.put(e.getKey(), conf);
            });
        }

        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(redisCacheConfigurationMap)
                .build();
    }

    private RedisCacheConfiguration getDefConf() {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .computePrefixWith(
                        cacheName -> "cache".concat(":").concat(cacheName).concat(":")
                )
                .serializeKeysWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                ).serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair.fromSerializer((new RedisObjectSerializer()))
                );
    }
}
