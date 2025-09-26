package com.fatebug.base.mq.rabbit.callback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 消费者确认收到消息后，手动ack回执回调处理
 * @author fatebug
 */
@Slf4j
@Component
public class MessageConfirmCallback implements RabbitTemplate.ConfirmCallback {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("===================================================");
        log.info("消息确认机制回调函数参数信息如下:");
        log.info("ACK状态:{}",ack);
        log.info("投递失败原因:{}",cause);
        log.info("===================================================");
    }
}
