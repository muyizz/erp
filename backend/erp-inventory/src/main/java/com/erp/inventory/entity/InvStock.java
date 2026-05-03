package com.erp.inventory.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("inv_stock")
public class InvStock {
    @TableId(type = IdType.AUTO)
    private Long id;private Long materialId; private Long warehouseId;
    private BigDecimal quantity; private BigDecimal lockedQty; private LocalDateTime updatedAt;
}