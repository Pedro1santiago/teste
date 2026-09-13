package com.teste_backend.teste_backend.infrastructure.event;

import com.teste_backend.teste_backend.domain.order.OrderStatus;

import java.time.OffsetDateTime;

record OrderTransitionedPayload(OffsetDateTime servidorEm, Long pedidoId, OrderStatus para, int versao) {
}
