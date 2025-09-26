package com.fatebug.base.mq.rabbitnew.POJO;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class BindingVO {
    /**
     * 绑定关系的交换机名称
     */
    private String exchange;

    /**
     * 绑定关系的队列名称
     */
    private String queue;

    /**
     * 路由键，用于路由消息到特定队列
     * 在 direct 和 topic 交换机中使用
     */
    private String routingKey;


    private Map<String, Object> arguments = new HashMap<>();
}