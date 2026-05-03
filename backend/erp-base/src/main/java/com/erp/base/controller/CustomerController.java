package com.erp.base.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.base.entity.BaseCustomer;
import com.erp.base.mapper.BaseCustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/base/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final BaseCustomerMapper mapper;
    @GetMapping
    public R<PageResult<BaseCustomer>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String customerCode,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<BaseCustomer>()
                .like(customerCode != null, BaseCustomer::getCustomerCode, customerCode)
                .like(customerName != null, BaseCustomer::getCustomerName, customerName)
                .eq(status != null, BaseCustomer::getStatus, status);
        Page<BaseCustomer> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<BaseCustomer> detail(@PathVariable Long id) { return R.ok(mapper.selectById(id)); }
    @PostMapping public R<Void> create(@RequestBody BaseCustomer e) { mapper.insert(e); return R.ok(); }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody BaseCustomer e) { e.setId(id); mapper.updateById(e); return R.ok(); }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { BaseCustomer e = new BaseCustomer(); e.setId(id); e.setStatus(0); mapper.updateById(e); return R.ok(); }
}