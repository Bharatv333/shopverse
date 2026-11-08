-- Stock lives here for now. In Lesson 11 it moves to its own inventory-service and inventory_db.

CREATE TABLE stock
(
    product_id BIGINT NOT NULL,
    available  INT    NOT NULL DEFAULT 0, -- can still be sold
    reserved   INT    NOT NULL DEFAULT 0, -- held for orders that aren't paid yet
    version    BIGINT NOT NULL DEFAULT 0, -- optimistic locking
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (product_id),
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT ck_stock_available CHECK (available >= 0),
    CONSTRAINT ck_stock_reserved CHECK (reserved >= 0)
);

INSERT INTO stock (product_id, available)
VALUES (1, 10),
       (2, 150),
       (3, 40),
       (4, 0),
       (5, 25),
       (6, 200);