package com.teste_backend.teste_backend.infrastructure.persistence;

import com.teste_backend.teste_backend.domain.order.Order;
import com.teste_backend.teste_backend.domain.order.OrderRepository;
import com.teste_backend.teste_backend.domain.order.OrderSearchCriteria;
import com.teste_backend.teste_backend.domain.order.OrderStatus;
import com.teste_backend.teste_backend.domain.order.PagedResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
class OrderRepositoryAdapter implements OrderRepository {

    private final SpringDataOrderJpaRepository jpaRepository;

    OrderRepositoryAdapter(SpringDataOrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id).map(OrderMapper::toDomain);
    }

    @Override
    public Order insert(Order order) {
        return OrderMapper.toDomain(jpaRepository.save(OrderMapper.toNewEntity(order)));
    }

    @Override
    public boolean tryUpdateStatus(Long id, OrderStatus newStatus, int expectedVersion, int newVersion) {
        int rowsAffected = jpaRepository.updateStatusIfVersionMatches(id, newStatus, expectedVersion, newVersion);
        return rowsAffected == 1;
    }

    @Override
    public PagedResult<Order> search(OrderSearchCriteria criteria) {
        Specification<OrderJpaEntity> specification = OrderSpecifications.matching(criteria);
        Sort sort = OrderSpecifications.sortFor(criteria);
        int zeroBasedPage = Math.max(0, criteria.page() - 1);
        int pageSize = Math.max(1, criteria.size());
        Pageable pageable = PageRequest.of(zeroBasedPage, pageSize, sort);

        Page<OrderJpaEntity> page = jpaRepository.findAll(specification, pageable);

        return new PagedResult<>(
                page.getContent().stream().map(OrderMapper::toDomain).toList(),
                criteria.page(),
                criteria.size(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public List<Order> findActive() {
        List<OrderStatus> activeStatuses = Arrays.stream(OrderStatus.values())
                .filter(status -> !status.isFinal())
                .toList();

        return jpaRepository.findByStatusIn(activeStatuses).stream()
                .map(OrderMapper::toDomain)
                .toList();
    }

    @Override
    public boolean hasAny() {
        return jpaRepository.count() > 0;
    }
}
