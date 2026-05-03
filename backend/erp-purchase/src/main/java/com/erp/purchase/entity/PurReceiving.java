package com.erp.purchase.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("pur_receiving")
public class PurReceiving {
    @TableId(type = IdType.AUTO)
    private Long id;private String receivingNo; private Long orderId; private Long supplierId;
    private Long warehouseId; private LocalDate receivingDate; private BigDecimal totalAmount; private Integer status;
    private String remark; private Long createdBy; private LocalDateTime createdAt;
}