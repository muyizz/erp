package com.erp.inventory.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("inv_check")
public class InvCheck {
    @TableId(type = IdType.AUTO)
    private Long id;private String checkNo; private Long warehouseId; private LocalDate checkDate;
    private Integer status; private Long checkedBy; private String remark; private LocalDateTime createdAt;
}