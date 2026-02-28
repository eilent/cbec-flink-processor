package com.gzeport.cbec.flink.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Kafka消息包装类
 * 包含消息内容和header信息
 */
public class KafkaMessageWithHeaders implements Serializable {
    private static final long serialVersionUID = 1L;
    private String content;
    private Map<String, String> headers;
    
    public KafkaMessageWithHeaders() {
    }
    
    public KafkaMessageWithHeaders(String content, Map<String, String> headers) {
        this.content = content;
        this.headers = headers;
    }
    
    /**
     * 获取消息内容
     */
    public String getContent() {
        return content;
    }
    
    /**
     * 设置消息内容
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * 获取消息header信息
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    /**
     * 设置消息header信息
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    /**
     * 根据header名称获取header值
     */
    public String getHeader(String name) {
        return headers != null ? headers.get(name) : null;
    }
    
    /**
     * 获取公司ID
     */
    public String getCompanyId() {
        return getHeader("CompanyId");
    }
    
    /**
     * 获取消息类型
     */
    public String getMessageType() {
        return getHeader("MessageType");
    }
    
    /**
     * 获取发送者ID
     */
    public String getSenderId() {
        return getHeader("SenderId");
    }
}
