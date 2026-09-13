package com.teste_backend.teste_backend.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Optional<Order> findById(Long id);

    Order insert(Order order);

    boolean tryUpdateStatus(Long id, OrderStatus newStatus, int expectedVersion, int newVersion);

    PagedResult<Order> search(OrderSearchCriteria criteria);

    List<Order> findActive();
}
