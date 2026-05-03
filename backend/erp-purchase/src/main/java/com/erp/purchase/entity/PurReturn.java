package com.erp.purchase.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("pur_return")
public class PurReturn {
    @TableId(type = IdType.AUTO)
    private Long id;private String returnNo; private Long supplierId; private Long orderId;
    private LocalDate returnDate; private BigDecimal totalAmount; private Integer status;
    private String reason; private Long createdBy; private LocalDateTime createdAt;
}