package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_dict_item")
public class SysDictItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dictTypeId;
    private String label;
    private String value;
    private Integer sortOrder;
    private Integer status;
    private String cssClass;
}
