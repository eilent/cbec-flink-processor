package com.gzeport.cbec.kafka.consumer;

import com.gzeport.cbec.config.KafkaConfig;
import com.gzeport.cbec.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 订单消息消费者
 * 用于接收Kafka中的订单数据报文，然后调用OrderService进行业务处理
 */
@Component
@Slf4j
public class OrderMessageConsumer {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Autowired
    private OrderService orderService;

    /**
     * 监听订单主题
     * 当接收到订单报文时，调用OrderService进行业务处理并保存至数据库
     * @param message 订单数据报文（XML格式）
     */
    //@KafkaListener(topics = "${kafka.topic.order.name}", groupId = "${kafka.consumer.group.id}")
    public void listenOrderMessage(String message) {
        log.info("接收到Kafka订单报文: {}", message.substring(0, Math.min(message.length(), 100)) + (message.length() > 100 ? "..." : ""));
        
        try {
            // 调用OrderService处理订单报文
            boolean result = orderService.processOrderMessage(message);
            if (result) {
                log.info("订单报文处理成功并保存至数据库");
            } else {
                log.warn("订单报文处理失败");
            }
        } catch (Exception e) {
            log.error("处理订单报文异常: {}", e.getMessage());
        }
    }
}
