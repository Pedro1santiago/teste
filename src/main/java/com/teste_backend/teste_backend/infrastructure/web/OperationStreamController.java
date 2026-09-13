package com.teste_backend.teste_backend.infrastructure.web;

import com.teste_backend.teste_backend.infrastructure.event.SseOrderEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
class OperationStreamController {

    private final SseOrderEventPublisher eventPublisher;

    OperationStreamController(SseOrderEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/operacao/stream")
    SseEmitter stream(@RequestHeader(value = "Last-Event-ID", required = false) Long lastEventId) {
        return eventPublisher.subscribe(lastEventId);
    }
}
