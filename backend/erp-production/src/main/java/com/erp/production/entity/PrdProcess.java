package com.erp.production.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("prd_process")
public class PrdProcess {
    @TableId(type = IdType.AUTO)
    private Long id;private String processCode; private String processName; private Long productId;
    private String description; private LocalDateTime createdAt;
    @TableField(exist = false) private List<PrdProcessStep> steps;
}