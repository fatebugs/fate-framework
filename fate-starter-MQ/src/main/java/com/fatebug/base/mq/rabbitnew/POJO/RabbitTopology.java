package com.fatebug.base.mq.rabbitnew.POJO;

import lombok.Data;

import java.util.*;

/**
 * RabbitMQ 拓扑结构定义
 * 包含交换机、队列和绑定关系的定义
 * 提供简单的去重逻辑
 */
@Data
public class RabbitTopology {

    /**
     * 交换机定义列表
     */
    private List<ExchangeVO> exchanges = new ArrayList<>();
    /**
     * 队列定义列表
     */
    private List<QueueVO> queues = new ArrayList<>();
    /**
     * 绑定关系定义列表
     * 包含交换机和队列之间的绑定关系
     */
    private List<BindingVO> bindings = new ArrayList<>();

    /**
     * 简单去重逻辑
     */
    public void normalize() {
        Set<String> exNames = new HashSet<>();
        this.exchanges.removeIf(e -> !exNames.add(e.getName()));
        Set<String> qNames = new HashSet<>();
        this.queues.removeIf(q -> !qNames.add(q.getName()));
    }
}