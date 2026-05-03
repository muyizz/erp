package com.erp.inventory.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("inv_transfer_item")
public class InvTransferItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long transferId; private Long materialId; private BigDecimal quantity;
}