package com.teste_backend.teste_backend.infrastructure.event;

import com.teste_backend.teste_backend.infrastructure.web.dto.OrderResponse;

import java.time.OffsetDateTime;

record OrderCreatedPayload(OffsetDateTime servidorEm, OrderResponse pedido) {
}
