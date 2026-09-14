package com.teste_backend.teste_backend.infrastructure.persistence;

import com.teste_backend.teste_backend.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

interface SpringDataOrderJpaRepository extends JpaRepository<OrderJpaEntity, Long>,
        JpaSpecificationExecutor<OrderJpaEntity> {

    @Transactional
    @Modifying
    @Query("UPDATE OrderJpaEntity o SET o.status = :status, o.version = :newVersion " +
            "WHERE o.id = :id AND o.version = :expectedVersion")
    int updateStatusIfVersionMatches(@Param("id") Long id, @Param("status") OrderStatus status,
                                      @Param("expectedVersion") int expectedVersion,
                                      @Param("newVersion") int newVersion);

    @Query("SELECT o FROM OrderJpaEntity o WHERE o.status IN :activeStatuses")
    List<OrderJpaEntity> findByStatusIn(@Param("activeStatuses") List<OrderStatus> activeStatuses);
}
