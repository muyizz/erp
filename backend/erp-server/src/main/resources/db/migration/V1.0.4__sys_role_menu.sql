CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL REFERENCES sys_role(id) ON DELETE CASCADE,
    menu_id BIGINT NOT NULL REFERENCES sys_menu(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, menu_id)
);