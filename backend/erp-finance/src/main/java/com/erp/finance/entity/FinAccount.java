package com.erp.finance.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
import java.util.List;
@Data
@TableName("fin_account")
public class FinAccount {
    @TableId(type = IdType.AUTO)
    private Long id;private String accountCode; private String accountName; private Long parentId;
    private Integer accountType; private BigDecimal balance; private Integer status;
    @TableField(exist = false) private List<FinAccount> children;
}