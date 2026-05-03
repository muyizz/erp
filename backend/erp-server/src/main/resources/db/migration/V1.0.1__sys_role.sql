CREATE TABLE sys_role (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    role_code VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(200),
    status SMALLINT DEFAULT 1,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);