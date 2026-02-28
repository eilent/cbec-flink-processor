package com.gzeport.cbec.data.repository;

import com.gzeport.cbec.data.entity.Waybill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 运单Repository
 */
@Repository
public interface WaybillRepository extends JpaRepository<Waybill, Long> {

    /**
     * 根据运单ID查询运单
     * @param waybillId 运单ID
     * @return 运单实体
     */
    Waybill findByWaybillId(String waybillId);

    /**
     * 根据订单ID查询运单
     * @param orderId 订单ID
     * @return 运单列表
     */
    java.util.List<Waybill> findByOrderId(String orderId);
}
