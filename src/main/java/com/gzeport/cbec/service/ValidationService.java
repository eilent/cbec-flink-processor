package com.gzeport.cbec.service;

import com.gzeport.cbec.data.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 验证服务类
 * 用于校验订单数据的合法性
 */
@Service
@Slf4j
public class ValidationService {

    /**
     * 校验订单数据
     * @param order 订单实体
     * @return 是否有效
     */
    public boolean validateOrder(Order order) {
        // 1. 检查订单是否为空
        if (order == null) {
            log.error("订单数据为空");
            return false;
        }

        // 2. 检查必填字段
        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            log.error("订单ID不能为空");
            return false;
        }

        if (order.getCompanyId() == null || order.getCompanyId().isEmpty()) {
            log.error("企业ID不能为空");
            return false;
        }

        // 3. 检查订单金额
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            log.error("订单金额无效");
            return false;
        }

        // 4. 检查订单状态
        if (order.getOrderStatus() == null || order.getOrderStatus().isEmpty()) {
            log.error("订单状态不能为空");
            return false;
        }

        // 5. 检查客户名称
        if (order.getCustomerName() == null || order.getCustomerName().isEmpty()) {
            log.error("客户名称不能为空");
            return false;
        }

        // 6. 检查企业ID是否合法（示例：企业ID长度为10位）
        if (order.getCompanyId().length() != 10) {
            log.error("企业ID格式错误，应为10位");
            return false;
        }

        log.info("订单数据校验通过: {}", order.getOrderId());
        return true;
    }
}
