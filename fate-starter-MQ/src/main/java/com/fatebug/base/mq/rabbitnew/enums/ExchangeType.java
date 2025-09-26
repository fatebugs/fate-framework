package com.fatebug.base.mq.rabbitnew.enums;

import lombok.Getter;
import org.springframework.amqp.core.ExchangeTypes;

/**
 * 交换机类型枚举
 */
@Getter
public enum ExchangeType {
    /**
     * direct类型的行为是"先匹配, 再投送".
     * <pre>
     * 即在绑定时设定一个routing_key,
     * 消息的routing_key完全匹配时, 才会被交换器投送到绑定的队列中去.
     */
    direct(ExchangeTypes.DIRECT),

    /**
     * topic类型的行为就很像是两边都模糊匹配的direct类型.
     * <pre>
     * topic交换器通过模式匹配分配消息的路由键属性, 将路由键和某个模式进行匹配, 此时队列需要绑定到一个模式上.
     * 它将路由键和绑定键的字符串切分成单词, 这些单词之间用点隔开.
     * 它同样也会识别两个通配符:符号"#"匹配一个或多个词, 符号"*"匹配不多不少恰好1个词.
     * 因此"abc.#"能够匹配到"abc.def.ghi", 但是"abc.*" 只会匹配到"abc.def".
     */
    topic(ExchangeTypes.TOPIC),

    /**
     * fanout类型的行为是"忽略routing_key的匹配过程",
     * 即只要消息被投递到该交换器, 该交换器就会把消息投递到所有与它绑定的队列中去.
     */
    fanout(ExchangeTypes.FANOUT),

    /**
     * headers类型的交换器比较特殊, 它不依赖于routing_key来进行路由.
     * <pre>
     *     它使用消息头中的属性来进行路由, 通过匹配消息头中的键值对来决定消息的去向.
     *     这种方式允许更复杂的路由逻辑, 但也需要更多的配置和管理.
     *     例如, 可以设置一个绑定, 使得只有当消息头中包含特定键值对时, 才会将消息路由到对应的队列.
     */
    header(ExchangeTypes.HEADERS),

    ;
    private final String name;

    ExchangeType(String name) {
        this.name = name;
    }

    /**
     * 根据交换机类型获取名称
     */
    public static String getName(ExchangeType type) {
        if (type == null) {
            return null;
        }
        return type.getName();
    }

}

