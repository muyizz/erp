CREATE TABLE IF NOT EXISTS fin_account (
    id BIGSERIAL PRIMARY KEY, account_code VARCHAR(50) UNIQUE NOT NULL,
    account_name VARCHAR(100) NOT NULL, parent_id BIGINT DEFAULT 0,
    account_type SMALLINT, balance DECIMAL(18,2) DEFAULT 0, status SMALLINT DEFAULT 1
);
CREATE TABLE IF NOT EXISTS fin_voucher (
    id BIGSERIAL PRIMARY KEY, voucher_no VARCHAR(50) UNIQUE NOT NULL,
    voucher_date DATE NOT NULL, voucher_type SMALLINT NOT NULL,
    source_type SMALLINT, source_id BIGINT,
    total_debit DECIMAL(18,2) DEFAULT 0, total_credit DECIMAL(18,2) DEFAULT 0,
    status SMALLINT DEFAULT 1, summary VARCHAR(500),
    created_by BIGINT, created_at TIMESTAMP DEFAULT NOW(), posted_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS fin_voucher_item (
    id BIGSERIAL PRIMARY KEY, voucher_id BIGINT NOT NULL, account_id BIGINT NOT NULL,
    debit DECIMAL(18,2) DEFAULT 0, credit DECIMAL(18,2) DEFAULT 0, summary VARCHAR(200)
);
CREATE TABLE IF NOT EXISTS fin_invoice (
    id BIGSERIAL PRIMARY KEY, invoice_no VARCHAR(50) UNIQUE NOT NULL,
    invoice_type SMALLINT NOT NULL, source_type SMALLINT, source_id BIGINT,
    company_id BIGINT NOT NULL, invoice_date DATE NOT NULL, due_date DATE,
    amount DECIMAL(18,2) NOT NULL, paid_amount DECIMAL(18,2) DEFAULT 0,
    tax_amount DECIMAL(18,2) DEFAULT 0, status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS fin_payment (
    id BIGSERIAL PRIMARY KEY, payment_no VARCHAR(50) UNIQUE NOT NULL,
    payment_type SMALLINT NOT NULL, company_id BIGINT NOT NULL, invoice_id BIGINT,
    amount DECIMAL(18,2) NOT NULL, payment_method SMALLINT,
    payment_date DATE NOT NULL, bank_account VARCHAR(50),
    status SMALLINT DEFAULT 1, created_by BIGINT, created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS fin_expense (
    id BIGSERIAL PRIMARY KEY, expense_no VARCHAR(50) UNIQUE NOT NULL,
    employee_id BIGINT NOT NULL, expense_date DATE NOT NULL,
    total_amount DECIMAL(18,2) NOT NULL, category SMALLINT DEFAULT 1,
    status SMALLINT DEFAULT 1, submitted_by BIGINT, approved_by BIGINT,
    remark VARCHAR(500), created_at TIMESTAMP DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS fin_expense_item (
    id BIGSERIAL PRIMARY KEY, expense_id BIGINT NOT NULL,
    description VARCHAR(200), amount DECIMAL(18,2) NOT NULL, attachment_url VARCHAR(255)
);
