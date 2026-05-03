package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private String name;
    private Integer type;
    private String path;
    private String component;
    private String permissionCode;
    private String icon;
    private Integer sortOrder;
    private Integer visible;
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<SysMenu> children;
}
