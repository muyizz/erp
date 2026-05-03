package com.erp.purchase.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("pur_order")
public class PurOrder {
    @TableId(type = IdType.AUTO)
    private Long id;private String orderNo; private Long supplierId; private LocalDate orderDate;
    private LocalDate expectedDate; private BigDecimal totalAmount; private Integer status; private Long approvedBy;
    private String remark; private Long createdBy; private LocalDateTime createdAt; private LocalDateTime updatedAt;
}