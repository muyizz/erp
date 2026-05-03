package com.erp.common.enums;

import lombok.Getter;

@Getter
public enum BusinessType {
    INSERT(1, "新增"),
    UPDATE(2, "修改"),
    DELETE(3, "删除"),
    IMPORT(4, "导入"),
    EXPORT(5, "导出"),
    OTHER(6, "其他");

    private final int code;
    private final String desc;

    BusinessType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
