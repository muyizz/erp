package com.erp.base.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;import java.util.List;
@Data
@TableName("base_material_category")
public class BaseMaterialCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId; private String name; private Integer sortOrder;
    @TableField(exist = false) private List<BaseMaterialCategory> children;
}