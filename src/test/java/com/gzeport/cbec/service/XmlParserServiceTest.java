package com.gzeport.cbec.service;

import com.gzeport.cbec.data.entity.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class XmlParserServiceTest {

    private XmlParserService parserService = new XmlParserService();

    @Test
    public void testParseOrderXml_ValidXml() {
        String validXml = "<order><orderId>ORDER001</orderId><companyId>COMP000001</companyId><customerName>John Doe</customerName><totalAmount>100.00</totalAmount><orderStatus>PENDING</orderStatus></order>";
        
        Order order = parserService.parseOrderXml(validXml);
        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertNotNull(order.getCompanyId());
        assertNotNull(order.getCustomerName());
        assertNotNull(order.getTotalAmount());
        assertNotNull(order.getOrderStatus());
    }
}
