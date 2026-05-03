package com.erp.sales.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("sale_delivery_item")
public class SaleDeliveryItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long deliveryId; private Long orderItemId;
    private Long materialId; private BigDecimal quantity; private BigDecimal unitPrice; private BigDecimal amount;
}