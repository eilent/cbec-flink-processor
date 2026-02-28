package com.gzeport.cbec.init;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文持有器
 * 用于在非Spring管理的类中获取Spring上下文
 * 特别适用于Flink任务中获取Spring管理的Bean
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 获取Spring上下文
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据类型获取Bean
     * @param clazz Bean类型
     * @param <T> 泛型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称获取Bean
     * @param name Bean名称
     * @param <T> 泛型
     * @return Bean实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 检查Spring上下文是否已初始化
     * @return 是否初始化
     */
    public static boolean isInitialized() {
        return applicationContext != null;
    }
}