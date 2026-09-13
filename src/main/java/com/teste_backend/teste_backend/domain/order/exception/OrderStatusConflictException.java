package com.teste_backend.teste_backend.domain.order.exception;

import com.teste_backend.teste_backend.domain.order.OrderStatus;

public class OrderStatusConflictException extends RuntimeException {

    private final OrderStatus currentStatus;

    public OrderStatusConflictException(OrderStatus currentStatus) {
        super("O pedido já está em " + currentStatus);
        this.currentStatus = currentStatus;
    }

    public OrderStatus currentStatus() {
        return currentStatus;
    }
}
