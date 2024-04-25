package com.example.orderservice.Service;

import com.example.orderservice.Dto.OrderDto;
import com.example.orderservice.JPA.OrderEntity;

public interface OrderService {
    OrderDto createOrder(OrderDto orderDetails);
    OrderDto getOrderByOrderId(String orderId);
    Iterable<OrderEntity> getOrdersByUserId(String userId);
}
