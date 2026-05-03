CREATE TABLE sys_operation_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    module VARCHAR(50),
    operation VARCHAR(50),
    request_method VARCHAR(10),
    request_url VARCHAR(500),
    request_params TEXT,
    response_result TEXT,
    ip VARCHAR(50),
    duration_ms BIGINT,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_oplog_created_at ON sys_operation_log(created_at);
CREATE INDEX idx_oplog_username ON sys_operation_log(username);