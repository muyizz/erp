package com.erp.production.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("prd_work_order_material")
public class PrdWorkOrderMaterial {
    @TableId(type = IdType.AUTO)
    private Long id;private Long workOrderId; private Long materialId;
    private BigDecimal requiredQty; private BigDecimal issuedQty; private Long warehouseId;
}