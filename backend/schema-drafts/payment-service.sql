-- payment_db: money movements. We never store card numbers (Stripe keeps them, Lesson 27).

CREATE TABLE payments (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    order_id            BIGINT        NOT NULL,
    provider            VARCHAR(20)   NOT NULL DEFAULT 'STRIPE',
    provider_payment_id VARCHAR(100)  NULL,          -- Stripe's PaymentIntent id: "pi_..."
    amount              DECIMAL(12,2) NOT NULL,
    currency            CHAR(3)       NOT NULL,
    status              VARCHAR(20)   NOT NULL DEFAULT 'CREATED',
    created_at          DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_payments_provider_id (provider_payment_id),
    KEY idx_payments_order (order_id),
    CONSTRAINT ck_payments_status CHECK (status IN ('CREATED', 'SUCCEEDED', 'FAILED', 'REFUNDED'))
);

CREATE TABLE refunds (
    id                 BIGINT        NOT NULL AUTO_INCREMENT,
    payment_id         BIGINT        NOT NULL,
    provider_refund_id VARCHAR(100)  NULL,
    amount             DECIMAL(12,2) NOT NULL,
    reason             VARCHAR(200)  NULL,
    created_at         DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_refunds_payment FOREIGN KEY (payment_id) REFERENCES payments (id)
);

CREATE TABLE processed_webhook_events (
    event_id     VARCHAR(100) NOT NULL,              -- Stripe sends the same event more than once
    received_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (event_id)
);
