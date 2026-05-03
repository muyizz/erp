package com.erp.production.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("prd_bom_item")
public class PrdBomItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long bomId; private Long materialId;
    private BigDecimal quantity; private String unit; private BigDecimal scrapRate; private Integer sortOrder;
}