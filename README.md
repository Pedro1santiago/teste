# teste-backend

Backend mock que simula uma operação de delivery em tempo real, servindo de apoio ao desafio técnico de frontend Angular. Pedidos são criados e avançados automaticamente por um scheduler (simulando a cozinha), além de poderem ser transicionados manualmente via API — tudo propagado em tempo real via SSE.

## Como rodar

Requer Java 21 e um PostgreSQL acessível. Configure via variáveis de ambiente (todas têm um default local para desenvolvimento):

```bash
export DATABASE_URL=jdbc:postgresql://<host>:5432/<database>?sslmode=require
export DATABASE_USERNAME=<usuario>
export DATABASE_PASSWORD=<senha>
export PORT=8080
```

```bash
./gradlew bootRun
```

O Flyway cria o schema automaticamente na primeira subida (`src/main/resources/db/migration`).

**Neon:** exige SSL — inclua `?sslmode=require` na `DATABASE_URL` (a connection string que o Neon fornece já vem com isso). Prefira a connection string **pooled** (via PgBouncer) que o Neon disponibiliza, já que o app roda com o pool padrão do Spring (HikariCP) e vários pedidos concorrentes (HTTP + scheduler de simulação + SSE).

**Hospedagem:** este backend precisa de um processo persistente (tem um scheduler `@Scheduled` rodando em background e conexões SSE de longa duração) — **não funciona em plataformas serverless como Vercel**, cuja execução é por requisição e tem timeout curto. Use algo com processo always-on: Render, Railway, Fly.io, ou um VPS com Docker.

## Rodar os testes

```bash
./gradlew test
```

`OrderStatusTest` e `OrderTest` são testes de domínio puro (sem banco). `TesteBackendApplicationTests` (contexto do Spring) exige um Postgres acessível para passar.

## Endpoints

- `GET /pedidos` — paginado (`page`, `size`), filtro (`status`, `busca`), ordenação (`ordenarPor=criadoEm|prometidoPara`, `ordem=asc|desc`).
- `GET /operacao/stream` — SSE com `pedido.criado`, `pedido.transicionado`, `heartbeat` (a cada 10s). Reconexão via `Last-Event-ID` é suportada (replay dos eventos perdidos, buffer de até 500 eventos). A conexão é fechada de propósito a cada 3 minutos para forçar reconexão.
- `POST /pedidos/{id}/transicoes` — `{ "para": "EM_ROTA", "motivo": null }`. Retorna `200` (pedido atualizado), `409` (pedido já mudou de estado) ou `422` (transição estruturalmente inválida ou motivo ausente/curto).

## Decisões de design

**409 vs 422.** `422` é usado quando o alvo da transição nunca é válido em nenhum cenário (ex: voltar para `RECEBIDO`, ou `CANCELADO` sem motivo de 10+ caracteres). `409` é usado quando o alvo é um estado real do fluxo, só que inalcançável a partir do status atual do pedido — isso cobre tanto uma corrida real (scheduler ou outro operador mudou primeiro) quanto um duplo clique (a segunda chamada vê que o pedido já está no estado que a primeira aplicou). A regra completa mora em `OrderStatus` (grafo de transições) e `Order.transitionTo` (domínio) — não existe em nenhum outro lugar.

**Concorrência.** Lock otimista via `UPDATE ... WHERE id = ? AND version = ?` (não usa `@Version` do Hibernate nem `SELECT FOR UPDATE`). Se 0 linhas forem afetadas, outra escrita venceu a corrida — o status real é recarregado e devolvido como `409`.

**Arquitetura.** Ports & Adapters: `domain/` não importa Spring nem JPA; `application/` orquestra casos de uso; `infrastructure/` implementa as portas (persistência JPA, SSE, scheduler, web).

**Simulação.** Um scheduler cria pedidos novos (a cada ~20s) e avança pedidos ativos existentes (a cada ~9s) pelo caminho feliz (nunca cancela automaticamente — cancelamento só acontece via chamada manual). Cadência ajustável via `simulation.creation-interval-ms` / `simulation.advance-interval-ms`.

**Fuso horário.** Todo horário (`servidorEm`, `criadoEm`, `prometidoPara`) é emitido com offset fixo `-03:00` (América/São Paulo, sem horário de verão desde 2019).

## Limitações conhecidas (dado o prazo)

- Sem smoke test end-to-end contra um Postgres real (Docker não estava disponível no ambiente de desenvolvimento) — validado via testes unitários de domínio + compilação limpa.
- CORS liberado para qualquer origem (`allowedOriginPatterns("*")`), adequado para um mock de teste técnico, não para produção.
- Busca por cliente usa `LIKE` simples (sem índice trigram); aceitável para o volume de dados de um teste técnico.
