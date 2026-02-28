package com.gzeport.cbec.data.repository;

import com.gzeport.cbec.data.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 订单Repository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 根据订单ID查询订单
     * @param orderId 订单ID
     * @return 订单实体
     */
    Order findByOrderId(String orderId);

    /**
     * 根据企业ID查询订单
     * @param companyId 企业ID
     * @return 订单列表
     */
    java.util.List<Order> findByCompanyId(String companyId);
}
