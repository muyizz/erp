package com.erp.finance.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("fin_expense")
public class FinExpense {
    @TableId(type = IdType.AUTO)
    private Long id;private String expenseNo; private Long employeeId; private LocalDate expenseDate;
    private BigDecimal totalAmount; private Integer category; private Integer status;
    private Long submittedBy; private Long approvedBy; private String remark; private LocalDateTime createdAt;
}