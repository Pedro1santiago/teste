package com.teste_backend.teste_backend.application.order;

import com.teste_backend.teste_backend.domain.order.Order;
import com.teste_backend.teste_backend.domain.order.OrderRepository;
import com.teste_backend.teste_backend.domain.order.OrderSearchCriteria;
import com.teste_backend.teste_backend.domain.order.PagedResult;
import org.springframework.stereotype.Service;

@Service
public class SearchOrdersService {

    private final OrderRepository orderRepository;

    public SearchOrdersService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public PagedResult<Order> execute(OrderSearchCriteria criteria) {
        return orderRepository.search(criteria);
    }
}
