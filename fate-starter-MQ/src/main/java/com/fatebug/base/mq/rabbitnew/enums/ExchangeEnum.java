package com.fatebug.base.mq.rabbitnew.enums;

import lombok.Getter;

/**
 * 交换机
 *
 * @author fatebug
 */
@Getter
public enum ExchangeEnum {
    DEFAULT_EXCHANGE(Names.DEFAULT_EXCHANGE, ExchangeType.direct, true, "默认交换机"),

    DEFAULT_DELAY_EXCHANGE(Names.DEFAULT_DELAY_EXCHANGE, ExchangeType.direct, true, "默认延迟交换机"),

    MSG_FANOUT_EXCHANGE(Names.MSG_FANOUT_EXCHANGE , ExchangeType.fanout, true, "消息广播交换机"),

    ;

    //交换机名称
    private final String name;

    //交换机类型
    private final ExchangeType type;
    //true持久化 交换机 消息在服务重启后存在
    private final Boolean durable;
    //长时间不使用交换机系统自动删除
//        private Boolean autoDelete;
    //描述
    private final String desc;

    ExchangeEnum(String name, ExchangeType type, Boolean durable, String desc) {
        this.name = name;
        this.type = type;
        this.durable = durable;
        this.desc = desc;
    }

    public static class Names {
        public static final String DEFAULT_EXCHANGE = "default_exchange";
        public static final String DEFAULT_DELAY_EXCHANGE = "default_delay_exchange";
        public static final String DEFAULT_DEAD_EXCHANGE = "default_dead_exchange";
        public static final String MSG_FANOUT_EXCHANGE = "msg_fanout_exchange";
    }
}

