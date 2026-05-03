package com.erp.base.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.*;
import java.math.BigDecimal;
@Data
@TableName("base_warehouse")
public class BaseWarehouse {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String warehouseCode; private String warehouseName; private String address;
    private String manager; private String phone; private Integer status;
}