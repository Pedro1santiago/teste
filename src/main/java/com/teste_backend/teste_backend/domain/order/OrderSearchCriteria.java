package com.teste_backend.teste_backend.domain.order;

import java.util.Optional;

public record OrderSearchCriteria(
        int page,
        int size,
        Optional<OrderStatus> status,
        Optional<String> clientNameQuery,
        OrderSortField sortField,
        SortDirection direction
) {
}
