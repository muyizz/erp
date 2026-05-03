package com.erp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.system.entity.SysOperationLog;
import com.erp.system.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/system/logs")
@RequiredArgsConstructor
public class LogController {

    private final SysOperationLogMapper sysOperationLogMapper;

    @GetMapping
    public R<PageResult<SysOperationLog>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        var wrapper = new LambdaQueryWrapper<SysOperationLog>()
                .like(module != null, SysOperationLog::getModule, module)
                .eq(operation != null, SysOperationLog::getOperation, operation)
                .like(username != null, SysOperationLog::getUsername, username)
                .ge(startTime != null, SysOperationLog::getCreatedAt, startTime)
                .le(endTime != null, SysOperationLog::getCreatedAt, endTime)
                .orderByDesc(SysOperationLog::getCreatedAt);
        Page<SysOperationLog> p = sysOperationLogMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }

    @GetMapping("/{id}")
    public R<SysOperationLog> detail(@PathVariable Long id) {
        return R.ok(sysOperationLogMapper.selectById(id));
    }
}
