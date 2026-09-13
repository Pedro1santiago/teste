package com.teste_backend.teste_backend.infrastructure.time;

import com.teste_backend.teste_backend.domain.time.ServerClock;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
class SaoPauloServerClock implements ServerClock {

    @Override
    public OffsetDateTime now() {
        return OffsetDateTime.now(SAO_PAULO);
    }
}
