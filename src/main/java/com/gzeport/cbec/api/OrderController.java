package com.gzeport.cbec.api;

import com.gzeport.cbec.kafka.producer.OrderMessageProducer;
import com.gzeport.cbec.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 订单API控制器
 * 用于接收企业发送的订单数据报文，保存至Kafka后由Flink处理
 */
@RestController
@RequestMapping("/api/orders")
@Slf4j
@Tag(name = "订单管理", description = "订单相关的API接口")
public class OrderController {

    @Autowired
    private OrderMessageProducer orderMessageProducer;

    @Autowired
    private OrderService orderService;

    /**
     * 接收企业发送的订单报文
     * 将订单数据保存至Kafka的订单主题，然后由Flink进行业务处理并保存至数据库
     * @param xmlMessage XML格式的订单报文
     * @param companyId 公司ID
     * @param messageType 消息类型
     * @param senderId 发送者ID
     * @return 处理结果
     */
    @Operation(
        summary = "提交订单",
        description = "接收企业发送的订单报文，将订单数据保存至Kafka的订单主题，然后由Flink进行业务处理并保存至数据库",
        tags = {"订单管理"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "订单提交成功，正在处理中",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "订单提交成功，正在处理中"))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "请求参数错误",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "订单报文为空"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "内部服务器错误",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "内部服务器错误"))
        )
    })
    @PostMapping("/submit")
    public ResponseEntity<String> submitOrder(
        @Parameter(
            description = "XML格式的订单报文",
            required = true,
            schema = @Schema(type = "string", example = "<order><orderId>ORDER001</orderId><companyId>COMP000001</companyId><customerName>John Doe</customerName><totalAmount>100.00</totalAmount><orderStatus>PENDING</orderStatus></order>")
        )
        @RequestBody String xmlMessage, 
        @Parameter(
            description = "公司ID",
            required = true,
            schema = @Schema(type = "string", example = "COMP000001"),
            in = ParameterIn.HEADER
        )
        @RequestHeader(value = "CompanyId", required = true) String companyId, 
        @Parameter(
            description = "消息类型",
            required = true,
            schema = @Schema(type = "string", example = "ORDER"),
            in = ParameterIn.HEADER
        )
        @RequestHeader(value = "MessageType", required = true) String messageType, 
        @Parameter(
            description = "发送者ID",
            required = true,
            schema = @Schema(type = "string", example = "SENDER001"),
            in = ParameterIn.HEADER
        )
        @RequestHeader(value = "SenderId", required = true) String senderId) {
        try {
            log.info("接收到企业订单提交请求，公司ID: {}, 消息类型: {}, 发送者ID: {}", companyId, messageType, senderId);
            
            // 验证XML格式是否正确
            if (xmlMessage == null || xmlMessage.isEmpty()) {
                return ResponseEntity.badRequest().body("订单报文为空");
            }
            
            // 验证header参数
            if (companyId == null || companyId.isEmpty()) {
                return ResponseEntity.badRequest().body("公司ID为空");
            }
            if (messageType == null || messageType.isEmpty()) {
                return ResponseEntity.badRequest().body("消息类型为空");
            }
            if (senderId == null || senderId.isEmpty()) {
                return ResponseEntity.badRequest().body("发送者ID为空");
            }
            
            // 将订单报文发送到Kafka，包含header信息
            boolean result = orderMessageProducer.sendOrderMessage(xmlMessage, companyId, messageType, senderId);
            if (result) {
                log.info("订单报文已成功发送到Kafka，等待Flink处理");
                return ResponseEntity.ok("订单提交成功，正在处理中");
            } else {
                log.error("订单报文发送到Kafka失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("订单提交失败，请重试");
            }
        } catch (Exception e) {
            log.error("处理订单提交请求异常: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("内部服务器错误");
        }
    }

    /**
     * 根据订单ID查询订单
     * @param orderId 订单ID
     * @return 订单信息
     */
    @Operation(
        summary = "查询订单",
        description = "根据订单ID查询订单信息",
        tags = {"订单管理"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "订单查询成功",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "订单不存在",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "内部服务器错误",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "内部服务器错误"))
        )
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(
        @Parameter(
            description = "订单ID",
            required = true,
            schema = @Schema(type = "string", example = "ORDER001")
        )
        @PathVariable String orderId) {
        try {
            log.info("查询订单: {}", orderId);
            var order = orderService.getOrderByOrderId(orderId);
            if (order != null) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("查询订单异常: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("内部服务器错误");
        }
    }

    /**
     * 健康检查接口
     * @return 健康状态
     */
    @Operation(
        summary = "健康检查",
        description = "检查订单服务的健康状态",
        tags = {"订单管理"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "服务健康",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Order Service is healthy"))
        )
    })
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Order Service is healthy");
    }
}
