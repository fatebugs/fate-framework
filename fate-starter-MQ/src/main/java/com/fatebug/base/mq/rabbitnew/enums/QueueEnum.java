package com.fatebug.base.mq.rabbitnew.enums;

import lombok.Getter;

import java.util.Map;

/**
 * 队列
 *
 * @author fatebug
 */
@Getter
public enum QueueEnum {
    //默认空名队列
    DEFAULT_NULL_QUEUE(Names.DEFAULT_NULL_QUEUE, false, null, "默认空名队列"),

    DEFAULT_QUEUE(Names.DEFAULT_QUEUE, false, null, "默认队列"),

    ;

    /**
     * 队列名称
     */
    private final String name;
    /**
     * 持久化
     */
    private final Boolean durable;
    /**
     * 队列参数
     */
    private final Map<String, Object> arguments;
    /**
     * 描述
     */
    private final String desc;

    QueueEnum(String name, Boolean durable, Map<String, Object> arguments, String desc) {
        this.name = name;
        this.durable = durable;
        this.arguments = arguments;
        this.desc = desc;
    }

    public static class Names {
        public static final String DEFAULT_QUEUE = "default_queue";
        public static final String DEFAULT_DURABLE_QUEUE = "default_durable_queue";
        public static final String DEFAULT_DEAD_QUEUE = "default_dead_queue";
        public static final String SERVICE_NOTIFY_DEAD_QUEUE = "service_notify_dead_queue";
        public static final String DEFAULT_NULL_QUEUE = "default_null_queue";
    }
}
