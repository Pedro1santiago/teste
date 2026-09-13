package com.teste_backend.teste_backend.domain.order.event;

import com.teste_backend.teste_backend.domain.order.Order;

public record OrderCreatedEvent(Order order) {
}
