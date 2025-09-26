package com.fatebug.base.mq.rabbit.service;


import com.fatebug.base.mq.rabbitnew.enums.DelayQueueEnum;
import com.fatebug.base.mq.rabbitnew.enums.ExchangeEnum;
import com.fatebug.base.mq.rabbitnew.enums.QueueEnum;

/**
 * @author fatebug
 * <p>
 * 统一消息队列发送消息方法
 * <p>
 * 为了解决rabbit繁琐的配置交换机，队列，及队列绑定。
 * 实现了公共方法统一消息队列发送消息方法
 * <p>
 * 使用步骤：
 * 1 在application主方法类上开启注解@EnableMq
 * 2 配置文件配置rabbitmq host ，port virtual-host 等配置
 * 3 pom引入spring-boot-starter-amqp依赖
 * 4 自定义 交换机{@link ExchangeEnum}，普通队列{@link QueueEnum} ，延迟队列{@link DelayQueueEnum}
 * 5 发送消息 mqService.send(msg,queueEnum)
 * 6 自定义监听消息。例如@RabbitListener(queues = "payment_service_notify_dead_queue")
 */
public interface RabbitMqService {

    /**
     * 发送普通消息 （默认交换机）
     *
     * @param msg       自定义消息
     * @param queueEnum 自定义队列
     */
    void send(Object msg, QueueEnum queueEnum);


    /**
     * 发送普通消息
     *
     * @param msg          自定义消息
     * @param exchangeEnum 自定义交换机
     * @param queueEnum    自定义队列
     */
    void send(Object msg, ExchangeEnum exchangeEnum, QueueEnum queueEnum);


    /**
     * 延迟发送
     * 消息发送至延时队列中，不要消费，过期后消息进入与之绑定的死信队列中
     * 消息消费需监听死信队列
     *
     * @param msg
     * @param delayQueueEnum
     * @throws Exception
     */
    void sendDelay(Object msg, DelayQueueEnum delayQueueEnum) throws Exception;

    /**
     * 延迟发送
     *
     * @param msg
     * @param delayExchangeEnum
     * @param delayQueueEnum
     * @throws Exception
     */
    void sendDelay(Object msg, ExchangeEnum delayExchangeEnum, DelayQueueEnum delayQueueEnum) throws Exception;
}

