package com.teste_backend.teste_backend.application.order;

import com.teste_backend.teste_backend.domain.order.Order;
import com.teste_backend.teste_backend.domain.order.OrderEventPublisher;
import com.teste_backend.teste_backend.domain.order.OrderRepository;
import com.teste_backend.teste_backend.domain.order.OrderStatus;
import com.teste_backend.teste_backend.domain.order.event.OrderTransitionedEvent;
import com.teste_backend.teste_backend.domain.order.exception.OrderNotFoundException;
import com.teste_backend.teste_backend.domain.order.exception.OrderStatusConflictException;
import org.springframework.stereotype.Service;

@Service
public class TransitionOrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public TransitionOrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    public Order execute(Long id, OrderStatus target, String reason) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        int expectedVersion = order.version();
        order.transitionTo(target, reason);

        boolean applied = orderRepository.tryUpdateStatus(id, order.status(), expectedVersion, order.version());
        if (!applied) {
            Order current = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
            throw new OrderStatusConflictException(current.status());
        }

        eventPublisher.publish(new OrderTransitionedEvent(order.id(), order.status(), order.version()));
        return order;
    }
}
