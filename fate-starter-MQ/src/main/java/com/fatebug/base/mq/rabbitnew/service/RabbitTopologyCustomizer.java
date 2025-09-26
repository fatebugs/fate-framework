package com.fatebug.base.mq.rabbitnew.service;

import com.fatebug.base.mq.rabbitnew.POJO.RabbitTopology;

@FunctionalInterface
public interface RabbitTopologyCustomizer {
    /**
     * 自定义 RabbitMQ 拓扑结构
     *
     * @param topology RabbitMQ 拓扑结构对象
     */
    void customize(RabbitTopology topology);

}