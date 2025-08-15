package com.fatebug.base.launch.config;

import com.fatebug.base.launch.props.FateProperties;
import com.fatebug.base.launch.props.FatePropertySourcePostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Fate框架属性配置类
 * 用于加载和管理Fate框架的配置属性
 */
@AutoConfiguration
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(FateProperties.class)
public class PropertyConfiguration {
    @Bean
    public FatePropertySourcePostProcessor fatePropertySourcePostProcessor() {
        return new FatePropertySourcePostProcessor();
    }
}
