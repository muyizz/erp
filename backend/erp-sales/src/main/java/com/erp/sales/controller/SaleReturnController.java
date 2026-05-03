package com.erp.sales.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.sales.entity.*;
import com.erp.sales.mapper.*;
import com.erp.inventory.mapper.InvStockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1/sales/returns")
@RequiredArgsConstructor
public class SaleReturnController {
    private final SaleReturnMapper mapper;
    private final SaleReturnItemMapper itemMapper;
    private final InvStockMapper stockMapper;
    @GetMapping
    public R<PageResult<SaleReturn>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<SaleReturn>()
                .eq(customerId != null, SaleReturn::getCustomerId, customerId)
                .eq(status != null, SaleReturn::getStatus, status).orderByDesc(SaleReturn::getCreatedAt);
        Page<SaleReturn> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        r.put("header", mapper.selectById(id));
        r.put("items", itemMapper.selectList(new LambdaQueryWrapper<SaleReturnItem>().eq(SaleReturnItem::getReturnId, id)));
        return R.ok(r);
    }
    @PostMapping public R<Void> create(@RequestBody Map<String,Object> body) {
        SaleReturn h = parse(body.get("header"), SaleReturn.class);
        h.setReturnNo("SRT-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000);
        h.setStatus(1); mapper.insert(h); saveItems(body, h.getId()); return R.ok();
    }
    @PostMapping("/{id}/confirm") @Transactional
    public R<Void> confirm(@PathVariable Long id) {
        SaleReturn h = mapper.selectById(id);
        List<SaleReturnItem> items = itemMapper.selectList(new LambdaQueryWrapper<SaleReturnItem>().eq(SaleReturnItem::getReturnId, id));
        for (SaleReturnItem item : items)
            stockMapper.upsert(item.getMaterialId(), 1L, item.getQuantity());
        h.setStatus(2); mapper.updateById(h); return R.ok();
    }
    private void saveItems(Map<String,Object> body, Long rid) {
        List<Map<String,Object>> items = (List<Map<String,Object>>) body.get("items");
        if (items == null) return;
        for (Map<String,Object> im : items) {
            SaleReturnItem item = parse(im, SaleReturnItem.class); item.setReturnId(rid);
            itemMapper.insert(item);
        }
    }
    
    private <T> T parse(Object data, Class<T> clz) {
        try {
            T obj = clz.getDeclaredConstructor().newInstance();
            java.util.Map<String,Object> m = (java.util.Map<String,Object>) data;
            for (java.lang.reflect.Field f : clz.getDeclaredFields()) {
                f.setAccessible(true);
                if (m.containsKey(f.getName()) && m.get(f.getName()) != null) {
                    Object v = m.get(f.getName());
                    if (v instanceof Integer && f.getType() == Long.class) v = ((Integer)v).longValue();
                    if (v instanceof Number && f.getType() == java.math.BigDecimal.class) v = java.math.BigDecimal.valueOf(((Number)v).doubleValue());
                    if (v instanceof String && f.getType() == java.time.LocalDate.class) v = java.time.LocalDate.parse((String)v);
                    if (v instanceof String && f.getType() == java.time.LocalDateTime.class) v = java.time.LocalDateTime.parse((String)v);
                    f.set(obj, v);
                }
            }
            return obj;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}