package com.teste_backend.teste_backend.infrastructure.web;

import com.teste_backend.teste_backend.application.order.SearchOrdersService;
import com.teste_backend.teste_backend.application.order.TransitionOrderService;
import com.teste_backend.teste_backend.domain.order.Order;
import com.teste_backend.teste_backend.domain.order.OrderSearchCriteria;
import com.teste_backend.teste_backend.domain.order.OrderSortField;
import com.teste_backend.teste_backend.domain.order.OrderStatus;
import com.teste_backend.teste_backend.domain.order.PagedResult;
import com.teste_backend.teste_backend.domain.order.SortDirection;
import com.teste_backend.teste_backend.domain.time.ServerClock;
import com.teste_backend.teste_backend.infrastructure.web.dto.OrderActionResponse;
import com.teste_backend.teste_backend.infrastructure.web.dto.OrderPageResponse;
import com.teste_backend.teste_backend.infrastructure.web.dto.OrderResponse;
import com.teste_backend.teste_backend.infrastructure.web.dto.TransitionRequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
class OrderController {

    private final SearchOrdersService searchOrdersService;
    private final TransitionOrderService transitionOrderService;
    private final ServerClock clock;

    OrderController(SearchOrdersService searchOrdersService, TransitionOrderService transitionOrderService,
                     ServerClock clock) {
        this.searchOrdersService = searchOrdersService;
        this.transitionOrderService = transitionOrderService;
        this.clock = clock;
    }

    @GetMapping
    OrderPageResponse list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "criadoEm") String ordenarPor,
            @RequestParam(defaultValue = "desc") String ordem
    ) {
        OrderSortField sortField = "prometidoPara".equals(ordenarPor)
                ? OrderSortField.PROMETIDO_PARA
                : OrderSortField.CRIADO_EM;
        SortDirection direction = "asc".equalsIgnoreCase(ordem) ? SortDirection.ASC : SortDirection.DESC;

        OrderSearchCriteria criteria = new OrderSearchCriteria(
                page, size, Optional.ofNullable(status), Optional.ofNullable(busca), sortField, direction);

        PagedResult<Order> result = searchOrdersService.execute(criteria);
        return OrderPageResponse.from(result, clock.now());
    }

    @PostMapping("/{id}/transicoes")
    OrderActionResponse transition(@PathVariable Long id, @RequestBody TransitionRequestBody body) {
        Order order = transitionOrderService.execute(id, body.para(), body.motivo());
        return new OrderActionResponse(clock.now(), OrderResponse.from(order));
    }
}
