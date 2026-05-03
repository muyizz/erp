CREATE TABLE IF NOT EXISTS base_supplier (
    id BIGSERIAL PRIMARY KEY, supplier_code VARCHAR(50) UNIQUE NOT NULL,
    supplier_name VARCHAR(100) NOT NULL, contact_person VARCHAR(50),
    contact_phone VARCHAR(20), contact_email VARCHAR(100),
    address VARCHAR(200), bank_name VARCHAR(100), bank_account VARCHAR(50),
    tax_id VARCHAR(50), status SMALLINT DEFAULT 1,
    remark VARCHAR(500), created_at TIMESTAMP DEFAULT NOW(), updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS base_customer (
    id BIGSERIAL PRIMARY KEY, customer_code VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(100) NOT NULL, contact_person VARCHAR(50),
    contact_phone VARCHAR(20), address VARCHAR(200),
    credit_limit DECIMAL(18,2) DEFAULT 0,
    status SMALLINT DEFAULT 1, remark VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW(), updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS base_material_category (
    id BIGSERIAL PRIMARY KEY, parent_id BIGINT DEFAULT 0,
    name VARCHAR(50) NOT NULL, sort_order INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS base_material (
    id BIGSERIAL PRIMARY KEY, material_code VARCHAR(50) UNIQUE NOT NULL,
    material_name VARCHAR(100) NOT NULL, category_id BIGINT,
    spec VARCHAR(200), unit VARCHAR(20) NOT NULL DEFAULT '个',
    sale_price DECIMAL(18,2) DEFAULT 0, purchase_price DECIMAL(18,2) DEFAULT 0,
    safety_stock DECIMAL(18,4) DEFAULT 0, status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT NOW(), updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS base_warehouse (
    id BIGSERIAL PRIMARY KEY, warehouse_code VARCHAR(50) UNIQUE NOT NULL,
    warehouse_name VARCHAR(100) NOT NULL, address VARCHAR(200),
    manager VARCHAR(50), phone VARCHAR(20), status SMALLINT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS base_employee (
    id BIGSERIAL PRIMARY KEY, employee_code VARCHAR(50) UNIQUE NOT NULL,
    employee_name VARCHAR(50) NOT NULL, dept_id BIGINT,
    position VARCHAR(50), phone VARCHAR(20), email VARCHAR(100),
    hire_date DATE, status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT NOW(), updated_at TIMESTAMP DEFAULT NOW()
);
