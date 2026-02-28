package com.gzeport.cbec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 应用配置管理类
 */
@Component
public class ApplicationConfig {
    @Value("${app.name}")
    private String name;
    
    @Value("${app.version}")
    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
