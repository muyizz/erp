package com.erp.inventory.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("inv_stock_in")
public class InvStockIn {
    @TableId(type = IdType.AUTO)
    private Long id;private String docNo; private Integer docType; private Long sourceId;
    private Long warehouseId; private LocalDate inDate; private Long createdBy; private LocalDateTime createdAt;
}