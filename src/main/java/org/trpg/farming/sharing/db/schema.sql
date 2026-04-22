CREATE TABLE IF NOT EXISTS sharing_product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_harvest_batch_id BIGINT NOT NULL UNIQUE,
    owner_user_id BIGINT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    origin_facility_id BIGINT NOT NULL,
    quantity DECIMAL(12,2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    quality_level VARCHAR(20),
    trace_code VARCHAR(64) NOT NULL UNIQUE,
    trace_snapshot_json TEXT,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE INDEX idx_sharing_product_status_created
    ON sharing_product(status, created_at);

CREATE TABLE IF NOT EXISTS sharing_listing (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    share_product_id BIGINT NOT NULL,
    title VARCHAR(120) NOT NULL,
    coin_price DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    published_at DATETIME NOT NULL,
    expired_at DATETIME NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_sharing_listing_product FOREIGN KEY (share_product_id) REFERENCES sharing_product(id)
);

CREATE INDEX idx_sharing_listing_status_time
    ON sharing_listing(status, published_at);

CREATE TABLE IF NOT EXISTS sharing_coin_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS sharing_coin_ledger (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    change_type VARCHAR(20) NOT NULL,
    change_amount DECIMAL(12,2) NOT NULL,
    balance_after DECIMAL(12,2) NOT NULL,
    biz_type VARCHAR(30) NOT NULL,
    biz_id BIGINT NULL,
    remark VARCHAR(255),
    created_at DATETIME NOT NULL
);

CREATE INDEX idx_sharing_coin_ledger_user_created
    ON sharing_coin_ledger(user_id, created_at);

CREATE TABLE IF NOT EXISTS sharing_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    listing_id BIGINT NOT NULL,
    listing_title_snapshot VARCHAR(120) NOT NULL,
    product_name_snapshot VARCHAR(100) NOT NULL,
    buyer_user_id BIGINT NOT NULL,
    seller_user_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    coin_amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    shipping_status VARCHAR(20) NOT NULL,
    shipping_no VARCHAR(64),
    created_at DATETIME NOT NULL,
    completed_at DATETIME NULL,
    CONSTRAINT fk_sharing_order_listing FOREIGN KEY (listing_id) REFERENCES sharing_listing(id)
);

CREATE INDEX idx_sharing_order_buyer_status
    ON sharing_order(buyer_user_id, status);

CREATE INDEX idx_sharing_order_seller_status
    ON sharing_order(seller_user_id, status);
