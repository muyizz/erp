package com.erp.purchase.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("pur_order_item")
public class PurOrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long orderId; private Integer lineNo; private Long materialId;
    private BigDecimal quantity; private BigDecimal unitPrice; private BigDecimal amount;
    private BigDecimal receivedQty; private String remark;
}