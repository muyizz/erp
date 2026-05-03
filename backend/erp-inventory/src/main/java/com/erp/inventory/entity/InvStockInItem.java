package com.erp.inventory.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("inv_stock_in_item")
public class InvStockInItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long stockInId; private Long materialId;
    private BigDecimal quantity; private BigDecimal unitCost;
}