CREATE TABLE sys_dict_type (
    id BIGSERIAL PRIMARY KEY,
    dict_name VARCHAR(100) NOT NULL,
    dict_code VARCHAR(100) UNIQUE NOT NULL,
    status SMALLINT DEFAULT 1
);

CREATE TABLE sys_dict_item (
    id BIGSERIAL PRIMARY KEY,
    dict_type_id BIGINT REFERENCES sys_dict_type(id) ON DELETE CASCADE,
    label VARCHAR(100) NOT NULL,
    value VARCHAR(100) NOT NULL,
    sort_order INT DEFAULT 0,
    status SMALLINT DEFAULT 1,
    css_class VARCHAR(50)
);