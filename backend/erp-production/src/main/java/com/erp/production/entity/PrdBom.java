package com.erp.production.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("prd_bom")
public class PrdBom {
    @TableId(type = IdType.AUTO)
    private Long id;private String bomCode; private Long productId; private String version;
    private BigDecimal quantity; private Integer status; private LocalDate effectiveDate;
    private LocalDate expiryDate; private Long createdBy; private LocalDateTime createdAt;
    @TableField(exist = false) private List<PrdBomItem> items;
}