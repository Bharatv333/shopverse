-- inventory_db: how many of each product we can sell (Lessons 6, 15)

CREATE TABLE stock (
    product_id  BIGINT NOT NULL,                     -- lives in product_db
    available   INT    NOT NULL DEFAULT 0,           -- can still be sold
    reserved    INT    NOT NULL DEFAULT 0,           -- held for orders that aren't paid yet
    version     BIGINT NOT NULL DEFAULT 0,
    updated_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (product_id),
    CONSTRAINT ck_stock_available CHECK (available >= 0),
    CONSTRAINT ck_stock_reserved CHECK (reserved >= 0)
);

CREATE TABLE stock_reservations (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    order_id    BIGINT      NOT NULL,                -- lives in order_db
    product_id  BIGINT      NOT NULL,
    quantity    INT         NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    expires_at  DATETIME(6) NOT NULL,                -- release if payment never arrives
    created_at  DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_reservations_order_product (order_id, product_id),
    CONSTRAINT ck_reservations_qty CHECK (quantity > 0),
    CONSTRAINT ck_reservations_status CHECK (status IN ('RESERVED', 'CONFIRMED', 'RELEASED'))
);
