package com.teste_backend.teste_backend.domain.time;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public interface ServerClock {

    ZoneOffset SAO_PAULO = ZoneOffset.of("-03:00");

    OffsetDateTime now();
}
