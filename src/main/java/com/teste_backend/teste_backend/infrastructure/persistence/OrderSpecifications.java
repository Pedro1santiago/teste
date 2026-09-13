package com.teste_backend.teste_backend.infrastructure.persistence;

import com.teste_backend.teste_backend.domain.order.OrderSearchCriteria;
import com.teste_backend.teste_backend.domain.order.SortDirection;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

final class OrderSpecifications {

    private OrderSpecifications() {
    }

    static Specification<OrderJpaEntity> matching(OrderSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            criteria.status().ifPresent(status ->
                    predicates.add(cb.equal(root.get("status"), status)));

            criteria.clientNameQuery().ifPresent(text ->
                    predicates.add(cb.like(cb.lower(root.get("clientName")), "%" + text.toLowerCase() + "%")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    static Sort sortFor(OrderSearchCriteria criteria) {
        String property = switch (criteria.sortField()) {
            case PROMETIDO_PARA -> "promisedFor";
            case CRIADO_EM -> "createdAt";
        };
        Sort.Direction direction = criteria.direction() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }
}
