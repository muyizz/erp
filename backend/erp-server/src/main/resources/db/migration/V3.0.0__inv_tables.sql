CREATE TABLE IF NOT EXISTS inv_stock (
    id BIGSERIAL PRIMARY KEY,
    material_id BIGINT NOT NULL, warehouse_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) DEFAULT 0, locked_qty DECIMAL(18,4) DEFAULT 0,
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(material_id, warehouse_id)
);
CREATE TABLE IF NOT EXISTS inv_stock_in (
    id BIGSERIAL PRIMARY KEY, doc_no VARCHAR(50) NOT NULL,
    doc_type SMALLINT NOT NULL, source_id BIGINT,
    warehouse_id BIGINT NOT NULL, in_date DATE NOT NULL,
    created_by BIGINT, created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS inv_stock_in_item (
    id BIGSERIAL PRIMARY KEY, stock_in_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL, quantity DECIMAL(18,4) NOT NULL, unit_cost DECIMAL(18,2)
);
CREATE TABLE IF NOT EXISTS inv_stock_out (
    id BIGSERIAL PRIMARY KEY, doc_no VARCHAR(50) NOT NULL,
    doc_type SMALLINT NOT NULL, source_id BIGINT,
    warehouse_id BIGINT NOT NULL, out_date DATE NOT NULL,
    created_by BIGINT, created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS inv_stock_out_item (
    id BIGSERIAL PRIMARY KEY, stock_out_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL, quantity DECIMAL(18,4) NOT NULL, unit_cost DECIMAL(18,2)
);
CREATE TABLE IF NOT EXISTS inv_transfer (
    id BIGSERIAL PRIMARY KEY, transfer_no VARCHAR(50) UNIQUE NOT NULL,
    from_warehouse BIGINT NOT NULL, to_warehouse BIGINT NOT NULL,
    transfer_date DATE NOT NULL, status SMALLINT DEFAULT 1,
    remark VARCHAR(500), created_by BIGINT, created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS inv_transfer_item (
    id BIGSERIAL PRIMARY KEY, transfer_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL, quantity DECIMAL(18,4) NOT NULL
);
CREATE TABLE IF NOT EXISTS inv_check (
    id BIGSERIAL PRIMARY KEY, check_no VARCHAR(50) UNIQUE NOT NULL,
    warehouse_id BIGINT NOT NULL, check_date DATE NOT NULL,
    status SMALLINT DEFAULT 1, checked_by BIGINT,
    remark VARCHAR(500), created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS inv_check_item (
    id BIGSERIAL PRIMARY KEY, check_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    book_qty DECIMAL(18,4), actual_qty DECIMAL(18,4), diff_qty DECIMAL(18,4)
);
