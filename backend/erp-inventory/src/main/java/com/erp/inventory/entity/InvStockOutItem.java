package com.erp.inventory.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("inv_stock_out_item")
public class InvStockOutItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long stockOutId; private Long materialId;
    private BigDecimal quantity; private BigDecimal unitCost;
}