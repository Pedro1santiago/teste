package com.teste_backend.teste_backend.infrastructure.persistence;

import com.teste_backend.teste_backend.domain.order.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "orders")
class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "address_summary", nullable = false)
    private String addressSummary;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "promised_for", nullable = false)
    private OffsetDateTime promisedFor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "version", nullable = false)
    private int version;

    protected OrderJpaEntity() {
    }

    OrderJpaEntity(Long id, String clientName, String addressSummary, BigDecimal totalAmount,
                   OffsetDateTime createdAt, OffsetDateTime promisedFor, OrderStatus status, int version) {
        this.id = id;
        this.clientName = clientName;
        this.addressSummary = addressSummary;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.promisedFor = promisedFor;
        this.status = status;
        this.version = version;
    }

    Long getId() {
        return id;
    }

    String getClientName() {
        return clientName;
    }

    String getAddressSummary() {
        return addressSummary;
    }

    BigDecimal getTotalAmount() {
        return totalAmount;
    }

    OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    OffsetDateTime getPromisedFor() {
        return promisedFor;
    }

    OrderStatus getStatus() {
        return status;
    }

    int getVersion() {
        return version;
    }
}
