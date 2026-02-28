package com.gzeport.cbec.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 配置管理类
 * 集中管理所有配置类，便于阅读和理解
 */
@Configuration
@Import({
    ApplicationConfig.class,
    BatchConfig.class,
    FlinkConfig.class,
    KafkaConfig.class
})
public class ConfigManagement {
    // 集中管理所有配置类，无需额外代码
}
