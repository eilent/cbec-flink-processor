package com.gzeport.cbec.flink.processor;

import com.gzeport.cbec.config.BatchConfig;
import com.gzeport.cbec.config.FlinkConfig;
import com.gzeport.cbec.config.KafkaConfig;
import com.gzeport.cbec.flink.function.OrderProcessFunction;
import com.gzeport.cbec.flink.model.KafkaMessageWithHeaders;
import com.gzeport.cbec.init.SpringContextHolder;
import com.gzeport.cbec.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 订单处理器
 * 负责配置和启动Flink流处理任务
 * 采用单例模式，确保系统中只有一个处理器实例
 * 支持批量处理，提升大促期间的处理性能
 */
@Component
@Slf4j
public class OrderProcessor implements Serializable {

    @Autowired
    private transient KafkaConfig kafkaConfig;

    @Autowired
    private transient FlinkConfig flinkConfig;

    @Autowired
    private transient BatchConfig batchConfig;

    @Autowired
    private transient OrderService orderService;

    /**
     * 启动订单处理任务
     * 初始化Flink环境，配置Kafka数据源，启动流处理
     * 支持批量处理，提升大促期间的处理性能
     */
    public void startProcessing() throws Exception {
        log.info("启动订单处理任务...");
        
        // 确保ApplicationContext已初始化
        if (SpringContextHolder.isInitialized()) {
            log.info("SpringContextHolder 已初始化");
        } else {
            log.warn("SpringContextHolder 尚未初始化");
        }
        
        // 创建Flink执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        log.info("Flink执行环境创建成功");

        // 配置并行度，与Kafka Topic分区数保持一致
        env.setParallelism(flinkConfig.getParallelism());
        log.info("设置并行度为: {}", flinkConfig.getParallelism());

        // 配置Checkpoint机制
        configureCheckpoint(env);

        // 配置状态后端
        configureStateBackend(env);

        // 配置重启策略
        configureRestartStrategy(env);

        // 读取Kafka数据
        DataStream<KafkaMessageWithHeaders> orderStream = env
                .fromSource(createKafkaSource(), WatermarkStrategy.noWatermarks(), "Kafka Source");
        log.info("Kafka数据源创建成功，开始读取订单数据");

        // 批量处理订单数据
        // 使用滚动窗口，根据配置文件处理一批数据，或达到批处理大小时处理
        orderStream
                // 按固定键分区，确保数据均匀分布
                .keyBy(element -> "fixed-key")
                // 使用滚动处理时间窗口，窗口大小从配置文件读取
                .window(TumblingProcessingTimeWindows.of(Time.seconds(flinkConfig.getWindowSizeSeconds())))
                // 应用批量处理函数（直接处理业务逻辑并保存到数据库）
                .process(new OrderProcessFunction(batchConfig, orderService));

        // 启动任务
        log.info("订单处理任务启动中...");
        env.execute("Order Processing Job");
    }

    /**
     * 创建Kafka数据源
     * 配置Kafka连接参数，确保与配置文件中的参数一致
     */
    private KafkaSource<KafkaMessageWithHeaders> createKafkaSource() {
        return KafkaSource.<KafkaMessageWithHeaders>
                builder()
                .setBootstrapServers(kafkaConfig.getBootstrapServers())
                .setTopics(kafkaConfig.getTopicOrderName())
                .setGroupId(kafkaConfig.getConsumerGroupId())
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
                .setProperty("enable.auto.commit", String.valueOf(kafkaConfig.isConsumerEnableAutoCommit()))
                .setDeserializer(new KafkaRecordDeserializationSchema<KafkaMessageWithHeaders>() {
                    @Override
                    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<KafkaMessageWithHeaders> out) {
                        String content = record.value() != null ? new String(record.value(), StandardCharsets.UTF_8) : null;
                        Map<String, String> headers = new LinkedHashMap<>();
                        
                        // 提取header信息
                        if (record.headers() != null) {
                            record.headers().forEach(header -> {
                                String key = header.key();
                                String value = new String(header.value(), StandardCharsets.UTF_8);
                                headers.put(key, value);
                            });
                        }
                        
                        out.collect(new KafkaMessageWithHeaders(content, headers));
                    }
                    
                    @Override
                    public TypeInformation<KafkaMessageWithHeaders> getProducedType() {
                        return TypeInformation.of(KafkaMessageWithHeaders.class);
                    }
                })
                .build();
    }

    /**
     * 配置Checkpoint
     * 确保数据一致性和容错性，结合JPA事务提交频率
     */
    private void configureCheckpoint(StreamExecutionEnvironment env) {
        // 启用Checkpoint
        env.enableCheckpointing(flinkConfig.getCheckpointInterval());
        log.info("启用Checkpoint，间隔: {}ms", flinkConfig.getCheckpointInterval());

        // 配置Checkpoint模式
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        checkpointConfig.setCheckpointTimeout(flinkConfig.getCheckpointTimeout());
        checkpointConfig.setMinPauseBetweenCheckpoints(flinkConfig.getCheckpointMinPauseBetweenCheckpoints());
        checkpointConfig.setMaxConcurrentCheckpoints(flinkConfig.getCheckpointMaxConcurrentCheckpoints());
        checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.valueOf(flinkConfig.getCheckpointExternalizedCleanup()));

        // 配置Checkpoint存储
        checkpointConfig.setCheckpointStorage(new FileSystemCheckpointStorage(flinkConfig.getStateCheckpointsDir()));
        log.info("Checkpoint存储路径: {}", flinkConfig.getStateCheckpointsDir());
    }

    /**
     * 配置重启策略
     * 确保在出现异常时能够自动重启任务
     */
    private void configureRestartStrategy(StreamExecutionEnvironment env) {
        // 配置固定延迟重启策略
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
                flinkConfig.getRestartStrategyMaxAttempts(), // 最多重启次数
                flinkConfig.getRestartStrategyDelayMs() // 重启间隔（毫秒）
        ));
        log.info("配置固定延迟重启策略: 最多重启{}次，间隔{}秒", 
                flinkConfig.getRestartStrategyMaxAttempts(), 
                flinkConfig.getRestartStrategyDelayMs() / 1000);
    }

    /**
     * 配置状态后端
     * 使用RocksDB提升大促时状态存储性能
     */
    private void configureStateBackend(StreamExecutionEnvironment env) {
        // 使用RocksDB状态后端
        if ("rocksdb".equals(flinkConfig.getStateBackend())) {
            try {
                RocksDBStateBackend rocksDBStateBackend = new RocksDBStateBackend(flinkConfig.getStateCheckpointsDir(), flinkConfig.isStateBackendIncremental()); // 使用配置项决定是否启用增量Checkpoint
                env.setStateBackend(rocksDBStateBackend);
                log.info("使用RocksDB状态后端，{}{}", 
                        flinkConfig.isStateBackendIncremental() ? "启用" : "禁用", 
                        "增量Checkpoint");
            } catch (Exception e) {
                log.error("配置RocksDB状态后端失败", e);
                // 失败时使用HashMapStateBackend作为后备
                env.setStateBackend(new HashMapStateBackend());
                log.info("使用HashMapStateBackend作为后备");
            }
        }
    }
}
