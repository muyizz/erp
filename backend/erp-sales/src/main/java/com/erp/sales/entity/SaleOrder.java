package com.erp.sales.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("sale_order")
public class SaleOrder {
    @TableId(type = IdType.AUTO)
    private Long id;private String orderNo; private Long customerId; private LocalDate orderDate;
    private LocalDate deliveryDate; private BigDecimal totalAmount; private Integer status; private Long approvedBy;
    private String remark; private Long createdBy; private LocalDateTime createdAt; private LocalDateTime updatedAt;
}