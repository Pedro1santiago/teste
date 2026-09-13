package com.teste_backend.teste_backend.infrastructure.event;

import java.time.OffsetDateTime;

record HeartbeatPayload(OffsetDateTime servidorEm) {
}
