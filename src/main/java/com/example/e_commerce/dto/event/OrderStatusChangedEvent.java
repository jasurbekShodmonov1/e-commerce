package com.example.e_commerce.dto.event;

import com.example.e_commerce.entity.order.OrderStatus;

public record OrderStatusChangedEvent(
        Long orderId,
        Long userId,
        OrderStatus status) {
}
