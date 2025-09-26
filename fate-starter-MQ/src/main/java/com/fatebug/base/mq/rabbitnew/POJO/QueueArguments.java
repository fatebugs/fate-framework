package com.fatebug.base.mq.rabbitnew.POJO;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.QueueBuilder;

/**
 * RabbitMQ 队列参数
 */
@Data
public class QueueArguments {
    /**
     * 消息过期时间，单位毫秒
     */
    @JSONField(name = "x-message-ttl")
    @NotNull
    private Long xMessageTtl;

    /**
     * 死信交换机
     */
    @JSONField(name = "x-dead-letter-exchange")
    private String xDeadLetterExchange;

    /**
     * 死信路由键
     *
     */
    @JSONField(name = "x-dead-letter-routing-key")
    private String xDeadLetterRoutingKey;

    /**
     * 队列最大长度
     */
    @JSONField(name = "x-max-length")
    private Integer xMaxLength;

    /**
     * 队列最大字节数
     */
    @JSONField(name = "x-max-length-bytes")
    private Integer xMaxLengthBytes;

}
