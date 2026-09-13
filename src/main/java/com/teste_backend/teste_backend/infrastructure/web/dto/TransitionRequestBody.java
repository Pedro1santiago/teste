package com.teste_backend.teste_backend.infrastructure.web.dto;

import com.teste_backend.teste_backend.domain.order.OrderStatus;

public record TransitionRequestBody(OrderStatus para, String motivo) {
}
