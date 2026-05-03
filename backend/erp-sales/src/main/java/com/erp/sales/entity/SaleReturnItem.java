package com.erp.sales.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("sale_return_item")
public class SaleReturnItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long returnId; private Long materialId;
    private BigDecimal quantity; private BigDecimal unitPrice; private BigDecimal amount;
}