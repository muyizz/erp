package com.erp.base.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
@Data
@TableName("base_employee")
public class BaseEmployee {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String employeeCode; private String employeeName; private Long deptId;
    private String position; private String phone; private String email;
    private LocalDate hireDate; private Integer status;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}