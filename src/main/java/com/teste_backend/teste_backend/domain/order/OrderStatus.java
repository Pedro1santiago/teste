package com.teste_backend.teste_backend.domain.order;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum OrderStatus {

    RECEBIDO,
    EM_PREPARO,
    PRONTO,
    EM_ROTA,
    ENTREGUE,
    CANCELADO;

    public Set<OrderStatus> allowedNextStatuses() {
        return switch (this) {
            case RECEBIDO -> EnumSet.of(EM_PREPARO, CANCELADO);
            case EM_PREPARO -> EnumSet.of(PRONTO, CANCELADO);
            case PRONTO -> EnumSet.of(EM_ROTA);
            case EM_ROTA -> EnumSet.of(ENTREGUE);
            case ENTREGUE, CANCELADO -> EnumSet.noneOf(OrderStatus.class);
        };
    }

    public boolean allowsTransitionTo(OrderStatus target) {
        return allowedNextStatuses().contains(target);
    }

    public boolean isFinal() {
        return allowedNextStatuses().isEmpty();
    }

    public static Set<OrderStatus> validTargetStatuses() {
        return Arrays.stream(values())
                .flatMap(status -> status.allowedNextStatuses().stream())
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(OrderStatus.class)));
    }
}
