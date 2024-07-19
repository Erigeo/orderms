package com.erigeo.orderms.controller.dto;

import com.erigeo.orderms.model.Order;

import java.math.BigDecimal;

public record OrderResponse(Long orderId, Long customerId, BigDecimal totalOrder) {

public static OrderResponse fromEntity(Order entity){
    return new OrderResponse(entity.getOrderId(), entity.getCustomerId(), entity.getTotalPrice());
}
}
