CREATE TABLE IF NOT EXISTS sale_order (
    id BIGSERIAL PRIMARY KEY, order_no VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL, order_date DATE NOT NULL, delivery_date DATE,
    total_amount DECIMAL(18,2) DEFAULT 0, status SMALLINT DEFAULT 1,
    approved_by BIGINT, remark VARCHAR(500), created_by BIGINT,
    created_at TIMESTAMP DEFAULT NOW(), updated_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS sale_order_item (
    id BIGSERIAL PRIMARY KEY, order_id BIGINT NOT NULL, line_no INT NOT NULL,
    material_id BIGINT NOT NULL, quantity DECIMAL(18,4) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL, amount DECIMAL(18,2) NOT NULL,
    delivered_qty DECIMAL(18,4) DEFAULT 0
);
CREATE TABLE IF NOT EXISTS sale_delivery (
    id BIGSERIAL PRIMARY KEY, delivery_no VARCHAR(50) UNIQUE NOT NULL,
    order_id BIGINT, customer_id BIGINT NOT NULL, warehouse_id BIGINT NOT NULL,
    delivery_date DATE NOT NULL, total_amount DECIMAL(18,2) DEFAULT 0,
    status SMALLINT DEFAULT 1, created_by BIGINT, created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS sale_delivery_item (
    id BIGSERIAL PRIMARY KEY, delivery_id BIGINT NOT NULL, order_item_id BIGINT,
    material_id BIGINT NOT NULL, quantity DECIMAL(18,4) NOT NULL,
    unit_price DECIMAL(18,2), amount DECIMAL(18,2)
);
CREATE TABLE IF NOT EXISTS sale_return (
    id BIGSERIAL PRIMARY KEY, return_no VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL, order_id BIGINT,
    return_date DATE NOT NULL, total_amount DECIMAL(18,2) DEFAULT 0,
    status SMALLINT DEFAULT 1, reason VARCHAR(500),
    created_by BIGINT, created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS sale_return_item (
    id BIGSERIAL PRIMARY KEY, return_id BIGINT NOT NULL, material_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) NOT NULL, unit_price DECIMAL(18,2), amount DECIMAL(18,2)
);
