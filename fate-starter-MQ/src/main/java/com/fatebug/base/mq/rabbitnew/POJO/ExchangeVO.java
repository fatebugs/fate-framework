package com.fatebug.base.mq.rabbitnew.POJO;

import com.fatebug.base.mq.rabbitnew.enums.ExchangeType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 交换机定义
 */
@Data
public class ExchangeVO {
    /**
     * 交换机名称
     */
    private String name;
    /**
     * 交换机类型
     * 如 direct, topic, fanout, headers
     */
    private ExchangeType type;
    /**
     * 交换机持久化标记，重启后是否保留
     * true: 持久化，false: 非持久化
     */
    private Boolean durable = false;

    /**
     * 交换机描述
     */
    private String desc;

    /**
     * 是否延迟交换机
     * 延迟交换机用于消息延迟投递
     * 注意：RabbitMQ 需要额外插件支持
     */
    private Boolean delayed;

    /**
     * 自动删除标记
     *
     * @deprecated RabbitMQ 3.0 之后不推荐使用 autoDelete
     * 自动删除交换机，当没有队列绑定时自动删除
     */
    private Boolean autoDelete;

    /**
     * 是否内部交换机
     * 内部交换机通常用于 RabbitMQ 内部通信
     */
    @Deprecated
    private boolean internal;
    /**
     * 交换机的其他参数
     * 可以用于设置额外的交换机属性
     */
    private Map<String, Object> arguments = new HashMap<>();


}