package com.teste_backend.teste_backend.domain.order;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderStatusTest {

    @Test
    void allowsTheHappyPathTransitions() {
        assertTrue(OrderStatus.RECEBIDO.allowsTransitionTo(OrderStatus.EM_PREPARO));
        assertTrue(OrderStatus.EM_PREPARO.allowsTransitionTo(OrderStatus.PRONTO));
        assertTrue(OrderStatus.PRONTO.allowsTransitionTo(OrderStatus.EM_ROTA));
        assertTrue(OrderStatus.EM_ROTA.allowsTransitionTo(OrderStatus.ENTREGUE));
    }

    @Test
    void allowsCancellationOnlyFromReceivedOrInPreparation() {
        assertTrue(OrderStatus.RECEBIDO.allowsTransitionTo(OrderStatus.CANCELADO));
        assertTrue(OrderStatus.EM_PREPARO.allowsTransitionTo(OrderStatus.CANCELADO));
        assertFalse(OrderStatus.PRONTO.allowsTransitionTo(OrderStatus.CANCELADO));
        assertFalse(OrderStatus.EM_ROTA.allowsTransitionTo(OrderStatus.CANCELADO));
    }

    @Test
    void rejectsSkippingStates() {
        assertFalse(OrderStatus.RECEBIDO.allowsTransitionTo(OrderStatus.ENTREGUE));
        assertFalse(OrderStatus.EM_PREPARO.allowsTransitionTo(OrderStatus.EM_ROTA));
    }

    @Test
    void treatsDeliveredAndCancelledAsFinal() {
        assertTrue(OrderStatus.ENTREGUE.isFinal());
        assertTrue(OrderStatus.CANCELADO.isFinal());
        assertFalse(OrderStatus.PRONTO.isFinal());
    }

    @Test
    void validTargetStatusesExcludesOnlyReceived() {
        assertEquals(
                EnumSetOf(OrderStatus.EM_PREPARO, OrderStatus.PRONTO, OrderStatus.EM_ROTA,
                        OrderStatus.ENTREGUE, OrderStatus.CANCELADO),
                OrderStatus.validTargetStatuses()
        );
    }

    private static java.util.Set<OrderStatus> EnumSetOf(OrderStatus... statuses) {
        return java.util.EnumSet.copyOf(java.util.List.of(statuses));
    }
}
