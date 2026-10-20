-- order_db: orders are history. Copy (snapshot) everything that can change later.

CREATE TABLE orders (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    order_number     VARCHAR(30)   NOT NULL,         -- shown to the customer: "SV-2026-000123"
    user_id          BIGINT        NOT NULL,
    status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    subtotal         DECIMAL(12,2) NOT NULL,
    shipping_fee     DECIMAL(12,2) NOT NULL DEFAULT 0,
    tax              DECIMAL(12,2) NOT NULL DEFAULT 0,
    total            DECIMAL(12,2) NOT NULL,
    currency         CHAR(3)       NOT NULL DEFAULT 'INR',
    ship_name        VARCHAR(150)  NOT NULL,         -- address snapshot, not an address_id
    ship_phone       VARCHAR(20)   NOT NULL,
    ship_line1       VARCHAR(200)  NOT NULL,
    ship_line2       VARCHAR(200)  NULL,
    ship_city        VARCHAR(100)  NOT NULL,
    ship_state       VARCHAR(100)  NOT NULL,
    ship_postal_code VARCHAR(12)   NOT NULL,
    ship_country     CHAR(2)       NOT NULL,
    idempotency_key  VARCHAR(64)   NOT NULL,         -- double-click on "Place order" = one order (Lesson 26)
    version          BIGINT        NOT NULL DEFAULT 0,
    created_at       DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at       DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_orders_number (order_number),
    UNIQUE KEY uk_orders_idempotency (user_id, idempotency_key),
    KEY idx_orders_user_created (user_id, created_at),
    CONSTRAINT ck_orders_status CHECK (status IN ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED'))
);

CREATE TABLE order_items (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    order_id      BIGINT        NOT NULL,
    product_id    BIGINT        NOT NULL,
    product_name  VARCHAR(200)  NOT NULL,            -- snapshot
    sku           VARCHAR(40)   NOT NULL,            -- snapshot
    unit_price    DECIMAL(12,2) NOT NULL,            -- snapshot: the price the customer actually paid
    quantity      INT           NOT NULL,
    line_total    DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_order_items_order (order_id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT ck_order_items_qty CHECK (quantity > 0)
);

CREATE TABLE order_status_history (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    order_id    BIGINT      NOT NULL,
    from_status VARCHAR(20) NULL,
    to_status   VARCHAR(20) NOT NULL,
    changed_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_status_history_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);
