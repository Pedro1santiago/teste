package com.teste_backend.teste_backend.infrastructure.persistence;

import com.teste_backend.teste_backend.domain.order.Order;
import com.teste_backend.teste_backend.domain.time.ServerClock;

final class OrderMapper {

    private OrderMapper() {
    }

    static Order toDomain(OrderJpaEntity entity) {
        return Order.reconstitute(
                entity.getId(),
                entity.getClientName(),
                entity.getAddressSummary(),
                entity.getTotalAmount(),
                entity.getCreatedAt().withOffsetSameInstant(ServerClock.SAO_PAULO),
                entity.getPromisedFor().withOffsetSameInstant(ServerClock.SAO_PAULO),
                entity.getStatus(),
                entity.getVersion()
        );
    }

    static OrderJpaEntity toNewEntity(Order order) {
        return new OrderJpaEntity(
                null,
                order.clientName(),
                order.addressSummary(),
                order.totalAmount(),
                order.createdAt(),
                order.promisedFor(),
                order.status(),
                order.version()
        );
    }
}
