package com.gzeport.cbec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Kafka配置管理类
 */
@Component
public class KafkaConfig {
    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;
    
    @Value("${kafka.consumer.group.id}")
    private String consumerGroupId;
    
    @Value("${kafka.consumer.auto.offset.reset}")
    private String consumerAutoOffsetReset;
    
    @Value("${kafka.consumer.enable.auto.commit}")
    private boolean consumerEnableAutoCommit;
    
    @Value("${kafka.topic.order.name}")
    private String topicOrderName;
    
    @Value("${kafka.topic.order.partitions}")
    private int topicOrderPartitions;
    
    @Value("${kafka.topic.order.replication.factor}")
    private int topicOrderReplicationFactor;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getConsumerGroupId() {
        return consumerGroupId;
    }

    public void setConsumerGroupId(String consumerGroupId) {
        this.consumerGroupId = consumerGroupId;
    }

    public String getConsumerAutoOffsetReset() {
        return consumerAutoOffsetReset;
    }

    public void setConsumerAutoOffsetReset(String consumerAutoOffsetReset) {
        this.consumerAutoOffsetReset = consumerAutoOffsetReset;
    }

    public boolean isConsumerEnableAutoCommit() {
        return consumerEnableAutoCommit;
    }

    public void setConsumerEnableAutoCommit(boolean consumerEnableAutoCommit) {
        this.consumerEnableAutoCommit = consumerEnableAutoCommit;
    }

    public String getTopicOrderName() {
        return topicOrderName;
    }

    public void setTopicOrderName(String topicOrderName) {
        this.topicOrderName = topicOrderName;
    }

    public int getTopicOrderPartitions() {
        return topicOrderPartitions;
    }

    public void setTopicOrderPartitions(int topicOrderPartitions) {
        this.topicOrderPartitions = topicOrderPartitions;
    }

    public int getTopicOrderReplicationFactor() {
        return topicOrderReplicationFactor;
    }

    public void setTopicOrderReplicationFactor(int topicOrderReplicationFactor) {
        this.topicOrderReplicationFactor = topicOrderReplicationFactor;
    }
}
