package com.erp.sales.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("sale_return")
public class SaleReturn {
    @TableId(type = IdType.AUTO)
    private Long id;private String returnNo; private Long customerId; private Long orderId;
    private LocalDate returnDate; private BigDecimal totalAmount; private Integer status;
    private String reason; private Long createdBy; private LocalDateTime createdAt;
}