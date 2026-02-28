package com.gzeport.cbec.data.repository;

import com.gzeport.cbec.data.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 支付单Repository
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 根据支付单ID查询支付单
     * @param paymentId 支付单ID
     * @return 支付单实体
     */
    Payment findByPaymentId(String paymentId);

    /**
     * 根据订单ID查询支付单
     * @param orderId 订单ID
     * @return 支付单列表
     */
    java.util.List<Payment> findByOrderId(String orderId);
}
