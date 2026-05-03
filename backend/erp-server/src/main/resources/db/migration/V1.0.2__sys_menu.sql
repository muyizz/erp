CREATE TABLE sys_menu (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(50) NOT NULL,
    type SMALLINT NOT NULL,
    path VARCHAR(200),
    component VARCHAR(200),
    permission_code VARCHAR(100),
    icon VARCHAR(50),
    sort_order INT DEFAULT 0,
    visible SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT NOW()
);