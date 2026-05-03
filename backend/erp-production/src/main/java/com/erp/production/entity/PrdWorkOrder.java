package com.erp.production.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("prd_work_order")
public class PrdWorkOrder {
    @TableId(type = IdType.AUTO)
    private Long id;private String orderNo; private Long productId; private Long bomId;
    private BigDecimal quantity; private BigDecimal completedQty;
    private LocalDate plannedStart; private LocalDate plannedEnd; private LocalDate actualStart; private LocalDate actualEnd;
    private String workshop; private Integer status; private Long createdBy; private LocalDateTime createdAt;
}