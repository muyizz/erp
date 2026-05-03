package com.erp.sales.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("sale_delivery")
public class SaleDelivery {
    @TableId(type = IdType.AUTO)
    private Long id;private String deliveryNo; private Long orderId; private Long customerId;
    private Long warehouseId; private LocalDate deliveryDate; private BigDecimal totalAmount; private Integer status;
    private Long createdBy; private LocalDateTime createdAt;
}