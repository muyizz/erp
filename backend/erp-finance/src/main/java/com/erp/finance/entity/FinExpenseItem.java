package com.erp.finance.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("fin_expense_item")
public class FinExpenseItem {
    @TableId(type = IdType.AUTO)
    private Long id;private Long expenseId; private String description;
    private BigDecimal amount; private String attachmentUrl;
}