package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_operation_log")
public class SysOperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String username;
    private String module;
    private String operation;
    private String requestMethod;
    private String requestUrl;
    private String requestParams;
    private String responseResult;
    private String ip;
    private Long durationMs;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
