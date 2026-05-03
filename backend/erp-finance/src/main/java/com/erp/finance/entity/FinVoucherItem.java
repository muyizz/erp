package com.erp.finance.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("fin_voucher_item")
public class FinVoucherItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long voucherId; private Long accountId;
    private BigDecimal debit; private BigDecimal credit; private String summary;
}