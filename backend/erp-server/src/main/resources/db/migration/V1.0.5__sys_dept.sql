CREATE TABLE sys_dept (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(50) NOT NULL,
    sort_order INT DEFAULT 0,
    leader VARCHAR(50),
    phone VARCHAR(20),
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);