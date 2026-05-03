package com.erp.base.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
@Data
@TableName("base_material")
public class BaseMaterial {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String materialCode; private String materialName; private Long categoryId;
    private String spec; private String unit;
    private BigDecimal salePrice; private BigDecimal purchasePrice;
    private BigDecimal safetyStock;
    private Integer status;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}