-- product-service owns these tables. No other service reads them directly.

CREATE TABLE categories
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    slug       VARCHAR(120) NOT NULL, -- URL-friendly name: "home-kitchen"
    parent_id  BIGINT NULL,           -- NULL = top level, else a sub-category
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_categories_slug (slug),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories (id)
);

CREATE TABLE products
(
    id           BIGINT         NOT NULL AUTO_INCREMENT,
    sku          VARCHAR(40)    NOT NULL,           -- the business code printed on the box
    name         VARCHAR(200)   NOT NULL,
    slug         VARCHAR(220)   NOT NULL,
    description  TEXT NULL,
    category_id  BIGINT         NOT NULL,
    price        DECIMAL(12, 2) NOT NULL,           -- money: DECIMAL, never FLOAT/DOUBLE
    currency     CHAR(3)        NOT NULL DEFAULT 'INR',
    avg_rating   DECIMAL(3, 2)  NOT NULL DEFAULT 0, -- kept in sync from reviews (fast to read)
    review_count INT            NOT NULL DEFAULT 0,
    status       VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    version      BIGINT         NOT NULL DEFAULT 0, -- optimistic locking (Lesson 6)
    created_at   DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at   DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at   DATETIME(6)   NULL,                -- soft delete: NULL = live
    PRIMARY KEY (id),
    UNIQUE KEY uk_products_sku (sku),
    UNIQUE KEY uk_products_slug (slug),
    KEY          idx_products_category (category_id),
    KEY          idx_products_price (price),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT ck_products_price CHECK (price >= 0),
    CONSTRAINT ck_products_status CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE'))
);

CREATE TABLE product_images
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    product_id BIGINT       NOT NULL,
    url        VARCHAR(500) NOT NULL,
    alt_text   VARCHAR(200) NULL,
    sort_order INT          NOT NULL DEFAULT 0, -- 0 = main image
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_images_order (product_id, sort_order),
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

CREATE TABLE reviews
(
    id         BIGINT  NOT NULL AUTO_INCREMENT,
    product_id BIGINT  NOT NULL,
    user_id    BIGINT  NOT NULL,                              -- lives in another service: no foreign key
    rating     TINYINT NOT NULL,
    title      VARCHAR(150) NULL,
    body       TEXT NULL,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_reviews_product_user (product_id, user_id), -- one review per user per product
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT ck_reviews_rating CHECK (rating BETWEEN 1 AND 5)
);