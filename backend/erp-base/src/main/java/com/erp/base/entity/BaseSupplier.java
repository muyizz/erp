package com.erp.base.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
@Data
@TableName("base_supplier")
public class BaseSupplier {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String supplierCode; private String supplierName; private String contactPerson;
    private String contactPhone; private String contactEmail; private String address;
    private String bankName; private String bankAccount; private String taxId;
    private Integer status; private String remark;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}