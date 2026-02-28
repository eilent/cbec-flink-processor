package com.gzeport.cbec.service;

import com.gzeport.cbec.data.entity.Order;
import com.gzeport.cbec.data.repository.OrderRepository;
import com.gzeport.cbec.service.XmlParserService;
import com.gzeport.cbec.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单服务类
 * 处理订单相关的业务逻辑
 */
@Service
@Slf4j
public class OrderService implements Serializable {

    @Autowired
    private transient OrderRepository orderRepository;

    @Autowired
    private transient XmlParserService xmlParserService;

    @Autowired
    private transient ValidationService validationService;

    /**
     * 处理订单报文
     * @param xmlMessage XML格式的订单报文
     * @return 处理结果
     */
    @Transactional
    public boolean processOrderMessage(String xmlMessage) {
        try {
            // 1. 解析XML报文
            Order order = xmlParserService.parseOrderXml(xmlMessage);
            if (order == null) {
                log.error("订单报文解析失败");
                return false;
            }

            // 2. 校验订单数据
            if (!validationService.validateOrder(order)) {
                log.error("订单数据校验失败");
                return false;
            }

            // 3. 保存订单数据
            orderRepository.save(order);
            log.info("订单保存成功: {}", order.getOrderId());
            return true;
        } catch (Exception e) {
            log.error("处理订单报文异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 批量处理订单
     * @param orders 订单列表
     * @return 处理结果
     */
    @Transactional
    public boolean batchProcessOrders(List<Order> orders) {
        try {
            orderRepository.saveAll(orders);
            log.info("批量保存订单成功，共 {} 条", orders.size());
            return true;
        } catch (Exception e) {
            log.error("批量处理订单异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 批量处理订单XML报文
     * 用于Flink批量处理，将XML报文解析为订单对象并保存
     * @param orderMessages 订单XML报文列表
     * @return 处理结果
     */
    @Transactional
    public boolean batchProcessOrderMessages(List<String> orderMessages) {
        try {
            List<Order> orders = new ArrayList<>();
            int validCount = 0;
            int invalidCount = 0;

            for (String message : orderMessages) {
                // 解析XML报文
                Order order = xmlParserService.parseOrderXml(message);
                if (order != null) {
                    // 校验订单数据
                    if (validationService.validateOrder(order)) {
                        orders.add(order);
                        validCount++;
                    } else {
                        invalidCount++;
                    }
                } else {
                    invalidCount++;
                }
            }

            // 批量保存有效订单
            if (!orders.isEmpty()) {
                orderRepository.saveAll(orders);
                log.info("批量处理订单报文完成，有效: {} 条，无效: {} 条", validCount, invalidCount);
                return true;
            } else {
                log.warn("批量处理订单报文，无有效订单");
                return false;
            }
        } catch (Exception e) {
            log.error("批量处理订单报文异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 根据订单ID查询订单
     * @param orderId 订单ID
     * @return 订单实体
     */
    public Order getOrderByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    /**
     * 根据企业ID查询订单
     * @param companyId 企业ID
     * @return 订单列表
     */
    public List<Order> getOrdersByCompanyId(String companyId) {
        return orderRepository.findByCompanyId(companyId);
    }
}
