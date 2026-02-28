package com.gzeport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 跨境电商海量报文处理系统主入口
 */
@SpringBootApplication(scanBasePackages = "com.gzeport.cbec")
public class CbecFlinkProcessor {
    public static void main(String[] args) {
        SpringApplication.run(CbecFlinkProcessor.class, args);
    }
}