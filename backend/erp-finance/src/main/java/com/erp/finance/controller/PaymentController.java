package com.erp.finance.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.finance.entity.FinPayment;
import com.erp.finance.mapper.FinPaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/finance/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final FinPaymentMapper mapper;
    @GetMapping
    public R<PageResult<FinPayment>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Integer paymentType,
            @RequestParam(required = false) Long companyId, @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<FinPayment>()
                .eq(paymentType != null, FinPayment::getPaymentType, paymentType)
                .eq(companyId != null, FinPayment::getCompanyId, companyId)
                .eq(status != null, FinPayment::getStatus, status).orderByDesc(FinPayment::getCreatedAt);
        Page<FinPayment> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<FinPayment> detail(@PathVariable Long id) { return R.ok(mapper.selectById(id)); }
    @PostMapping public R<Void> create(@RequestBody FinPayment e) { mapper.insert(e); return R.ok(); }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody FinPayment e) { e.setId(id); mapper.updateById(e); return R.ok(); }
}