package com.fatebug.base.mq.rabbitnew.POJO;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static com.fatebug.base.mq.rabbitnew.constants.RabbitConstants.DEFAULT_TTL;

/**
 * 队列定义
 */
@Data
public class QueueVO {
    /**
     * 队列名称
     */
    private String name;
    /**
     * 队列持久化标记，重启后是否保留
     * true: 持久化，false: 非持久化
     */
    private boolean durable;
    /**
     * 队列描述
     */
    private String desc;
    /**
     * 是否独占队列
     * 独占队列只能被创建它的连接使用，连接关闭后队列自动删除
     */
    private boolean exclusive;
    /**
     * 是否自动删除队列
     * 当没有消费者时，队列会自动删除
     */
    private boolean autoDelete;

    /**
     * 是否延迟队列
     * 延迟队列用于消息延迟投递
     */
    private Boolean delayed = false;

    /**
     * 队列参数
     * 可以包含消息TTL、最大长度、死信交换机等配置
     */
    public QueueArguments arguments = new QueueArguments(DEFAULT_TTL);
}