package com.erp.production.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("prd_process_step")
public class PrdProcessStep {
    @TableId(type = IdType.AUTO)
    private Long id;private Long processId; private Integer stepNo;
    private String stepName; private String workCenter; private BigDecimal standardHours;
    private String description; private Integer sortOrder;
}