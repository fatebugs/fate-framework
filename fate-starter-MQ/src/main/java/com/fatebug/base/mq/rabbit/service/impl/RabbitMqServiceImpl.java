package com.fatebug.base.mq.rabbit.service.impl;

import com.fatebug.base.mq.rabbitnew.enums.DelayQueueEnum;
import com.fatebug.base.mq.rabbitnew.enums.ExchangeEnum;
import com.fatebug.base.mq.rabbitnew.enums.QueueEnum;
import com.alibaba.fastjson2.JSON;
import com.fatebug.base.mq.rabbit.service.RabbitMqService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * rabbitmq 公共调用方法
 *
 * @author fatebug
 */
@Service
@Primary
public class RabbitMqServiceImpl implements RabbitMqService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Override
    public void send(Object msg, QueueEnum queueEnum) {
        ExchangeEnum exchangeEnum = ExchangeEnum.DEFAULT_EXCHANGE;
        this.send(msg, exchangeEnum, queueEnum);
    }

    @Override
    public void send(Object msg, ExchangeEnum exchangeEnum, QueueEnum queueEnum) {
        this.bindExchangeAndQueue(exchangeEnum, queueEnum);
        //消息处理
        Message message = MessageBuilder.withBody(JSON.toJSONBytes(msg)).build();
        rabbitTemplate.convertAndSend(exchangeEnum.getName(), queueEnum.getName(), message);
    }

    @Override
    public void sendDelay(Object msg, DelayQueueEnum delayQueueEnum) throws Exception {
        ExchangeEnum exchangeEnum = ExchangeEnum.DEFAULT_EXCHANGE;
        this.sendDelay(msg, exchangeEnum, delayQueueEnum);
    }

    @Override
    public void sendDelay(Object msg, ExchangeEnum delayExchangeEnum, DelayQueueEnum delayQueueEnum) {
        //绑定死信
        this.bindExchangeAndQueue(delayQueueEnum.getDeadExchangeEnum(), delayQueueEnum.getDeadQueueEnum());
        //绑定延迟
        this.bindDelayExchangeAndQueue(delayExchangeEnum, delayQueueEnum);

        Message message = MessageBuilder.withBody(JSON.toJSONBytes(msg)).build();
        rabbitTemplate.convertAndSend(delayExchangeEnum.getName(), delayQueueEnum.getName(), message);
    }

    /**
     * 创建交换机
     *
     * @param exchangeEnum 交换机
     * @throws Exception 异常
     */
    private void createMyExchange(ExchangeEnum exchangeEnum) throws Exception {
        switch (exchangeEnum.getType()) {
            case direct:
                rabbitAdmin.declareExchange(new DirectExchange(exchangeEnum.getName(), exchangeEnum.getDurable(), false));
                break;
            case topic:
                rabbitAdmin.declareExchange(new TopicExchange(exchangeEnum.getName(), exchangeEnum.getDurable(), false));
                break;
            case fanout:
                rabbitAdmin.declareExchange(new FanoutExchange(exchangeEnum.getName(), exchangeEnum.getDurable(), false));
                break;
            default:
                throw new Exception("请指定交换机类型");
        }

    }

    /**
     * 创建队列
     *
     * @param queueEnum 队列
     * @throws Exception 异常
     */
    private void createMyQueue(QueueEnum queueEnum) throws Exception {
        if (null != queueEnum.getArguments() && !queueEnum.getArguments().isEmpty()) {
            rabbitAdmin.declareQueue(new Queue(queueEnum.getName(), queueEnum.getDurable(), false, false, queueEnum.getArguments()));
            return;
        }
        rabbitAdmin.declareQueue(new Queue(queueEnum.getName(), queueEnum.getDurable()));
    }

    /**
     * 创建延时队列
     *
     * @param delayQueueEnum 延时队列
     * @throws Exception 异常
     */
    private void createMyDelayQueue(DelayQueueEnum delayQueueEnum) throws Exception {
        Map<String, Object> params = DelayQueueEnum.params(delayQueueEnum.getDeadExchangeEnum().getName(), delayQueueEnum.getDeadQueueEnum().getName(), delayQueueEnum.getTtl());
        Queue queue = QueueBuilder.durable(delayQueueEnum.getName()).withArguments(params).build();
        rabbitAdmin.declareQueue(queue);
    }

    /**
     * 绑定 队列与交换机
     *
     * @param exchangeEnum 交换机
     * @param queueEnum    队列
     */
    private void bindExchangeAndQueue(ExchangeEnum exchangeEnum, QueueEnum queueEnum) {

        rabbitAdmin.declareBinding(new Binding(
                queueEnum.getName(),
                Binding.DestinationType.QUEUE,
                exchangeEnum.getName(),
                queueEnum.getName(),
                null
        ));
    }


    /**
     * 绑定延时 队列 与交换机
     *
     * @param exchangeEnum   交换机
     * @param delayQueueEnum 延时队列
     */
    private void bindDelayExchangeAndQueue(ExchangeEnum exchangeEnum, DelayQueueEnum delayQueueEnum) {

        rabbitAdmin.declareBinding(new Binding(
                delayQueueEnum.getName(),
                Binding.DestinationType.QUEUE,
                exchangeEnum.getName(),
                delayQueueEnum.getName(),
                null
        ));
    }


}

