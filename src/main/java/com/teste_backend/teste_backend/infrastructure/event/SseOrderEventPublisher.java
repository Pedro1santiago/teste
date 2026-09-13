package com.teste_backend.teste_backend.infrastructure.event;

import com.teste_backend.teste_backend.domain.order.OrderEventPublisher;
import com.teste_backend.teste_backend.domain.order.event.OrderCreatedEvent;
import com.teste_backend.teste_backend.domain.order.event.OrderTransitionedEvent;
import com.teste_backend.teste_backend.domain.time.ServerClock;
import com.teste_backend.teste_backend.infrastructure.web.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SseOrderEventPublisher implements OrderEventPublisher {

    private final ServerClock clock;
    private final int maxBufferedEvents;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final List<StoredEvent> eventLog = new CopyOnWriteArrayList<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public SseOrderEventPublisher(ServerClock clock,
                                   @Value("${sse.max-buffered-events:500}") int maxBufferedEvents) {
        this.clock = clock;
        this.maxBufferedEvents = maxBufferedEvents;
    }

    public SseEmitter subscribe(Long lastEventId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(ex -> emitters.remove(emitter));

        replayMissedEvents(emitter, lastEventId);

        return emitter;
    }

    @Override
    public void publish(OrderCreatedEvent event) {
        broadcast("pedido.criado", new OrderCreatedPayload(clock.now(), OrderResponse.from(event.order())));
    }

    @Override
    public void publish(OrderTransitionedEvent event) {
        broadcast("pedido.transicionado",
                new OrderTransitionedPayload(clock.now(), event.orderId(), event.status(), event.version()));
    }

    @Scheduled(fixedRateString = "${sse.heartbeat-interval-ms:10000}")
    void sendHeartbeat() {
        sendToAll("heartbeat", sequence.incrementAndGet(), new HeartbeatPayload(clock.now()));
    }

    @Scheduled(fixedRateString = "${sse.forced-disconnect-interval-ms:180000}")
    void forceReconnects() {
        for (SseEmitter emitter : emitters) {
            emitter.complete();
        }
    }

    private void broadcast(String eventName, Object payload) {
        long id = sequence.incrementAndGet();
        storeEvent(id, eventName, payload);
        sendToAll(eventName, id, payload);
    }

    private void storeEvent(long id, String eventName, Object payload) {
        eventLog.add(new StoredEvent(id, eventName, payload));
        while (eventLog.size() > maxBufferedEvents) {
            eventLog.remove(0);
        }
    }

    private void sendToAll(String eventName, long id, Object payload) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(id)).name(eventName).data(payload));
            } catch (IOException | IllegalStateException ex) {
                emitters.remove(emitter);
            }
        }
    }

    private void replayMissedEvents(SseEmitter emitter, Long lastEventId) {
        try {
            for (StoredEvent stored : eventLog) {
                if (lastEventId == null || stored.id() > lastEventId) {
                    emitter.send(SseEmitter.event()
                            .id(String.valueOf(stored.id()))
                            .name(stored.eventName())
                            .data(stored.payload()));
                }
            }
        } catch (IOException ex) {
            emitters.remove(emitter);
        }
    }

    private record StoredEvent(long id, String eventName, Object payload) {
    }
}
