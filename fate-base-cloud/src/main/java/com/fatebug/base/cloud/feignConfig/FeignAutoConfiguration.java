package com.fatebug.base.cloud.feignConfig;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import feign.Feign;
import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**
 * Feign 配置注册
 *
 * @author fatebug
 **/
@AllArgsConstructor
@AutoConfiguration(before = SentinelFeignAutoConfiguration.class)
//@ConditionalOnProperty(name = "feign.sentinel.enabled")
public class FeignAutoConfiguration
{
    @Bean
    public RequestInterceptor requestInterceptor()
    {
        return new FeignRequestInterceptor();
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Feign.Builder feignSentinelBuilder(RequestInterceptor requestInterceptor) {
        return FateFeignSentinel.builder().requestInterceptor(requestInterceptor);
    }
}
