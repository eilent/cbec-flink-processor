package com.gzeport.cbec.service;

import com.gzeport.cbec.data.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;

/**
 * XML解析服务类
 * 用于解析订单报文
 */
@Service
@Slf4j
public class XmlParserService {

    /**
     * 解析订单XML报文
     * @param xmlMessage XML格式的订单报文
     * @return 订单实体
     */
    public Order parseOrderXml(String xmlMessage) {
        try {
            // 创建DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 解析XML字符串
            Document document = builder.parse(new ByteArrayInputStream(xmlMessage.getBytes()));
            document.getDocumentElement().normalize();

            // 创建订单实体
            Order order = new Order();

            // 解析订单ID
            NodeList orderIdList = document.getElementsByTagName("orderId");
            if (orderIdList.getLength() > 0) {
                order.setOrderId(orderIdList.item(0).getTextContent());
            }

            // 解析企业ID
            NodeList companyIdList = document.getElementsByTagName("companyId");
            if (companyIdList.getLength() > 0) {
                order.setCompanyId(companyIdList.item(0).getTextContent());
            }

            // 解析客户名称
            NodeList customerNameList = document.getElementsByTagName("customerName");
            if (customerNameList.getLength() > 0) {
                order.setCustomerName(customerNameList.item(0).getTextContent());
            }

            // 解析订单金额
            NodeList totalAmountList = document.getElementsByTagName("totalAmount");
            if (totalAmountList.getLength() > 0) {
                String amountStr = totalAmountList.item(0).getTextContent();
                try {
                    order.setTotalAmount(new java.math.BigDecimal(amountStr));
                } catch (NumberFormatException e) {
                    log.error("订单金额格式错误: {}", amountStr);
                }
            }

            // 解析订单状态
            NodeList orderStatusList = document.getElementsByTagName("orderStatus");
            if (orderStatusList.getLength() > 0) {
                order.setOrderStatus(orderStatusList.item(0).getTextContent());
            }

            return order;
        } catch (Exception e) {
            log.error("解析订单XML异常: {}", e.getMessage());
            return null;
        }
    }
}
