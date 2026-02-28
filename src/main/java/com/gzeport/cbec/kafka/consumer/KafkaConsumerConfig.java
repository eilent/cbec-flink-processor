package com.gzeport.cbec.kafka.consumer;

import com.gzeport.cbec.config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka消费者配置类
 */
@Configuration
public class KafkaConsumerConfig {

    @Autowired
    private KafkaConfig kafkaConfig;

    /**
     * 创建消费者工厂
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // 添加空值检查，提供默认值
        String bootstrapServers = (kafkaConfig != null && kafkaConfig.getBootstrapServers() != null) 
                ? kafkaConfig.getBootstrapServers() 
                : "localhost:9092";
                
        String groupId = "order-group";
        String autoOffsetReset = "earliest";
        boolean enableAutoCommit = false;
        
        if (kafkaConfig != null) {
            if (kafkaConfig.getConsumerGroupId() != null) {
                groupId = kafkaConfig.getConsumerGroupId();
            }
            if (kafkaConfig.getConsumerAutoOffsetReset() != null) {
                autoOffsetReset = kafkaConfig.getConsumerAutoOffsetReset();
            }
            enableAutoCommit = kafkaConfig.isConsumerEnableAutoCommit();
        }
        
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * 创建Kafka监听器容器工厂
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        
        // 添加空值检查，提供默认值
        int concurrency = 4;
        if (kafkaConfig != null) {
            concurrency = kafkaConfig.getTopicOrderPartitions();
        }
        factory.setConcurrency(concurrency);
        
        return factory;
    }
}
