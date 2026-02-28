package com.gzeport.cbec.init;

import com.gzeport.cbec.flink.processor.OrderProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Flink作业初始化器
 * 应用启动时自动启动Flink订单处理任务
 */
@Component
@Slf4j
public class FlinkJobInitializer implements ApplicationRunner {

    @Autowired
    private OrderProcessor orderProcessor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("正在启动 Flink 订单处理任务...");

        // 初始化ApplicationContext
        // 确保在Flink任务启动前完成ApplicationContext的初始化
        ApplicationContext context = SpringContextHolder.getApplicationContext();
        if (context != null) {
            log.info("ApplicationContext 初始化成功");
        } else {
            log.warn("ApplicationContext 尚未初始化，Flink任务可能会在运行时获取");
        }

        // 在新线程中启动，避免阻塞应用启动
        Thread flinkThread = new Thread(() -> {
            try {
                log.info("Flink 处理线程启动中...");
                orderProcessor.startProcessing();
                log.info("Flink 订单处理任务启动成功");
            } catch (Exception e) {
                log.error("启动 Flink 订单处理任务失败", e);
                log.error("错误详情: {}", e.getMessage());
                if (e.getCause() != null) {
                    log.error("根本原因: {}", e.getCause().getMessage());
                }
            }
        }, "Flink-Processing-Thread");
        flinkThread.setDaemon(false);
        flinkThread.start();
        log.info("Flink 订单处理任务已启动");
    }
}
