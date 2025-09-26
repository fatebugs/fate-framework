package com.fatebug.base.mq.rabbitnew.service.impl;

import com.fatebug.base.auto.service.AutoService;
import com.fatebug.base.auto.service.Nameable;
import com.fatebug.base.mq.rabbitnew.POJO.RabbitTopology;
import com.fatebug.base.mq.rabbitnew.service.RabbitTopologyCustomizer;

/**
 * RabbitMQ配置自定义器实现
 */
@AutoService(RabbitTopologyCustomizer.class)
public class EnumTopologyCustomizerImpl implements RabbitTopologyCustomizer, Nameable {
    @Override
    public void customize(RabbitTopology topology) {


    }

    @Override
    public String getName() {
        return "default";
    }
}
