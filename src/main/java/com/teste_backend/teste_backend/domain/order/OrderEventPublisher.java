package com.teste_backend.teste_backend.domain.order;

import com.teste_backend.teste_backend.domain.order.event.OrderCreatedEvent;
import com.teste_backend.teste_backend.domain.order.event.OrderTransitionedEvent;

public interface OrderEventPublisher {

    void publish(OrderCreatedEvent event);

    void publish(OrderTransitionedEvent event);
}
