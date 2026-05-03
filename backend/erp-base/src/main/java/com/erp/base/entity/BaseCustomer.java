package com.erp.base.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
@Data
@TableName("base_customer")
public class BaseCustomer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String customerCode; private String customerName; private String contactPerson;
    private String contactPhone; private String address;
    private BigDecimal creditLimit;
    private Integer status; private String remark;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}