package com.teste_backend.teste_backend.infrastructure.web.dto;

import com.teste_backend.teste_backend.domain.order.Order;
import com.teste_backend.teste_backend.domain.order.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderResponse(
        Long id,
        String codigo,
        String clienteNome,
        String enderecoResumo,
        OrderStatus status,
        BigDecimal valorTotal,
        OffsetDateTime criadoEm,
        OffsetDateTime prometidoPara,
        int versao
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.id(),
                order.code(),
                order.clientName(),
                order.addressSummary(),
                order.status(),
                order.totalAmount(),
                order.createdAt(),
                order.promisedFor(),
                order.version()
        );
    }
}
