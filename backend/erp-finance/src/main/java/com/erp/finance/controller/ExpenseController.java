package com.erp.finance.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.finance.entity.*;
import com.erp.finance.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1/finance/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final FinExpenseMapper mapper;
    private final FinExpenseItemMapper itemMapper;
    @GetMapping
    public R<PageResult<FinExpense>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<FinExpense>()
                .eq(employeeId != null, FinExpense::getEmployeeId, employeeId)
                .eq(status != null, FinExpense::getStatus, status).orderByDesc(FinExpense::getCreatedAt);
        Page<FinExpense> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        r.put("header", mapper.selectById(id));
        r.put("items", itemMapper.selectList(new LambdaQueryWrapper<FinExpenseItem>().eq(FinExpenseItem::getExpenseId, id)));
        return R.ok(r);
    }
    @PostMapping public R<Void> create(@RequestBody FinExpense e) { e.setExpenseNo("EXP-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000); e.setStatus(1); mapper.insert(e); return R.ok(); }
    @PostMapping("/{id}/submit") public R<Void> submit(@PathVariable Long id) { FinExpense e = new FinExpense(); e.setId(id); e.setStatus(2); mapper.updateById(e); return R.ok(); }
    @PostMapping("/{id}/approve") public R<Void> approve(@PathVariable Long id) { FinExpense e = new FinExpense(); e.setId(id); e.setStatus(3); mapper.updateById(e); return R.ok(); }
    @PostMapping("/{id}/reimburse") public R<Void> reimburse(@PathVariable Long id) { FinExpense e = new FinExpense(); e.setId(id); e.setStatus(4); mapper.updateById(e); return R.ok(); }
}