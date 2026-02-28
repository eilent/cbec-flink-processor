package com.gzeport.cbec.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 * 用于设置OpenAPI文档和Swagger UI
 */
@Configuration
public class SwaggerConfig {

    /**
     * 创建OpenAPI配置
     * @return OpenAPI配置对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("跨境电商海量报文处理系统 API")
                        .version("1.0.0")
                        .description("用于接收企业发送的订单数据报文，保存至Kafka后由Flink处理的API文档")
                        .termsOfService("https://example.com/terms")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
