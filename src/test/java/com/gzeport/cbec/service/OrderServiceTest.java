package com.gzeport.cbec.service;

import com.gzeport.cbec.data.entity.Order;
import com.gzeport.cbec.data.repository.OrderRepository;
import com.gzeport.cbec.service.XmlParserService;
import com.gzeport.cbec.service.ValidationService;
import com.gzeport.cbec.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 订单服务测试类
 */
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private XmlParserService xmlParserService;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessOrderMessage_Success() {
        // 准备测试数据
        String xmlMessage = "<order><orderId>ORDER001</orderId><companyId>COMP000001</companyId><customerName>John Doe</customerName><totalAmount>100.00</totalAmount><orderStatus>PENDING</orderStatus></order>";
        Order order = new Order();
        order.setOrderId("ORDER001");
        order.setCompanyId("COMP000001");
        order.setCustomerName("John Doe");
        order.setTotalAmount(new java.math.BigDecimal("100.00"));
        order.setOrderStatus("PENDING");

        // 模拟依赖方法的行为
        when(xmlParserService.parseOrderXml(xmlMessage)).thenReturn(order);
        when(validationService.validateOrder(order)).thenReturn(true);
        when(orderRepository.save(order)).thenReturn(order);

        // 执行测试方法
        boolean result = orderService.processOrderMessage(xmlMessage);

        // 验证结果
        assertTrue(result);
        verify(xmlParserService, times(1)).parseOrderXml(xmlMessage);
        verify(validationService, times(1)).validateOrder(order);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    public void testProcessOrderMessage_ParseFailed() {
        // 准备测试数据
        String xmlMessage = "<order><orderId>ORDER001</orderId><companyId>COMP000001</companyId></order>";

        // 模拟依赖方法的行为
        when(xmlParserService.parseOrderXml(xmlMessage)).thenReturn(null);

        // 执行测试方法
        boolean result = orderService.processOrderMessage(xmlMessage);

        // 验证结果
        assertFalse(result);
        verify(xmlParserService, times(1)).parseOrderXml(xmlMessage);
        verify(validationService, never()).validateOrder(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    public void testProcessOrderMessage_ValidationFailed() {
        // 准备测试数据
        String xmlMessage = "<order><orderId>ORDER001</orderId><companyId>COMP000001</companyId></order>";
        Order order = new Order();
        order.setOrderId("ORDER001");
        order.setCompanyId("COMP000001");

        // 模拟依赖方法的行为
        when(xmlParserService.parseOrderXml(xmlMessage)).thenReturn(order);
        when(validationService.validateOrder(order)).thenReturn(false);

        // 执行测试方法
        boolean result = orderService.processOrderMessage(xmlMessage);

        // 验证结果
        assertFalse(result);
        verify(xmlParserService, times(1)).parseOrderXml(xmlMessage);
        verify(validationService, times(1)).validateOrder(order);
        verify(orderRepository, never()).save(any());
    }
}
