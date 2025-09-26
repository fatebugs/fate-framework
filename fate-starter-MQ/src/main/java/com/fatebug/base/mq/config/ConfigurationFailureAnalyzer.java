package com.fatebug.base.mq.config;

import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationFailureAnalyzer implements FailureAnalyzer {
    @Override
    public FailureAnalysis analyze(Throwable failure) {
        if (failure instanceof MQInitializingException) {
            return new FailureAnalysis(
                    "应用配置错误: " + failure.getMessage(),
                    "请检查RabbitMQ配置并确保所有必要的交换机和队列已正确设置",
                    failure
            );
        }
        return null;
    }
}
