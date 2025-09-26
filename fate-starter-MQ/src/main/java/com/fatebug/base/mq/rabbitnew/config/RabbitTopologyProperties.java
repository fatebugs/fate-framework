package com.fatebug.base.mq.rabbitnew.config;

import com.fatebug.base.mq.rabbitnew.POJO.BindingVO;
import com.fatebug.base.mq.rabbitnew.POJO.ExchangeVO;
import com.fatebug.base.mq.rabbitnew.POJO.QueueVO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "fatebug.mq.rabbit")
public class RabbitTopologyProperties {

    /**
     * 若为 true(默认) 且用户未提供 exchanges/queues/bindings/自定义器, 则启用内部枚举适配.
     */
    private boolean enableEnumFallback = true;

    /**
     * 交换机列表
     */
    private List<ExchangeVO> exchanges = new ArrayList<>();
    /**
     * 队列列表
     */
    private List<QueueVO> queues = new ArrayList<>();
    /**
     * 绑定列表
     */
    private List<BindingVO> bindings = new ArrayList<>();

}