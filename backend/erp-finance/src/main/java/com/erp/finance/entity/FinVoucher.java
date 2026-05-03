package com.erp.finance.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("fin_voucher")
public class FinVoucher {
    @TableId(type = IdType.AUTO)
    private Long id;private String voucherNo; private LocalDate voucherDate; private Integer voucherType;
    private Integer sourceType; private Long sourceId; private BigDecimal totalDebit; private BigDecimal totalCredit;
    private Integer status; private String summary; private Long createdBy; private LocalDateTime createdAt; private LocalDateTime postedAt;
}