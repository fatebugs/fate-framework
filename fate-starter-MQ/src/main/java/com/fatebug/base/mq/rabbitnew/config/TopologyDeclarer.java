package com.fatebug.base.mq.rabbitnew.config;

import com.fatebug.base.mq.config.MQInitializingException;
import com.fatebug.base.mq.rabbitnew.POJO.*;
import com.fatebug.base.utils.Fate;
import com.fatebug.base.utils.bean.BeanUtil;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.HashMap;
import java.util.Set;

import static com.fatebug.base.mq.rabbitnew.constants.RabbitConstants.DEFAULT_TTL;

public class TopologyDeclarer implements SmartInitializingSingleton {

    private final RabbitTopology topology;
    private final RabbitAdmin rabbitAdmin;

    public TopologyDeclarer(RabbitTopology topology, RabbitAdmin rabbitAdmin) {
        this.topology = topology;
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public void afterSingletonsInstantiated() {
        topology.normalize();
        // 声明交换机
        if (topology.getExchanges() == null || topology.getExchanges().isEmpty()) {
            return; // 如果没有交换机定义，直接返回
        }
        for (ExchangeVO vo : topology.getExchanges()) {
            ExchangeBuilder exchangeBuilder =
                    switch (vo.getType()) {
                        case direct -> ExchangeBuilder
                                .directExchange(vo.getName());
                        case topic -> ExchangeBuilder
                                .topicExchange(vo.getName());
                        case fanout -> ExchangeBuilder
                                .fanoutExchange(vo.getName());
                        case header -> ExchangeBuilder
                                .headersExchange(vo.getName());
                    };
            exchangeBuilder
                    .withArguments(vo.getArguments());
            rabbitAdmin.declareExchange(exchangeBuilder.build());
        }
        // 声明队列
        for (QueueVO vo : topology.getQueues()) {
            Queue queue = new Queue(vo.getName(), vo.isDurable(), vo.isExclusive(), vo.isAutoDelete(), BeanUtil.beanToMapDeep(vo.getArguments()));
            rabbitAdmin.declareQueue(queue);
        }

        // 绑定交换机与队列
        if (topology.getBindings() == null || topology.getBindings().isEmpty()) {
            return; // 如果没有绑定关系定义，直接返回
        }
//        for (BindingVO vo : topology.getBindings()) {
//            Binding binding = new Binding(
//                    vo.getQueue().getName(),
//                    Binding.DestinationType.QUEUE,
//                    vo.getExchange().getName(),
//                    vo.getRoutingKey(),
//                    vo.getArguments()
//            );
//            rabbitAdmin.declareBinding(binding);
//        }


        // 注册交换机
        for (ExchangeVO vo : topology.getExchanges()) {
            AbstractExchange exchange = null;
            switch (vo.getType()) {
                case topic:
                    exchange = new TopicExchange(vo.getName(), vo.getDurable(), vo.getAutoDelete(), vo.getArguments());
                    break;
                case direct:
                    exchange = new DirectExchange(vo.getName(), vo.getDurable(), vo.getAutoDelete(), vo.getArguments());
                    break;
                case fanout:
                    exchange = new FanoutExchange(vo.getName(), vo.getDurable(), vo.getAutoDelete(), vo.getArguments());
                    break;
                case header:
                    exchange = new HeadersExchange(vo.getName(), vo.getDurable(), vo.getAutoDelete(), vo.getArguments());
                    break;
                default:
                    break;
            }
            if (Fate.isNotEmpty(exchange)) {
                rabbitAdmin.declareExchange(exchange);
            }
        }
        // 注册队列
        for (QueueVO vo : topology.getQueues()) {
            QueueArguments arguments = vo.getArguments();
            if (vo.getDelayed()) {
                // 延迟队列，判断参数是否存在
                if (Fate.isEmpty(arguments.getXMessageTtl())) {
                    arguments.setXMessageTtl(DEFAULT_TTL);
                }
                if (Fate.isEmpty(arguments.getXDeadLetterExchange())) {
                    throw new MQInitializingException("延迟队列必须设置死信交换机");
                }
                Set<Declarable> manualDeclarableSet = rabbitAdmin.getManualDeclarableSet();
                boolean exchangeExists = manualDeclarableSet.stream()
                        .filter(declarable -> declarable instanceof Exchange)
                        .map(declarable -> (Exchange) declarable)
                        .anyMatch(exchange -> exchange.getName().equals(vo.getArguments().getXDeadLetterExchange()));
                if (!exchangeExists) {
                    throw new MQInitializingException("延迟队列:" + vo.getName() + "所设置的死信交换机:" + vo.getArguments().getXDeadLetterExchange() + "不存在");
                }
                Queue queue = new Queue(vo.getName(), vo.isDurable(), vo.isExclusive(), vo.isAutoDelete(), BeanUtil.beanToMapDeep(arguments));
                rabbitAdmin.declareQueue(queue);

                Binding binding = new Binding(
                        queue.getName(),
                        Binding.DestinationType.QUEUE,
                        vo.getArguments().getXDeadLetterExchange(),
                        Fate.isEmpty(vo.getArguments().getXDeadLetterRoutingKey()) ? "" : vo.getArguments().getXDeadLetterRoutingKey(),
                        new HashMap<>()
                        );
                rabbitAdmin.declareBinding(binding);
                continue;
            }
            Queue queue = new Queue(vo.getName(), vo.isDurable(), vo.isExclusive(), vo.isAutoDelete(), BeanUtil.beanToMapDeep(arguments));
            rabbitAdmin.declareQueue(queue);
        }

        for (BindingVO binding : topology.getBindings()) {
            // 校验交换机和队列是否存在
            if (Fate.isEmpty(binding.getExchange()) || Fate.isEmpty(binding.getQueue())) {
                throw new MQInitializingException("绑定关系必须指定交换机和队列");
            }

            Set<Declarable> manualDeclarableSet = rabbitAdmin.getManualDeclarableSet();
            boolean exchangeExists = manualDeclarableSet.stream()
                    .filter(declarable -> declarable instanceof Exchange)
                    .map(declarable -> (Exchange) declarable)
                    .anyMatch(exchange -> exchange.getName().equals(binding.getExchange()));
            if (!exchangeExists) {
                throw new MQInitializingException("绑定关系所指定的交换机:" + binding.getExchange() + "不存在");
            }
            boolean queueExists = manualDeclarableSet.stream()
                    .filter(declarable -> declarable instanceof Queue)
                    .map(declarable -> (Queue) declarable)
                    .anyMatch(queue -> queue.getName().equals(binding.getQueue()));
            if (!queueExists) {
                throw new MQInitializingException("绑定关系所指定的队列:" + binding.getQueue() + "不存在");
            }
            Binding bind = new Binding(
                    binding.getQueue(),
                    Binding.DestinationType.QUEUE,
                    binding.getExchange(),
                    binding.getRoutingKey() == null ? "" : binding.getRoutingKey(),
                    binding.getArguments() == null ? new HashMap<>() : binding.getArguments()
            );
            rabbitAdmin.declareBinding(bind);
        }


    }
}