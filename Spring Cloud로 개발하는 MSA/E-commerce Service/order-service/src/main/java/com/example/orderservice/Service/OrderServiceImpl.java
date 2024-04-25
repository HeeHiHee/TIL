package com.example.orderservice.Service;

import com.example.orderservice.Dto.OrderDto;
import com.example.orderservice.JPA.OrderEntity;
import com.example.orderservice.JPA.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{
    OrderRepository orderRepository;
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderDto createOrder(OrderDto orderDetails) {
        OrderEntity orderEntity = OrderEntity.builder()
                .productId(orderDetails.getProductId())
                .qty(orderDetails.getQty())
                .unitPrice(orderDetails.getUnitPrice())
                .totalPrice(orderDetails.getQty() * orderDetails.getUnitPrice())
                .userId(orderDetails.getUserId())
                .orderId(UUID.randomUUID().toString())
                .build();

        orderRepository.save(orderEntity);

        OrderDto orderDto = OrderDto.builder()
                .productId(orderEntity.getProductId())
                .qty(orderEntity.getQty())
                .unitPrice(orderEntity.getUnitPrice())
                .totalPrice(orderEntity.getTotalPrice())
                .orderId(orderEntity.getOrderId())
                .userId(orderEntity.getUserId())
                .build();

        return orderDto;
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        OrderEntity orderEntity = orderRepository.findByOrderId(orderId);
        OrderDto orderDto = OrderDto.builder()
                .productId(orderEntity.getProductId())
                .qty(orderEntity.getQty())
                .unitPrice(orderEntity.getUnitPrice())
                .totalPrice(orderEntity.getTotalPrice())
                .orderId(orderEntity.getOrderId())
                .userId(orderEntity.getUserId())
                .build();
        return orderDto;
    }

    @Override
    public Iterable<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
