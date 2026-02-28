package com.gzeport.cbec.flink.function;

import com.gzeport.cbec.config.BatchConfig;
import com.gzeport.cbec.flink.model.KafkaMessageWithHeaders;
import com.gzeport.cbec.init.SpringContextHolder;
import com.gzeport.cbec.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.RichProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单批量处理函数
 * 用于批量处理订单数据的业务逻辑，提升大促期间的处理性能
 * 合并了业务逻辑和数据库保存处理，直接将数据保存到数据库
 * 支持处理Kafka消息的header信息
 */
@Slf4j
public class OrderProcessFunction extends RichProcessWindowFunction<KafkaMessageWithHeaders, List<String>, String, TimeWindow> implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient BatchConfig batchConfig;

    private transient OrderService orderService;

    private int batchSize;

    public OrderProcessFunction() {
        // 默认批处理大小
        this.batchSize = 100;
    }

    public OrderProcessFunction(BatchConfig batchConfig, OrderService orderService) {
        this.batchConfig = batchConfig;
        this.orderService = orderService;
        this.batchSize = batchConfig != null ? batchConfig.getSize() : 100;
    }

    /**
     * 设置批处理大小
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * 设置订单服务
     */
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 初始化方法，在Flink任务启动时执行
     * 用于获取Spring上下文和初始化依赖
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        
        // 尝试从SpringContextHolder获取OrderService（如果构造函数中未传入）
        if (orderService == null) {
            try {
                if (SpringContextHolder.isInitialized()) {
                    orderService = SpringContextHolder.getBean(OrderService.class);
                    if (orderService != null) {
                        log.info("从SpringContextHolder成功获取OrderService");
                    }
                } else {
                    log.warn("SpringContextHolder尚未初始化，OrderService可能为null");
                }
            } catch (Exception e) {
                log.error("从SpringContextHolder获取OrderService失败: {}", e.getMessage());
            }
        }
        
        // 尝试从SpringContextHolder获取BatchConfig（如果构造函数中未传入）
        if (batchConfig == null) {
            try {
                if (SpringContextHolder.isInitialized()) {
                    batchConfig = SpringContextHolder.getBean(BatchConfig.class);
                    if (batchConfig != null) {
                        this.batchSize = batchConfig.getSize();
                        log.info("从SpringContextHolder成功获取BatchConfig，批处理大小: {}", batchSize);
                    }
                }
            } catch (Exception e) {
                log.error("从SpringContextHolder获取BatchConfig失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 批量处理订单数据
     * @param key 分区键
     * @param context 窗口上下文
     * @param elements 窗口内的订单数据
     * @param out 输出收集器
     */
    @Override
    public void process(String key, Context context, Iterable<KafkaMessageWithHeaders> elements, Collector<List<String>> out) throws Exception {
        List<String> validOrders = new ArrayList<>();
        int count = 0;

        try {
            log.info("开始批量处理订单报文，批次大小: {}", batchSize);

            // 遍历窗口内的订单数据
            for (KafkaMessageWithHeaders message : elements) {
                // 1. 根据header信息进行业务逻辑判断
                /*if (validateOrderWithHeaders(message)) {
                    // 2. 转换订单数据格式
                    String processedOrder = transformOrder(message.getContent());
                    validOrders.add(processedOrder);
                    count++;

                    // 达到批处理大小，保存一批数据
                    if (count >= batchSize) {
                        saveOrders(validOrders);
                        log.info("批量处理完成一批订单，共 {} 条", count);
                        validOrders.clear();
                        count = 0;
                    }
                }*/

                    
                //TODO 显示消息的header信息和内容
                log.info("消息header信息: {}", message.getHeaders());
                log.info("消息内容: {}", message.getContent());
                count++;
            }

            // 处理剩余的订单数据
            if (!validOrders.isEmpty()) {
                saveOrders(validOrders);
                log.info("批量处理完成最后一批订单，共 {} 条", validOrders.size());
            }
            log.info("批量处理完成，共 {} 条", count);
            log.info("订单批量处理完成");
        } catch (Exception e) {
            log.error("批量处理订单异常: {}", e.getMessage());
            // 重新抛出异常，让Flink知道处理失败
            throw e;
        }
    }

    /**
     * 根据header信息校验订单数据
     * @param message 包含header信息的Kafka消息
     * @return 是否有效
     */
    private boolean validateOrderWithHeaders(KafkaMessageWithHeaders message) {
        try {
            // 1. 检查消息是否为空
            if (message == null || message.getContent() == null) {
                log.error("消息内容为空");
                return false;
            }

            // 2. 获取header信息
            String companyId = message.getCompanyId();
            String messageType = message.getMessageType();
            String senderId = message.getSenderId();

            // 3. 检查header信息
            if (companyId == null || companyId.isEmpty()) {
                log.error("公司ID为空");
                return false;
            }

            if (messageType == null || messageType.isEmpty()) {
                log.error("消息类型为空");
                return false;
            }

            if (senderId == null || senderId.isEmpty()) {
                log.error("发送者ID为空");
                return false;
            }

            // 4. 根据消息类型进行不同的业务逻辑判断
            if ("ORDER".equals(messageType)) {
                // 订单类型消息的处理逻辑
                log.debug("处理订单类型消息，公司: {}, 发送者: {}", companyId, senderId);
            } else if ("PAYMENT".equals(messageType)) {
                // 支付类型消息的处理逻辑
                log.debug("处理支付类型消息，公司: {}, 发送者: {}", companyId, senderId);
            } else if ("SHIPMENT".equals(messageType)) {
                // 物流类型消息的处理逻辑
                log.debug("处理物流类型消息，公司: {}, 发送者: {}", companyId, senderId);
            } else {
                // 未知消息类型
                log.warn("未知消息类型: {}, 公司: {}, 发送者: {}", messageType, companyId, senderId);
                return false;
            }

            // 5. 校验消息内容
            return validateOrderContent(message.getContent());
        } catch (Exception e) {
            log.error("校验订单消息异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 校验订单内容
     * @param orderData 订单数据
     * @return 是否有效
     */
    private boolean validateOrderContent(String orderData) {
        // TODO: 实现订单数据校验逻辑
        // 例如：检查必填字段、格式验证等
        return true;
    }

    /**
     * 保存订单数据到数据库
     * @param orders 订单数据列表
     * @throws Exception 保存过程中出现的异常
     */
    private void saveOrders(List<String> orders) throws Exception {
        log.info("开始批量保存订单数据，共 {} 条", orders.size());
        
        if (orderService != null) {
            boolean result = orderService.batchProcessOrderMessages(orders);
            if (result) {
                log.info("批量保存订单数据成功，共 {} 条", orders.size());
            } else {
                log.error("批量保存订单数据失败");
                // 保存失败视为致命异常，向上抛出
                throw new RuntimeException("批量保存订单数据失败");
            }
        } else {
            log.error("OrderService未初始化，无法保存订单数据");
            throw new RuntimeException("OrderService未初始化，无法保存订单数据");
        }
    }

    /**
     * 转换订单数据格式
     * @param orderData 原始订单数据
     * @return 转换后的订单数据
     */
    private String transformOrder(String orderData) {
        // TODO: 实现订单数据转换逻辑
        // 例如：将XML转换为内部对象模型
        return orderData;
    }
}
