package com.fatebug.base.mq.rabbit.config;

import cn.hutool.core.util.ObjectUtil;
import com.fatebug.base.mq.rabbitnew.enums.DelayQueueEnum;
import com.fatebug.base.mq.rabbitnew.enums.ExchangeEnum;
import com.fatebug.base.mq.rabbitnew.enums.QueueEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.wildfly.common.annotation.NotNull;

import java.util.Map;

/**
 * 开启mq后，注入交换机和队列
 *
 * @author fatebug
 */
@Slf4j
public class RabbitMqBeanDefinitionRegister implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(@NotNull AnnotationMetadata importingClassMetadata, @NotNull BeanDefinitionRegistry registry) {
        log.info("====>开始创建消息队列交换机");
        //注入交换机
        for (ExchangeEnum value : ExchangeEnum.values()) {
            switch (value.getType()) {
                case topic:
                    BeanDefinition topicExchange = BeanDefinitionBuilder.genericBeanDefinition(
                            TopicExchange.class,
                                    () -> new TopicExchange(value.getName(), value.getDurable(), false)
                    )
                            .getBeanDefinition();
                    registry.registerBeanDefinition(value.getName(), topicExchange);
                    break;
                case direct:
                    BeanDefinition directExchange = BeanDefinitionBuilder.genericBeanDefinition(
                            DirectExchange.class,
                                    () -> new DirectExchange(value.getName(), value.getDurable(), false)
                    )
                            .getBeanDefinition();
                    registry.registerBeanDefinition(value.getName(), directExchange);
                    break;
                case fanout:

                    BeanDefinition fanoutExchange = BeanDefinitionBuilder.genericBeanDefinition(
                            FanoutExchange.class,
                                    () -> new FanoutExchange(value.getName(), value.getDurable(), false)
                    )
                            .getBeanDefinition();
                    registry.registerBeanDefinition(value.getName(), fanoutExchange);
                    break;
                default:
                    break;
            }
        }
        log.info("====>开始注入消息队列");
        //注入队列
        for (QueueEnum value : QueueEnum.values()) {
            //如果队列名为空，则不注入
            if (ObjectUtil.isEmpty(value.getName())) {
                continue;
            }
            BeanDefinition queue = BeanDefinitionBuilder.genericBeanDefinition(
                    Queue.class,
                    () -> new Queue(value.getName(), value.getDurable())
            )
                    .getBeanDefinition();
            registry.registerBeanDefinition(value.getName(), queue);
        }
        log.info("====>开始注入延迟队列");
        //注入延迟队列
        for (DelayQueueEnum value : DelayQueueEnum.values()) {
            Map<String, Object> params = DelayQueueEnum.params(
                    value.getDeadExchangeEnum().getName(), value.getDeadQueueEnum().getName(), value.getTtl()
            );
            BeanDefinition queue = BeanDefinitionBuilder
                    .genericBeanDefinition(Queue.class,
                            () -> QueueBuilder.durable(value.getName())
                                    .withArguments(params).build())
                    .getBeanDefinition();
            registry.registerBeanDefinition(value.getName(), queue);
        }
    }

}

