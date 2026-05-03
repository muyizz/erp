package com.erp.finance.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("fin_invoice")
public class FinInvoice {
    @TableId(type = IdType.AUTO)
    private Long id;private String invoiceNo; private Integer invoiceType; private Integer sourceType;
    private Long sourceId; private Long companyId; private LocalDate invoiceDate; private LocalDate dueDate;
    private BigDecimal amount; private BigDecimal paidAmount; private BigDecimal taxAmount; private Integer status; private LocalDateTime createdAt;
}