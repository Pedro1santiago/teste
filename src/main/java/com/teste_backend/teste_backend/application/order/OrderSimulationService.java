package com.teste_backend.teste_backend.application.order;

import com.teste_backend.teste_backend.domain.order.Order;
import com.teste_backend.teste_backend.domain.order.OrderEventPublisher;
import com.teste_backend.teste_backend.domain.order.OrderRepository;
import com.teste_backend.teste_backend.domain.order.OrderStatus;
import com.teste_backend.teste_backend.domain.order.event.OrderCreatedEvent;
import com.teste_backend.teste_backend.domain.order.event.OrderTransitionedEvent;
import com.teste_backend.teste_backend.domain.time.ServerClock;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderSimulationService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final ServerClock clock;
    private final RandomOrderFactory orderFactory;

    public OrderSimulationService(OrderRepository orderRepository, OrderEventPublisher eventPublisher,
                                   ServerClock clock, RandomOrderFactory orderFactory) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
        this.orderFactory = orderFactory;
    }

    public void createRandomOrder() {
        Order order = orderFactory.next(clock.now());
        Order saved = orderRepository.insert(order);
        eventPublisher.publish(new OrderCreatedEvent(saved));
    }

    public void advanceRandomActiveOrder() {
        List<Order> active = orderRepository.findActive();
        if (active.isEmpty()) {
            return;
        }

        Order chosen = active.get(ThreadLocalRandom.current().nextInt(active.size()));
        Optional<OrderStatus> next = nextHappyPathStatus(chosen.status());
        if (next.isEmpty()) {
            return;
        }

        int expectedVersion = chosen.version();
        chosen.transitionTo(next.get(), null);

        boolean applied = orderRepository.tryUpdateStatus(chosen.id(), chosen.status(), expectedVersion, chosen.version());
        if (applied) {
            eventPublisher.publish(new OrderTransitionedEvent(chosen.id(), chosen.status(), chosen.version()));
        }
    }

    private Optional<OrderStatus> nextHappyPathStatus(OrderStatus current) {
        return current.allowedNextStatuses().stream()
                .filter(candidate -> candidate != OrderStatus.CANCELADO)
                .findFirst();
    }
}
