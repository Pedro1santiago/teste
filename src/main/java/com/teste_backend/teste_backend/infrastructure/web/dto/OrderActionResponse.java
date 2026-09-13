package com.teste_backend.teste_backend.infrastructure.web.dto;

import java.time.OffsetDateTime;

public record OrderActionResponse(OffsetDateTime servidorEm, OrderResponse pedido) {
}
