package com.teste_backend.teste_backend.infrastructure.scheduling;

import com.teste_backend.teste_backend.application.order.OrderSimulationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class OrderSimulationScheduler {

    private final OrderSimulationService simulationService;

    OrderSimulationScheduler(OrderSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Scheduled(fixedDelayString = "${simulation.creation-interval-ms:20000}")
    void createOrders() {
        simulationService.createRandomOrder();
    }

    @Scheduled(fixedDelayString = "${simulation.advance-interval-ms:9000}")
    void advanceOrders() {
        simulationService.advanceRandomActiveOrder();
    }
}
