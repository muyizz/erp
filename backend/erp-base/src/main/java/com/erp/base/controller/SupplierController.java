package com.erp.base.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.base.entity.BaseSupplier;
import com.erp.base.mapper.BaseSupplierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/base/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final BaseSupplierMapper mapper;
    @GetMapping
    public R<PageResult<BaseSupplier>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String supplierCode,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<BaseSupplier>()
                .like(supplierCode != null, BaseSupplier::getSupplierCode, supplierCode)
                .like(supplierName != null, BaseSupplier::getSupplierName, supplierName)
                .eq(status != null, BaseSupplier::getStatus, status);
        Page<BaseSupplier> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<BaseSupplier> detail(@PathVariable Long id) { return R.ok(mapper.selectById(id)); }
    @PostMapping public R<Void> create(@RequestBody BaseSupplier e) { mapper.insert(e); return R.ok(); }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody BaseSupplier e) { e.setId(id); mapper.updateById(e); return R.ok(); }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { BaseSupplier e = new BaseSupplier(); e.setId(id); e.setStatus(0); mapper.updateById(e); return R.ok(); }
}