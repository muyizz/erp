package com.erp.finance.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.common.exception.BusinessException;
import com.erp.finance.entity.*;
import com.erp.finance.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
@RestController
@RequestMapping("/api/v1/finance/vouchers")
@RequiredArgsConstructor
public class VoucherController {
    private final FinVoucherMapper mapper;
    private final FinVoucherItemMapper itemMapper;
    @GetMapping
    public R<PageResult<FinVoucher>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Integer voucherType,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<FinVoucher>()
                .eq(voucherType != null, FinVoucher::getVoucherType, voucherType)
                .eq(status != null, FinVoucher::getStatus, status).orderByDesc(FinVoucher::getCreatedAt);
        Page<FinVoucher> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        r.put("header", mapper.selectById(id));
        r.put("items", itemMapper.selectList(new LambdaQueryWrapper<FinVoucherItem>().eq(FinVoucherItem::getVoucherId, id)));
        return R.ok(r);
    }
    @PostMapping public R<Void> create(@RequestBody Map<String,Object> body) {
        FinVoucher h = parse(body.get("header"), FinVoucher.class);
        h.setVoucherNo("VCH-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000);
        h.setStatus(1); mapper.insert(h);
        List<Map<String,Object>> items = (List<Map<String,Object>>) body.get("items");
        if (items != null) {
            BigDecimal totalDr = BigDecimal.ZERO, totalCr = BigDecimal.ZERO;
            for (Map<String,Object> im : items) {
                FinVoucherItem item = parse(im, FinVoucherItem.class); item.setVoucherId(h.getId());
                if (item.getDebit() == null) item.setDebit(BigDecimal.ZERO);
                if (item.getCredit() == null) item.setCredit(BigDecimal.ZERO);
                totalDr = totalDr.add(item.getDebit()); totalCr = totalCr.add(item.getCredit());
                itemMapper.insert(item);
            }
            h.setTotalDebit(totalDr); h.setTotalCredit(totalCr); mapper.updateById(h);
        }
        return R.ok();
    }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { com.erp.finance.entity.FinVoucher e = new com.erp.finance.entity.FinVoucher(); e.setId(id); e.setStatus(3); mapper.updateById(e); return R.ok(); }
    @GetMapping("/{id}/entries") public R<List<com.erp.finance.entity.FinVoucherItem>> entries(@PathVariable Long id) { return R.ok(itemMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.erp.finance.entity.FinVoucherItem>().eq(com.erp.finance.entity.FinVoucherItem::getVoucherId, id))); }
    @PostMapping("/{id}/post") public R<Void> post(@PathVariable Long id) {
        FinVoucher h = mapper.selectById(id);
        if (h.getTotalDebit().compareTo(h.getTotalCredit()) != 0)
            throw new BusinessException("借贷不平衡，无法过账");
        h.setStatus(2); h.setPostedAt(java.time.LocalDateTime.now()); mapper.updateById(h);
        return R.ok();
    }
    private <T> T parse(Object data, Class<T> clz) {
        try {
            T obj = clz.getDeclaredConstructor().newInstance();
            Map<String,Object> m = (Map<String,Object>) data;
            for (java.lang.reflect.Field f : clz.getDeclaredFields()) {
                f.setAccessible(true);
                if (m.containsKey(f.getName()) && m.get(f.getName()) != null) {
                    Object v = m.get(f.getName());
                    if (v instanceof Integer && f.getType() == Long.class) v = ((Integer)v).longValue();
                    if (v instanceof Number && f.getType() == BigDecimal.class) v = BigDecimal.valueOf(((Number)v).doubleValue());
                    if (v instanceof String && f.getType() == java.time.LocalDate.class) v = java.time.LocalDate.parse((String)v);
                    if (v instanceof String && f.getType() == java.time.LocalDateTime.class) v = java.time.LocalDateTime.parse((String)v);
                    f.set(obj, v);
                }
            }
            return obj;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}