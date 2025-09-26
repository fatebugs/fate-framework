package com.fatebug.base.mq.rabbitnew.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 延迟队列
 *
 * @author fatebug
 */
@Getter
public enum DelayQueueEnum {
    /**
     * 默认延迟队列
     * 绑定默认延迟交换机
     * 延迟时间 5000ms
     */
    DEFAULT_DELAY_QUEUE(Names.DEFAULT_DELAY_QUEUE, true, 5000L, ExchangeEnum.DEFAULT_DELAY_EXCHANGE, QueueEnum.DEFAULT_QUEUE, "默认延迟队列"),

    ;

    //队列名称
    private final String name;
    //持久化
    private final Boolean durable;
    //延迟时间ms
    private final Long ttl;
    //监听交换机
    private final ExchangeEnum deadExchangeEnum;
    //监听队列
    private final QueueEnum deadQueueEnum;
    //队列描述
    private final String desc;

    DelayQueueEnum(String name, Boolean durable, Long ttl, ExchangeEnum deadExchangeEnum, QueueEnum deadQueueEnum, String desc) {
        this.name = name;
        this.durable = durable;
        this.ttl = ttl;
        this.deadExchangeEnum = deadExchangeEnum;
        this.deadQueueEnum = deadQueueEnum;
        this.desc = desc;
    }

    /**
     * 延迟队列 绑定死信
     * 消息配置
     *
     * @param deadExchange 死信交换机
     * @param deadQueue    死信队列
     * @param ttl          延迟时间
     * @return Map 配置
     *
     */
    public static Map<String, Object> params(String deadExchange, String deadQueue, Long ttl) {
        // reply_to 队列
        Map<String, Object> map = new HashMap<>();
        //设置消息的过期时间 单位毫秒
        map.put("x-message-ttl", ttl);
        //设置附带的死信交换机
        map.put("x-dead-letter-exchange", deadExchange);
        //指定重定向的路由建 消息作废之后可以决定需不需要更改他的路由建 如果需要 就在这里指定
        map.put("x-dead-letter-routing-key", deadQueue);
        return map;
    }

    public static class Names {
        public static final String DEFAULT_DELAY_QUEUE = "default_delay_queue";
        public static final String SERVICE_NOTIFY_DELAY_QUEUE_15000 = "service_notify_delay_queue_15000";
        public static final String SERVICE_NOTIFY_DELAY_QUEUE_30000 = "service_notify_delay_queue_30000";
    }


}

