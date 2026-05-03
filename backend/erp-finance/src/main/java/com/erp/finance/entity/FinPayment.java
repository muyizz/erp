package com.erp.finance.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("fin_payment")
public class FinPayment {
    @TableId(type = IdType.AUTO)
    private Long id;private String paymentNo; private Integer paymentType; private Long companyId;
    private Long invoiceId; private BigDecimal amount; private Integer paymentMethod;
    private LocalDate paymentDate; private String bankAccount; private Integer status; private Long createdBy; private LocalDateTime createdAt;
}