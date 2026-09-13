package com.teste_backend.teste_backend.domain.order.exception;

import com.teste_backend.teste_backend.domain.order.CancellationReason;
import com.teste_backend.teste_backend.domain.order.OrderStatus;

public class InvalidTransitionException extends RuntimeException {

    private InvalidTransitionException(String message) {
        super(message);
    }

    public static InvalidTransitionException missingReason() {
        return new InvalidTransitionException(
                "Motivo é obrigatório e deve ter ao menos " + CancellationReason.MINIMUM_LENGTH
                        + " caracteres para cancelar o pedido.");
    }

    public static InvalidTransitionException unknownTransition(OrderStatus current, OrderStatus target) {
        return new InvalidTransitionException(
                "Não é possível transicionar de " + current + " para " + target + ".");
    }
}
