package com.erp.purchase.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("pur_receiving_item")
public class PurReceivingItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long receivingId; private Long orderItemId;
    private Long materialId; private BigDecimal quantity; private BigDecimal unitPrice; private BigDecimal amount;
}