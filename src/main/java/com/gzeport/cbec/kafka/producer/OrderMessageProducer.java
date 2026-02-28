package com.gzeport.cbec.kafka.producer;

import com.gzeport.cbec.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 订单消息生产者
 * 用于将API接收到的订单数据发送到Kafka的订单主题
 */
@Slf4j
@Component
public class OrderMessageProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaConfig kafkaConfig;

    /**
     * 发送订单消息到Kafka
     * @param orderMessage 订单数据报文（XML格式）
     * @return 是否发送成功
     */
    public boolean sendOrderMessage(String orderMessage) {
        try {
            // 添加空值检查，使用默认主题名称
            String topicName = "cbec-order-topic";
            if (kafkaConfig != null && kafkaConfig.getTopicOrderName() != null) {
                topicName = kafkaConfig.getTopicOrderName();
            }
            log.info("发送订单报文到Kafka主题: {}", topicName);
            kafkaTemplate.send(topicName, orderMessage);
            log.info("订单报文发送成功");
            return true;
        } catch (Exception e) {
            log.error("发送订单报文异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 发送订单消息到Kafka，包含header信息
     * @param orderMessage 订单数据报文（XML格式）
     * @param companyId 公司ID
     * @param messageType 消息类型
     * @param senderId 发送者ID
     * @return 是否发送成功
     */
    public boolean sendOrderMessage(String orderMessage, String companyId, String messageType, String senderId) {
        try {
            // 添加空值检查，使用默认主题名称
            String topicName = "cbec-order-topic";
            if (kafkaConfig != null && kafkaConfig.getTopicOrderName() != null) {
                topicName = kafkaConfig.getTopicOrderName();
            }
            log.info("发送订单报文到Kafka主题: {}，公司ID: {}, 消息类型: {}, 发送者ID: {}", 
                    topicName, companyId, messageType, senderId);

            // 创建带有header信息的消息
            Message<String> message = MessageBuilder
                    .withPayload(orderMessage)
                    .setHeader("CompanyId", companyId)
                    .setHeader("MessageType", messageType)
                    .setHeader("SenderId", senderId)
                    .setHeader(KafkaHeaders.TOPIC, topicName)
                    .build();

            // 发送消息到Kafka
            kafkaTemplate.send(message);
            log.info("订单报文发送成功，包含header信息");
            return true;
        } catch (Exception e) {
            log.error("发送订单报文异常: {}", e.getMessage());
            return false;
        }
    }
}
