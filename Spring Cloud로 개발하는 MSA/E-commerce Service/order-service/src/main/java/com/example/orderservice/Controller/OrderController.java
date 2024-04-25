package com.example.orderservice.Controller;

import com.example.orderservice.Dto.OrderDto;
import com.example.orderservice.JPA.OrderEntity;
import com.example.orderservice.Service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order-service")
public class OrderController {
    OrderService orderService;
    Environment env;
    @Autowired
    public OrderController(OrderService orderService, Environment env) {
        this.orderService = orderService;
        this.env = env;
    }

    @GetMapping("/health_check")
    public String status() {
        // 오더 서비스의 포트번호 출력으로 잘 작동하는지 확인
        return String.format("It's Working in Order Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders") // 주문생성
    public ResponseEntity<Object> createOrder(@PathVariable("userId") String userId,
                                              @RequestBody RequestOrder requestOrder) {
        try {
            OrderDto orderDto = OrderDto.builder()
                    .productId(requestOrder.getProductId())
                    .qty(requestOrder.getQty())
                    .unitPrice(requestOrder.getUnitPrice())
                    .userId(userId)
                    .build();

            OrderDto createDto = orderService.createOrder(orderDto);

            ResponseOrder responseOrder = ResponseOrder.builder()
                    .productId(createDto.getProductId())
                    .qty(createDto.getQty())
                    .unitPrice(createDto.getUnitPrice())
                    .totalPrice(createDto.getTotalPrice())
                    .orderId(createDto.getOrderId())
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
        }catch (Exception e) {
            // 오류가 발생한 경우 400 Bad Request 오류와 함께 오류 메시지 응답
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred: " + e.getMessage());
        }

    }


    @GetMapping("/{userId}/orders") // 주문조회
    public ResponseEntity<Object> getOrder(@PathVariable("userId") String userId) {
        try {
            Iterable<OrderEntity> orderEntities = orderService.getOrdersByUserId(userId);
            List<ResponseOrder> responseOrders = new ArrayList<>();
            orderEntities.forEach(v -> {
                ResponseOrder responseOrder = ResponseOrder.builder()
                        .productId(v.getProductId())
                        .qty(v.getQty())
                        .unitPrice(v.getUnitPrice())
                        .totalPrice(v.getTotalPrice())
                        .createdAt(v.getCreatedAt())
                        .orderId(v.getOrderId())
                        .build();

                responseOrders.add(responseOrder);
            });

            return ResponseEntity.status(HttpStatus.OK).body(responseOrders);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error occurred: " + e.getMessage());
        }
    }


}
