package com.teste_backend.teste_backend.domain.order.event;

import com.teste_backend.teste_backend.domain.order.OrderStatus;

public record OrderTransitionedEvent(Long orderId, OrderStatus status, int version) {
}
