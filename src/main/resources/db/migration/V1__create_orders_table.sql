CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(200) NOT NULL,
    address_summary VARCHAR(300) NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    promised_for TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL,
    version INTEGER NOT NULL
);

CREATE INDEX idx_orders_status ON orders (status);
