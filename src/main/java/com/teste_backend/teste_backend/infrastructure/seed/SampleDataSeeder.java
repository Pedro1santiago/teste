package com.teste_backend.teste_backend.infrastructure.seed;

import com.teste_backend.teste_backend.application.order.RandomOrderFactory;
import com.teste_backend.teste_backend.domain.order.Order;
import com.teste_backend.teste_backend.domain.order.OrderEventPublisher;
import com.teste_backend.teste_backend.domain.order.OrderRepository;
import com.teste_backend.teste_backend.domain.order.OrderStatus;
import com.teste_backend.teste_backend.domain.order.event.OrderCreatedEvent;
import com.teste_backend.teste_backend.domain.time.ServerClock;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
class SampleDataSeeder implements ApplicationRunner {

    private static final OrderStatus[] HAPPY_PATH = {
            OrderStatus.EM_PREPARO, OrderStatus.PRONTO, OrderStatus.EM_ROTA, OrderStatus.ENTREGUE
    };

    private static final OrderStatus[] BACKLOG_TERMINAL = {OrderStatus.ENTREGUE, OrderStatus.ENTREGUE, OrderStatus.CANCELADO};
    private static final OrderStatus[] BACKLOG_ACTIVE =
            {OrderStatus.RECEBIDO, OrderStatus.EM_PREPARO, OrderStatus.PRONTO, OrderStatus.EM_ROTA};

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final ServerClock clock;
    private final RandomOrderFactory orderFactory;

    SampleDataSeeder(OrderRepository orderRepository, OrderEventPublisher eventPublisher, ServerClock clock,
                      RandomOrderFactory orderFactory) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
        this.orderFactory = orderFactory;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (orderRepository.hasAny()) {
            return;
        }

        OffsetDateTime now = clock.now();
        seedFixedScenarios(now);
        seedRandomBacklog(now);
    }

    private void seedFixedScenarios(OffsetDateTime now) {
        persist(scenario("Maria Silva", "Rua das Acácias, 210 — Pinheiros", "87.40", now, 45, OrderStatus.RECEBIDO, null));
        persist(scenario("João Souza", "Av. Paulista, 1500 — Bela Vista", "62.90", now, 8, OrderStatus.RECEBIDO, null));
        persist(scenario("Ana Pereira", "Rua Augusta, 900 — Consolação", "45.00", now, 3, OrderStatus.EM_PREPARO, null));
        persist(scenario("Carlos Lima", "Rua Oscar Freire, 300 — Jardins", "120.50", now, -10, OrderStatus.EM_PREPARO, null));
        persist(scenario("Beatriz Alves", "Rua das Acácias, 55 — Pinheiros", "38.20", now, 15, OrderStatus.PRONTO, null));
        persist(scenario("Pedro Costa", "Av. Paulista, 800 — Bela Vista", "99.90", now, 2, OrderStatus.PRONTO, null));
        persist(scenario("Juliana Rocha", "Rua Augusta, 400 — Consolação", "54.30", now, 6, OrderStatus.EM_ROTA, null));
        persist(scenario("Felipe Martins", "Rua Oscar Freire, 150 — Jardins", "73.10", now, -20, OrderStatus.EM_ROTA, null));
        persist(scenario("Camila Nunes", "Rua das Acácias, 300 — Pinheiros", "41.00", now, -60, OrderStatus.ENTREGUE, null));
        persist(scenario("Rafael Dias", "Av. Paulista, 2000 — Bela Vista", "88.75", now, -120, OrderStatus.CANCELADO,
                "Cliente desistiu do pedido"));
    }

    private void seedRandomBacklog(OffsetDateTime now) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 15; i++) {
            OffsetDateTime createdAt = now.minusMinutes(random.nextInt(600));
            Order order = orderFactory.next(createdAt);

            boolean finished = random.nextBoolean();
            OrderStatus target = finished
                    ? BACKLOG_TERMINAL[random.nextInt(BACKLOG_TERMINAL.length)]
                    : BACKLOG_ACTIVE[random.nextInt(BACKLOG_ACTIVE.length)];

            advance(order, target, "Cliente desistiu do pedido");
            persist(order);
        }
    }

    private Order scenario(String clientName, String address, String amount, OffsetDateTime now, int promisedInMinutes,
                            OrderStatus targetStatus, String cancellationReason) {
        Order order = Order.create(clientName, address, new BigDecimal(amount), now, now.plusMinutes(promisedInMinutes));
        advance(order, targetStatus, cancellationReason);
        return order;
    }

    private void advance(Order order, OrderStatus target, String cancellationReason) {
        if (target == OrderStatus.CANCELADO) {
            order.transitionTo(OrderStatus.CANCELADO, cancellationReason);
            return;
        }
        for (OrderStatus step : HAPPY_PATH) {
            if (order.status() == target) {
                return;
            }
            order.transitionTo(step, null);
        }
    }

    private void persist(Order order) {
        Order saved = orderRepository.insert(order);
        eventPublisher.publish(new OrderCreatedEvent(saved));
    }
}
