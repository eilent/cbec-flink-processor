# 跨境电商海量报文处理系统

## 项目简介

跨境电商海量报文处理系统是基于Spring Boot和Apache Flink构建的实时数据处理平台，专为处理和分析跨境电商场景下的海量订单数据而设计。系统通过Kafka消息队列接收订单数据，利用Flink进行实时流处理，并将处理结果存储到MySQL数据库中。

## 核心功能

- **实时流处理**：基于Apache Flink的流处理引擎，实现订单数据的实时处理
- **Kafka集成**：通过Kafka消息队列接收和处理订单数据
- **批量处理**：支持批量处理订单，提升大促期间的处理性能
- **数据存储**：与MySQL数据库集成，存储处理后的订单信息
- **REST API**：提供REST API接口，用于查询和管理订单
- **容错机制**：配置了Flink的Checkpoint和重启策略，确保系统稳定性
- **可配置性**：核心参数可通过配置文件调整，适应不同场景

## 技术栈

| 技术/框架 | 版本 | 用途 |
|---------|------|------|
| Spring Boot | 3.2.0 | 应用框架 |
| Apache Flink | 1.17.0 | 流处理引擎 |
| Kafka | 3.5.0 | 消息队列 |
| Spring Data JPA | - | 数据库访问 |
| MySQL | 8.0.33 | 数据存储 |
| Lombok | - | 代码简化 |
| SpringDoc | 2.0.2 | API文档 |

## 项目结构

```
cbec-flink-processor/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── gzeport/
│   │   │           ├── CbecFlinkProcessor.java        # 应用主入口
│   │   │           └── cbec/
│   │   │               ├── api/                        # REST API控制器
│   │   │               │   └── OrderController.java
│   │   │               ├── config/                      # 配置类
│   │   │               │   ├── ApplicationConfig.java
│   │   │               │   ├── BatchConfig.java
│   │   │               │   ├── ConfigManagement.java
│   │   │               │   ├── FlinkConfig.java
│   │   │               │   ├── KafkaConfig.java
│   │   │               │   └── SwaggerConfig.java
│   │   │               ├── data/                        # 数据层
│   │   │               │   ├── entity/                  # 实体类
│   │   │               │   │   ├── Order.java
│   │   │               │   │   ├── Payment.java
│   │   │               │   │   └── Waybill.java
│   │   │               │   └── repository/             # 仓库接口
│   │   │               │       ├── OrderRepository.java
│   │   │               │       ├── PaymentRepository.java
│   │   │               │       └── WaybillRepository.java
│   │   │               ├── flink/                       # Flink处理
│   │   │               │   ├── function/                # 处理函数
│   │   │               │   │   └── OrderProcessFunction.java
│   │   │               │   ├── model/                  # 数据模型
│   │   │               │   │   └── KafkaMessageWithHeaders.java
│   │   │               │   └── processor/              # 处理器
│   │   │               │       └── OrderProcessor.java
│   │   │               ├── init/                        # 初始化
│   │   │               │   ├── FlinkJobInitializer.java
│   │   │               │   └── SpringContextHolder.java
│   │   │               ├── kafka/                       # Kafka组件
│   │   │               │   ├── consumer/                # 消费者
│   │   │               │   │   ├── KafkaConsumerConfig.java
│   │   │               │   │   └── OrderMessageConsumer.java
│   │   │               │   └── producer/                # 生产者
│   │   │               │       ├── KafkaProducerConfig.java
│   │   │               │       └── OrderMessageProducer.java
│   │   │               └── service/                     # 业务服务
│   │   │                   ├── OrderService.java
│   │   │                   ├── ValidationService.java
│   │   │                   └── XmlParserService.java
│   │   └── resources/                                  # 资源文件
│   │       ├── application.properties                  # 配置文件
│   │       └── logback.xml                             # 日志配置
│   └── test/                                           # 测试代码
├── pom.xml                                             # Maven配置
└── README.md                                           # 项目说明
```

## 配置说明

### 数据库配置

```properties
# 数据库连接配置
spring.datasource.url=jdbc:mysql://localhost:3306/cbec_db?useSSL=false&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=eilent

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Kafka配置

```properties
# Kafka集群地址，多个地址用逗号分隔
kafka.bootstrap.servers=192.168.120.8:9092,192.168.120.9:9092,192.168.120.10:9092
# 消费者组ID，用于标识消费者组
kafka.consumer.group.id=cbec-order-group-a
# 消费者偏移量重置策略，可选值：earliest（从最早消息开始）、latest（从最新消息开始）
kafka.consumer.auto.offset.reset=earliest
# 是否自动提交偏移量，false表示由Flink管理偏移量
kafka.consumer.enable.auto.commit=false
# 订单主题名称
kafka.topic.order.name=cbec_topic_order
# 订单主题分区数
kafka.topic.order.partitions=2
# 订单主题副本因子
kafka.topic.order.replication.factor=1
```

### Flink配置

```properties
# Flink并行度，建议与Kafka主题分区数保持一致
flink.parallelism=4
# Checkpoint间隔时间（毫秒）
flink.checkpoint.interval=60000
# Checkpoint超时时间（毫秒）
flink.checkpoint.timeout=120000
# 两次Checkpoint之间的最小暂停时间（毫秒）
flink.checkpoint.min-pause-between-checkpoints=5000
# 最大并发Checkpoint数量
flink.checkpoint.max-concurrent-checkpoints=1
# 外部化Checkpoint清理策略，可选值：RETAIN_ON_CANCELLATION、DELETE_ON_CANCELLATION
flink.checkpoint.externalized-cleanup=RETAIN_ON_CANCELLATION
# 状态后端类型，可选值：hashmap、rocksdb
flink.state.backend=rocksdb
# Checkpoint存储路径
flink.state.checkpoints.dir=file:///C:/tmp/flink/checkpoints
# 是否启用增量Checkpoint
flink.state.backend.incremental=true
# 重启策略最大尝试次数
flink.restart.strategy.max-attempts=3
# 重启策略延迟时间（毫秒）
flink.restart.strategy.delay.ms=10000
# 批处理大小
flink.batch.size=100
# 窗口大小（秒）
flink.window.size.seconds=60
```

## 快速开始

### 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 5.7+
- Kafka 3.5.0+
- Flink 1.17.0+

### 构建与运行

1. **克隆项目**

```bash
git clone <项目地址>
cd cbec-flink-processor
```

2. **构建项目**

```bash
mvn clean package -DskipTests
```

3. **运行项目**

```bash
java -Dfile.encoding=UTF-8 --add-opens java.base/java.util=ALL-UNNAMED -jar target/cbec-flink-processor-1.0.0.jar
```

### API文档

项目集成了SpringDoc OpenAPI，启动后可通过以下地址访问API文档：

```
http://localhost:8081/swagger-ui.html
```

## 系统架构

### 数据流

1. **数据输入**：订单数据通过Kafka消息队列输入系统
2. **流处理**：Flink从Kafka读取数据，进行实时流处理
3. **批量处理**：使用滚动窗口进行批量处理，提升性能
4. **数据存储**：处理后的订单数据存储到MySQL数据库
5. **API访问**：通过REST API提供订单查询和管理功能

### 核心组件

- **OrderProcessor**：核心处理器，负责配置和启动Flink流处理任务
- **OrderProcessFunction**：具体的订单处理逻辑实现
- **KafkaMessageWithHeaders**：消息模型，包含消息内容和头信息
- **OrderService**：订单业务逻辑服务
- **OrderController**：REST API控制器，提供订单查询和管理接口

## 性能优化

1. **批量处理**：使用Flink的窗口机制进行批量处理，减少数据库写入次数
2. **状态管理**：使用RocksDB状态后端，提升大促时状态存储性能
3. **Checkpoint优化**：配置合理的Checkpoint间隔和超时时间，确保数据一致性
4. **并行度设置**：与Kafka主题分区数保持一致，充分利用系统资源
5. **增量Checkpoint**：启用增量Checkpoint，减少Checkpoint时间和资源消耗

## 故障处理

1. **自动重启**：配置了固定延迟重启策略，确保在出现异常时能够自动重启任务
2. **状态恢复**：通过Checkpoint机制，确保系统在故障后能够从正确的位置恢复
3. **偏移量管理**：使用Flink管理Kafka偏移量，确保数据不重复处理
4. **外部化Checkpoint**：将Checkpoint存储到外部文件系统，确保在作业取消后仍然保留

## 监控与维护

1. **日志管理**：配置了详细的日志记录，便于问题排查
2. **Flink Web UI**：通过Flink的Web UI监控作业状态和性能
3. **Kafka监控**：监控Kafka主题的消费情况和延迟
4. **数据库监控**：监控数据库连接和性能

## 扩展建议

1. **添加更多数据源**：支持从其他消息队列或数据源读取数据
2. **增加处理逻辑**：根据业务需求添加更多的处理逻辑和规则
3. **优化存储方案**：考虑使用列式存储或NoSQL数据库，提升查询性能
4. **添加实时分析**：集成实时分析和可视化工具，提供业务洞察
5. **实现高可用**：部署多实例，实现负载均衡和故障转移

## 许可证

[Apache License 2.0](LICENSE)
