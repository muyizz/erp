CREATE TABLE IF NOT EXISTS prd_bom (
    id BIGSERIAL PRIMARY KEY, bom_code VARCHAR(50) UNIQUE NOT NULL,
    product_id BIGINT NOT NULL, version VARCHAR(20) NOT NULL,
    quantity DECIMAL(18,4) DEFAULT 1, status SMALLINT DEFAULT 1,
    effective_date DATE, expiry_date DATE,
    created_by BIGINT, created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(product_id, version)
);
CREATE TABLE IF NOT EXISTS prd_bom_item (
    id BIGSERIAL PRIMARY KEY, bom_id BIGINT NOT NULL, material_id BIGINT NOT NULL,
    quantity DECIMAL(18,4) NOT NULL, unit VARCHAR(20),
    scrap_rate DECIMAL(5,2) DEFAULT 0, sort_order INT DEFAULT 0
);
CREATE TABLE IF NOT EXISTS prd_work_order (
    id BIGSERIAL PRIMARY KEY, order_no VARCHAR(50) UNIQUE NOT NULL,
    product_id BIGINT NOT NULL, bom_id BIGINT,
    quantity DECIMAL(18,4) NOT NULL, completed_qty DECIMAL(18,4) DEFAULT 0,
    planned_start DATE, planned_end DATE, actual_start DATE, actual_end DATE,
    workshop VARCHAR(100), status SMALLINT DEFAULT 1,
    created_by BIGINT, created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS prd_work_order_material (
    id BIGSERIAL PRIMARY KEY, work_order_id BIGINT NOT NULL, material_id BIGINT NOT NULL,
    required_qty DECIMAL(18,4) NOT NULL, issued_qty DECIMAL(18,4) DEFAULT 0,
    warehouse_id BIGINT
);
CREATE TABLE IF NOT EXISTS prd_process (
    id BIGSERIAL PRIMARY KEY, process_code VARCHAR(50) UNIQUE NOT NULL,
    process_name VARCHAR(100) NOT NULL, product_id BIGINT NOT NULL,
    description VARCHAR(500), created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS prd_process_step (
    id BIGSERIAL PRIMARY KEY, process_id BIGINT NOT NULL, step_no INT NOT NULL,
    step_name VARCHAR(100) NOT NULL, work_center VARCHAR(100),
    standard_hours DECIMAL(10,2), description VARCHAR(500), sort_order INT DEFAULT 0
);
