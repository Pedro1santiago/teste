package com.teste_backend.teste_backend.domain.order;

import com.teste_backend.teste_backend.domain.order.exception.InvalidTransitionException;
import com.teste_backend.teste_backend.domain.order.exception.OrderStatusConflictException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public final class Order {

    private final Long id;
    private final String clientName;
    private final String addressSummary;
    private final BigDecimal totalAmount;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime promisedFor;
    private OrderStatus status;
    private int version;

    private Order(Long id, String clientName, String addressSummary, BigDecimal totalAmount,
                   OffsetDateTime createdAt, OffsetDateTime promisedFor, OrderStatus status, int version) {
        this.id = id;
        this.clientName = Objects.requireNonNull(clientName);
        this.addressSummary = Objects.requireNonNull(addressSummary);
        this.totalAmount = Objects.requireNonNull(totalAmount);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.promisedFor = Objects.requireNonNull(promisedFor);
        this.status = Objects.requireNonNull(status);
        this.version = version;
    }

    public static Order create(String clientName, String addressSummary, BigDecimal totalAmount,
                                OffsetDateTime createdAt, OffsetDateTime promisedFor) {
        return new Order(null, clientName, addressSummary, totalAmount, createdAt, promisedFor,
                OrderStatus.RECEBIDO, 1);
    }

    public static Order reconstitute(Long id, String clientName, String addressSummary, BigDecimal totalAmount,
                                      OffsetDateTime createdAt, OffsetDateTime promisedFor, OrderStatus status,
                                      int version) {
        return new Order(Objects.requireNonNull(id), clientName, addressSummary, totalAmount, createdAt,
                promisedFor, status, version);
    }

    public void transitionTo(OrderStatus target, String reason) {
        if (target == OrderStatus.CANCELADO && !CancellationReason.isValid(reason)) {
            throw InvalidTransitionException.missingReason();
        }
        if (status.allowsTransitionTo(target)) {
            status = target;
            version++;
            return;
        }
        if (OrderStatus.validTargetStatuses().contains(target)) {
            throw new OrderStatusConflictException(status);
        }
        throw InvalidTransitionException.unknownTransition(status, target);
    }

    public boolean isActive() {
        return !status.isFinal();
    }

    public String code() {
        return "PED-" + String.format("%04d", id);
    }

    public Long id() {
        return id;
    }

    public String clientName() {
        return clientName;
    }

    public String addressSummary() {
        return addressSummary;
    }

    public BigDecimal totalAmount() {
        return totalAmount;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public OffsetDateTime promisedFor() {
        return promisedFor;
    }

    public OrderStatus status() {
        return status;
    }

    public int version() {
        return version;
    }
}
