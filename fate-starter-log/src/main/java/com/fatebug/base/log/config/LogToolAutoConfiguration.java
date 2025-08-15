package com.fatebug.base.log.config;


import com.fatebug.base.launch.props.FateProperties;
import com.fatebug.base.log.aspect.ApiLogAspect;
import com.fatebug.base.log.aspect.RequestLogAspect;
import com.fatebug.base.log.event.LogApiListener;
import com.fatebug.base.log.event.LogErrorListener;
import com.fatebug.base.log.service.ILogClient;
import com.fatebug.base.log.utils.ServerInfo;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * 日志工具自动配置
 */
@AutoConfiguration
@ConditionalOnWebApplication
public class LogToolAutoConfiguration {

    @Bean
    public ApiLogAspect apiLogAspect() {
        return new ApiLogAspect();
    }

    @Bean
    public RequestLogAspect requestLogAspect() {
        return new RequestLogAspect();
    }


    @Bean
    @ConditionalOnMissingBean(name = "apiLogListener")
    public LogApiListener apiLogListener(ILogClient logClient, ServerInfo serverInfo, FateProperties fateProperties) {
        return new LogApiListener(logClient, serverInfo, fateProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "logErrorListener")
    public LogErrorListener logErrorListener(ILogClient logClient, ServerInfo serverInfo, FateProperties fateProperties) {
        return new LogErrorListener(logClient, serverInfo, fateProperties);
    }


}
