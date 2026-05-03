package com.erp.inventory.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("inv_transfer")
public class InvTransfer {
    @TableId(type = IdType.AUTO)
    private Long id;private String transferNo; private Long fromWarehouse; private Long toWarehouse;
    private LocalDate transferDate; private Integer status; private String remark; private Long createdBy; private LocalDateTime createdAt;
}