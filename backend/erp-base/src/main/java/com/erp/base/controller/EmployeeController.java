package com.erp.base.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.base.entity.BaseEmployee;
import com.erp.base.mapper.BaseEmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/base/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final BaseEmployeeMapper mapper;
    @GetMapping
    public R<PageResult<BaseEmployee>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String employeeName,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<BaseEmployee>()
                .like(employeeName != null, BaseEmployee::getEmployeeName, employeeName)
                .eq(deptId != null, BaseEmployee::getDeptId, deptId)
                .eq(status != null, BaseEmployee::getStatus, status);
        Page<BaseEmployee> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<BaseEmployee> detail(@PathVariable Long id) { return R.ok(mapper.selectById(id)); }
    @PostMapping public R<Void> create(@RequestBody BaseEmployee e) { mapper.insert(e); return R.ok(); }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody BaseEmployee e) { e.setId(id); mapper.updateById(e); return R.ok(); }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { BaseEmployee e = new BaseEmployee(); e.setId(id); e.setStatus(0); mapper.updateById(e); return R.ok(); }
}