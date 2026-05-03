package com.erp.sales.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("sale_order_item")
public class SaleOrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long orderId; private Integer lineNo; private Long materialId;
    private BigDecimal quantity; private BigDecimal unitPrice; private BigDecimal amount; private BigDecimal deliveredQty;
}