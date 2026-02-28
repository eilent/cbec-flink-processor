package com.gzeport.cbec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 批处理配置管理类
 */
@Component
public class BatchConfig implements Serializable {
    @Value("${batch.size}")
    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
