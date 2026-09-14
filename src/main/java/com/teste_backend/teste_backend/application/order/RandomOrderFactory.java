package com.teste_backend.teste_backend.application.order;

import com.teste_backend.teste_backend.domain.order.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomOrderFactory {

    private static final List<String> CLIENT_NAMES = List.of(
            "Maria Silva", "João Souza", "Ana Pereira", "Carlos Lima",
            "Beatriz Alves", "Pedro Costa", "Juliana Rocha", "Felipe Martins"
    );

    private static final List<String> ADDRESSES = List.of(
            "Rua das Acácias, 210 — Pinheiros", "Av. Paulista, 1500 — Bela Vista",
            "Rua Augusta, 900 — Consolação", "Rua Oscar Freire, 300 — Jardins"
    );

    public Order next(OffsetDateTime now) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        String clientName = CLIENT_NAMES.get(random.nextInt(CLIENT_NAMES.size()));
        String address = ADDRESSES.get(random.nextInt(ADDRESSES.size()));
        BigDecimal amount = BigDecimal.valueOf(20 + random.nextInt(180)).setScale(2, RoundingMode.UNNECESSARY);
        OffsetDateTime promisedFor = now.plusMinutes(20 + random.nextInt(40));

        return Order.create(clientName, address, amount, now, promisedFor);
    }
}
