-- user_db: profile, addresses, wishlist. user_id = users.id in auth_db (no cross-database foreign key)

CREATE TABLE profiles (
    user_id     BIGINT       NOT NULL,
    full_name   VARCHAR(150) NOT NULL,
    phone       VARCHAR(20)  NULL,
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (user_id)
);

CREATE TABLE addresses (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    user_id      BIGINT       NOT NULL,
    label        VARCHAR(30)  NULL,                  -- "Home", "Office"
    full_name    VARCHAR(150) NOT NULL,
    phone        VARCHAR(20)  NOT NULL,
    line1        VARCHAR(200) NOT NULL,
    line2        VARCHAR(200) NULL,
    city         VARCHAR(100) NOT NULL,
    state        VARCHAR(100) NOT NULL,
    postal_code  VARCHAR(12)  NOT NULL,
    country      CHAR(2)      NOT NULL DEFAULT 'IN',
    is_default   BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at   DATETIME(6)  NULL,                  -- soft delete: old orders still show it
    PRIMARY KEY (id),
    KEY idx_addresses_user (user_id)
);

CREATE TABLE wishlist_items (
    user_id     BIGINT      NOT NULL,
    product_id  BIGINT      NOT NULL,                -- lives in product_db
    added_at    DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (user_id, product_id)
);
