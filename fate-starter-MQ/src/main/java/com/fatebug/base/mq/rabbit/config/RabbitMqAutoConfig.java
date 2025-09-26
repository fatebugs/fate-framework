package com.fatebug.base.mq.rabbit.config;

import com.fatebug.base.mq.rabbitnew.POJO.BindingVO;
import com.fatebug.base.mq.rabbitnew.POJO.ExchangeVO;
import com.fatebug.base.mq.rabbitnew.POJO.QueueVO;
import com.fatebug.base.mq.rabbitnew.POJO.RabbitTopology;
import com.fatebug.base.mq.rabbitnew.config.RabbitTopologyProperties;
import com.fatebug.base.mq.rabbitnew.config.TopologyDeclarer;
import com.fatebug.base.mq.rabbitnew.service.RabbitTopologyCustomizer;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.ServiceLoader;


/**
 * Date: 2023/1/5
 * Time: 16:43
 *
 * @author FateBug
 */
@Configuration
@EnableConfigurationProperties(RabbitTopologyProperties.class)
@ConditionalOnProperty(name = "fatebug.mq.rabbit.enable", havingValue = "true")
@Import({RabbitMqBeanDefinitionRegister.class, RabbitTopologyProperties.class})
public class RabbitMqAutoConfig {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 注册 RabbitAdmin
     * RabbitAdmin 是 RabbitMQ 的管理工具类，可以用来声明交换机、队列、绑定等。
     * 它会在应用启动时自动创建，并且可以通过 RabbitTemplate 进行消息发送和接收等操作。
     */
    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(rabbitTemplate);
    }


    @Bean
    public TopologyDeclarer topologyDeclarer(RabbitTopologyProperties rabbitTopologyProperties, RabbitAdmin rabbitAdmin) {
        List<ExchangeVO> exchanges = rabbitTopologyProperties.getExchanges();

        List<QueueVO> queues = rabbitTopologyProperties.getQueues();

        List<BindingVO> bindings = rabbitTopologyProperties.getBindings();

        RabbitTopology topology = new RabbitTopology();
        topology.setExchanges(exchanges);
        topology.setQueues(queues);
        topology.setBindings(bindings);

        //加载自定义适配器
        ServiceLoader<RabbitTopologyCustomizer> customizerServiceLoader = ServiceLoader.load(RabbitTopologyCustomizer.class);
        if (rabbitTopologyProperties.isEnableEnumFallback()) {
            //如果启用枚举回退，则添加默认的枚举适配器
            customizerServiceLoader.stream()
                    .filter(provider -> "default".equals(provider.type().getName()))
                    .findFirst()
                    .ifPresent(provider -> provider.get().customize(topology));
        }
        customizerServiceLoader.stream()
                .filter(provider -> !"default".equals(provider.type().getName()))
                .forEach(provider -> provider.get().customize(topology));
        return new TopologyDeclarer(topology, rabbitAdmin);
    }


}
