package com.gzeport.cbec.data.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 运单实体类
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "cbec_waybill")
public class Waybill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "waybill_id", unique = true, nullable = false)
    private String waybillId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "waybill_status")
    private String waybillStatus;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
