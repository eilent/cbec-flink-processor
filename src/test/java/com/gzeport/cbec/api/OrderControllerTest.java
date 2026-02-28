package com.gzeport.cbec.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 订单控制器测试类 - 使用集成测试直接调用API接口
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getBaseUrl() {
        return "http://localhost:8080/api";
    }

    @Test
    public void testSubmitOrder_Success() {
        // 准备测试数据
        String xmlMessage = "<order><orderId>ORDER001</orderId><companyId>COMP000001</companyId><customerName>John Doe</customerName><totalAmount>100.00</totalAmount><orderStatus>PENDING</orderStatus></order>";
        String companyId = "COMP000001";
        String messageType = "ORDER";
        String senderId = "SENDER001";

        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("CompanyId", companyId);
        headers.set("MessageType", messageType);
        headers.set("SenderId", senderId);

        // 创建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(xmlMessage, headers);

        // 执行测试方法 - 调用API接口
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/orders/submit",
                requestEntity,
                String.class
        );

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("订单提交成功，正在处理中", response.getBody());
    }

    @Test
    public void testSubmitOrder_EmptyMessage() {
        // 准备测试数据
        String xmlMessage = "";
        String companyId = "COMP000001";
        String messageType = "ORDER";
        String senderId = "SENDER001";

        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("CompanyId", companyId);
        headers.set("MessageType", messageType);
        headers.set("SenderId", senderId);

        // 创建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(xmlMessage, headers);

        // 执行测试方法 - 调用API接口
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/orders/submit",
                requestEntity,
                String.class
        );

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("订单报文为空", response.getBody());
    }

    @Test
    public void testSubmitOrder_EmptyCompanyId() {
        // 准备测试数据
        String xmlMessage = "<order><orderId>ORDER001</orderId><companyId>COMP000001</companyId></order>";
        String companyId = "";
        String messageType = "ORDER";
        String senderId = "SENDER001";

        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("CompanyId", companyId);
        headers.set("MessageType", messageType);
        headers.set("SenderId", senderId);

        // 创建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(xmlMessage, headers);

        // 执行测试方法 - 调用API接口
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/orders/submit",
                requestEntity,
                String.class
        );

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("公司ID为空", response.getBody());
    }

    @Test
    public void testSubmitOrder_EmptyMessageType() {
        // 准备测试数据
        String xmlMessage = "<order><orderId>ORDER001</orderId><companyId>COMP000001</companyId></order>";
        String companyId = "COMP000001";
        String messageType = "";
        String senderId = "SENDER001";

        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("CompanyId", companyId);
        headers.set("MessageType", messageType);
        headers.set("SenderId", senderId);

        // 创建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(xmlMessage, headers);

        // 执行测试方法 - 调用API接口
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/orders/submit",
                requestEntity,
                String.class
        );

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("消息类型为空", response.getBody());
    }

    @Test
    public void testSubmitOrder_EmptySenderId() {
        // 准备测试数据
        String xmlMessage = "<order><orderId>ORDER001</orderId><companyId>COMP000001</companyId></order>";
        String companyId = "COMP000001";
        String messageType = "ORDER";
        String senderId = "";

        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("CompanyId", companyId);
        headers.set("MessageType", messageType);
        headers.set("SenderId", senderId);

        // 创建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(xmlMessage, headers);

        // 执行测试方法 - 调用API接口
        ResponseEntity<String> response = restTemplate.postForEntity(
                getBaseUrl() + "/orders/submit",
                requestEntity,
                String.class
        );

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("发送者ID为空", response.getBody());
    }

    @Test
    public void testHealthCheck() {
        // 执行测试方法 - 调用API接口
        ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + "/orders/health",
                String.class
        );

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Order Service is healthy", response.getBody());
    }

    @Test
    public void testSubmitOrder_Batch_10000() {
        // 测试批量提交10000条订单数据
        int batchSize = 10000;
        long startTime = System.currentTimeMillis();
        int successCount = 0;
        int failureCount = 0;

        System.out.println("开始批量提交测试，共 " + batchSize + " 条订单");

        for (int i = 1; i <= batchSize; i++) {
            // 准备测试数据
            String orderId = "ORDER" + String.format("%05d", i);
            String companyId = "COMP" + String.format("%06d", (i % 1000) + 1); // 模拟1000家不同的公司
            String xmlMessage = "<order><orderId>" + orderId + "</orderId><companyId>" + companyId + "</companyId><customerName>Customer " + i + "</customerName><totalAmount>" + (100.00 + i) + "</totalAmount><orderStatus>PENDING</orderStatus></order>";
            String messageType = "ORDER";
            String senderId = "SENDER" + String.format("%03d", (i % 100) + 1); // 模拟100个不同的发送者

            // 创建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.set("CompanyId", companyId);
            headers.set("MessageType", messageType);
            headers.set("SenderId", senderId);

            // 创建请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(xmlMessage, headers);

            try {
                // 执行测试方法 - 调用API接口
                ResponseEntity<String> response = restTemplate.postForEntity(
                        getBaseUrl() + "/orders/submit",
                        requestEntity,
                        String.class
                );

                // 验证结果
                if (HttpStatus.OK.equals(response.getStatusCode()) && "订单提交成功，正在处理中".equals(response.getBody())) {
                    successCount++;
                } else {
                    failureCount++;
                    System.out.println("第 " + i + " 条订单提交失败: " + response.getStatusCode() + " - " + response.getBody());
                }

                // 每1000条记录打印一次进度
                if (i % 1000 == 0) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    System.out.println("已提交 " + i + " 条订单，成功: " + successCount + "，失败: " + failureCount + "，耗时: " + elapsedTime + "ms");
                }
            } catch (Exception e) {
                failureCount++;
                System.out.println("第 " + i + " 条订单提交异常: " + e.getMessage());
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("批量提交测试完成，共 " + batchSize + " 条订单");
        System.out.println("成功: " + successCount + "，失败: " + failureCount);
        System.out.println("总耗时: " + totalTime + "ms，平均每条: " + (totalTime / (double) batchSize) + "ms");

        // 验证至少95%的请求成功
        double successRate = (double) successCount / batchSize;
        System.out.println("成功率: " + (successRate * 100) + "%");
        assert successRate >= 0.95 : "批量提交成功率低于95%，实际成功率: " + (successRate * 100) + "%";
    }
}
