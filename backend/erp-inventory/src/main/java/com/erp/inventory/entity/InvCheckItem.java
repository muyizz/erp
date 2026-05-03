package com.erp.inventory.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("inv_check_item")
public class InvCheckItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long checkId; private Long materialId;
    private BigDecimal bookQty; private BigDecimal actualQty; private BigDecimal diffQty;
}