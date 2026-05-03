package com.erp.base.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.base.entity.BaseWarehouse;
import com.erp.base.mapper.BaseWarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/base/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final BaseWarehouseMapper mapper;
    @GetMapping
    public R<PageResult<BaseWarehouse>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<BaseWarehouse>()
                .like(warehouseName != null, BaseWarehouse::getWarehouseName, warehouseName)
                .eq(status != null, BaseWarehouse::getStatus, status);
        Page<BaseWarehouse> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<BaseWarehouse> detail(@PathVariable Long id) { return R.ok(mapper.selectById(id)); }
    @PostMapping public R<Void> create(@RequestBody BaseWarehouse e) { mapper.insert(e); return R.ok(); }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody BaseWarehouse e) { e.setId(id); mapper.updateById(e); return R.ok(); }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { BaseWarehouse e = new BaseWarehouse(); e.setId(id); e.setStatus(0); mapper.updateById(e); return R.ok(); }
}