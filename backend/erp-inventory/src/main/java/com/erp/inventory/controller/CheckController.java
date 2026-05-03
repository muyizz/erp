package com.erp.inventory.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.inventory.entity.*;
import com.erp.inventory.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
@RestController
@RequestMapping("/api/v1/inventory/checks")
@RequiredArgsConstructor
public class CheckController {
    private final InvCheckMapper mapper;
    private final InvCheckItemMapper itemMapper;
    private final InvStockMapper stockMapper;
    @GetMapping
    public R<PageResult<InvCheck>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<InvCheck>()
                .eq(warehouseId != null, InvCheck::getWarehouseId, warehouseId)
                .eq(status != null, InvCheck::getStatus, status)
                .orderByDesc(InvCheck::getCreatedAt);
        Page<InvCheck> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}")
    public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        r.put("header", mapper.selectById(id));
        r.put("items", itemMapper.selectList(new LambdaQueryWrapper<InvCheckItem>().eq(InvCheckItem::getCheckId, id)));
        return R.ok(r);
    }
    @PostMapping
    public R<Void> create(@RequestBody Map<String,Object> body) {
        InvCheck h = parse(body.get("header"), InvCheck.class);
        h.setCheckNo("CHK-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000);
        h.setStatus(1); mapper.insert(h);
        saveItems(body, h.getId());
        return R.ok();
    }
    @PostMapping("/{id}/confirm")
    @Transactional
    public R<Void> confirm(@PathVariable Long id) {
        InvCheck h = mapper.selectById(id);
        List<InvCheckItem> items = itemMapper.selectList(new LambdaQueryWrapper<InvCheckItem>().eq(InvCheckItem::getCheckId, id));
        for (InvCheckItem item : items) {
            BigDecimal diff = item.getDiffQty();
            if (diff == null) diff = BigDecimal.ZERO;
            if (diff.compareTo(BigDecimal.ZERO) > 0)
                stockMapper.upsert(item.getMaterialId(), h.getWarehouseId(), diff);
            else if (diff.compareTo(BigDecimal.ZERO) < 0)
                stockMapper.deduct(item.getMaterialId(), h.getWarehouseId(), diff.abs());
        }
        h.setStatus(2); mapper.updateById(h);
        return R.ok();
    }
    private void saveItems(Map<String,Object> body, Long pid) {
        List<Map<String,Object>> items = (List<Map<String,Object>>) body.get("items");
        if (items == null) return;
        for (Map<String,Object> im : items) {
            InvCheckItem item = parse(im, InvCheckItem.class);
            item.setCheckId(pid);
            itemMapper.insert(item);
        }
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
                    if (v instanceof Number && f.getType() == BigDecimal.class) v = java.math.BigDecimal.valueOf(((Number)v).doubleValue());
                    f.set(obj, v);
                }
            }
            return obj;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}