package com.teste_backend.teste_backend.domain.order.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Pedido " + id + " não encontrado.");
    }
}
