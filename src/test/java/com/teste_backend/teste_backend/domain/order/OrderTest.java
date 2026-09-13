package com.teste_backend.teste_backend.domain.order;

import com.teste_backend.teste_backend.domain.order.exception.InvalidTransitionException;
import com.teste_backend.teste_backend.domain.order.exception.OrderStatusConflictException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    private Order newOrder() {
        OffsetDateTime now = OffsetDateTime.now();
        return Order.create("Maria Silva", "Rua das Acácias, 210", BigDecimal.TEN, now, now.plusMinutes(30));
    }

    @Test
    void appliesAValidTransitionAndBumpsVersion() {
        Order order = newOrder();

        order.transitionTo(OrderStatus.EM_PREPARO, null);

        assertEquals(OrderStatus.EM_PREPARO, order.status());
        assertEquals(2, order.version());
    }

    @Test
    void rejectsAnAlreadyChangedStatusWithConflict() {
        Order order = newOrder();
        order.transitionTo(OrderStatus.EM_PREPARO, null);
        order.transitionTo(OrderStatus.PRONTO, null);

        OrderStatusConflictException ex = assertThrows(OrderStatusConflictException.class,
                () -> order.transitionTo(OrderStatus.PRONTO, null));

        assertEquals(OrderStatus.PRONTO, ex.currentStatus());
    }

    @Test
    void rejectsAStructurallyImpossibleTarget() {
        Order order = newOrder();

        assertThrows(InvalidTransitionException.class, () -> order.transitionTo(OrderStatus.RECEBIDO, null));
    }

    @Test
    void rejectsCancellationWithoutAValidReason() {
        Order order = newOrder();

        assertThrows(InvalidTransitionException.class, () -> order.transitionTo(OrderStatus.CANCELADO, "curto"));
    }

    @Test
    void acceptsCancellationWithAValidReason() {
        Order order = newOrder();

        order.transitionTo(OrderStatus.CANCELADO, "Cliente desistiu da compra");

        assertEquals(OrderStatus.CANCELADO, order.status());
    }
}
