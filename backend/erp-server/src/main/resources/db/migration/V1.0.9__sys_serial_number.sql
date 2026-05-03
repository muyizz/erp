CREATE TABLE sys_serial_number (
    id BIGSERIAL PRIMARY KEY,
    prefix VARCHAR(20) NOT NULL,
    date_part VARCHAR(20),
    current_seq BIGINT DEFAULT 0,
    UNIQUE(prefix, date_part)
);