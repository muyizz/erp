package com.erp.common.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {
    ENABLED(1, "启用"),
    DISABLED(0, "禁用");

    private final int code;
    private final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
