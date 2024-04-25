package com.example.userservice.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ResponseOrder {
    private String productId;     // 상품아이디
    private Integer qty;          // 주문수량
    private Integer unitPrice;    // 단가
    private Integer totalPrice;   // 전체가격
    private Date createdAt;       // 주문날짜

    private String orderId;
}
