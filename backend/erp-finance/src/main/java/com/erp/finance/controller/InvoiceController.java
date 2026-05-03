package com.erp.finance.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.finance.entity.FinInvoice;
import com.erp.finance.mapper.FinInvoiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/finance/invoices")
@RequiredArgsConstructor
public class InvoiceController {
    private final FinInvoiceMapper mapper;
    @GetMapping
    public R<PageResult<FinInvoice>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Integer invoiceType,
            @RequestParam(required = false) Long companyId, @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<FinInvoice>()
                .eq(invoiceType != null, FinInvoice::getInvoiceType, invoiceType)
                .eq(companyId != null, FinInvoice::getCompanyId, companyId)
                .eq(status != null, FinInvoice::getStatus, status).orderByDesc(FinInvoice::getCreatedAt);
        Page<FinInvoice> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<FinInvoice> detail(@PathVariable Long id) { return R.ok(mapper.selectById(id)); }
    @PostMapping public R<Void> create(@RequestBody FinInvoice e) { mapper.insert(e); return R.ok(); }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody FinInvoice e) { e.setId(id); mapper.updateById(e); return R.ok(); }
}