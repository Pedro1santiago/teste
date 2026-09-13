package com.teste_backend.teste_backend.infrastructure.web.dto;

import com.teste_backend.teste_backend.domain.order.Order;
import com.teste_backend.teste_backend.domain.order.PagedResult;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderPageResponse(
        OffsetDateTime servidorEm,
        List<OrderResponse> conteudo,
        int pagina,
        int tamanho,
        long total,
        int totalPaginas
) {

    public static OrderPageResponse from(PagedResult<Order> result, OffsetDateTime now) {
        return new OrderPageResponse(
                now,
                result.content().stream().map(OrderResponse::from).toList(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
