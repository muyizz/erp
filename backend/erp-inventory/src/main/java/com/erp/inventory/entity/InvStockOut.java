package com.erp.inventory.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("inv_stock_out")
public class InvStockOut {
    @TableId(type = IdType.AUTO)
    private Long id;private String docNo; private Integer docType; private Long sourceId;
    private Long warehouseId; private LocalDate outDate; private Long createdBy; private LocalDateTime createdAt;
}