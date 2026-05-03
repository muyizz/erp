package com.erp.inventory.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.common.exception.BusinessException;
import com.erp.inventory.entity.InvStockIn;
import com.erp.inventory.entity.InvStockInItem;
import com.erp.inventory.entity.InvStock;
import com.erp.inventory.mapper.InvStockInMapper;
import com.erp.inventory.mapper.InvStockInItemMapper;
import com.erp.inventory.mapper.InvStockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
@RestController
@RequestMapping("/api/v1/inventory/stock-ins")
@RequiredArgsConstructor
public class StockInController {
    private final InvStockInMapper mapper;
    private final InvStockInItemMapper itemMapper;
    private final InvStockMapper stockMapper;
    @GetMapping
    public R<PageResult<InvStockIn>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Integer docType) {
        var w = new LambdaQueryWrapper<InvStockIn>()
                .eq(warehouseId != null, InvStockIn::getWarehouseId, warehouseId)
                .eq(docType != null, InvStockIn::getDocType, docType)
                .orderByDesc(InvStockIn::getCreatedAt);
        Page<InvStockIn> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}")
    public R<Map<String,Object>> detail(@PathVariable Long id) {
        InvStockIn h = mapper.selectById(id);
        List<InvStockInItem> items = itemMapper.selectList(new LambdaQueryWrapper<InvStockInItem>().eq(InvStockInItem::getStockInId, id));
        Map<String,Object> r = new HashMap<>();
        r.put("header", h); r.put("items", items);
        return R.ok(r);
    }
    @PostMapping
    @Transactional
    public R<Void> create(@RequestBody Map<String,Object> body) {
        InvStockIn h = parseHeader(body, InvStockIn.class);
        h.setDocNo("IN-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000);
        mapper.insert(h);
        saveItems(body, h.getId(), InvStockInItem.class);
        return R.ok();
    }
    @PostMapping("/{id}/confirm")
    @Transactional
    public R<Void> confirm(@PathVariable Long id) {
        InvStockIn h = mapper.selectById(id);
        List<InvStockInItem> items = itemMapper.selectList(new LambdaQueryWrapper<InvStockInItem>().eq(InvStockInItem::getStockInId, id));
        for (InvStockInItem item : items) {
            stockMapper.upsert(item.getMaterialId(), h.getWarehouseId(), item.getQuantity());
        }
        return R.ok();
    }
    @SuppressWarnings("unchecked")
    private <T> T parseHeader(Map<String,Object> body, Class<T> clz) {
        Map<String,Object> h = (Map<String,Object>) body.get("header");
        try {
            T obj = clz.getDeclaredConstructor().newInstance();
            for (java.lang.reflect.Field f : clz.getDeclaredFields()) {
                f.setAccessible(true);
                if (h.containsKey(f.getName())) {
                    Object v = h.get(f.getName());
                    if (v instanceof Integer && f.getType() == Long.class) v = ((Integer)v).longValue();
                    if (v instanceof String && f.getType() == Long.class) v = Long.valueOf((String)v);
                    if (v instanceof Number && f.getType() == java.math.BigDecimal.class) v = java.math.BigDecimal.valueOf(((Number)v).doubleValue());
                    if (v instanceof String && f.getType() == java.math.BigDecimal.class) v = new java.math.BigDecimal((String)v);
                    if (v instanceof String && f.getType() == java.time.LocalDate.class) v = java.time.LocalDate.parse((String)v);
                    if (v instanceof String && f.getType() == java.time.LocalDateTime.class) v = java.time.LocalDateTime.parse((String)v);
                    f.set(obj, v);
                }
            }
            return obj;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    private void saveItems(Map<String,Object> body, Long parentId, Class<?> itemClz) {
        List<Map<String,Object>> items = (List<Map<String,Object>>) body.get("items");
        if (items == null) return;
        for (Map<String,Object> im : items) {
            InvStockInItem item = new InvStockInItem();
            item.setStockInId(parentId);
            if (im.containsKey("materialId")) item.setMaterialId(toLong(im.get("materialId")));
            if (im.containsKey("quantity")) item.setQuantity(toBigDecimal(im.get("quantity")));
            if (im.containsKey("unitCost")) item.setUnitCost(toBigDecimal(im.get("unitCost")));
            itemMapper.insert(item);
        }
    }
    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Integer) return ((Integer)v).longValue();
        if (v instanceof Long) return (Long)v;
        return Long.valueOf(v.toString());
    }
    private java.math.BigDecimal toBigDecimal(Object v) {
        if (v == null) return null;
        if (v instanceof java.math.BigDecimal) return (java.math.BigDecimal)v;
        if (v instanceof Number) return java.math.BigDecimal.valueOf(((Number)v).doubleValue());
        return new java.math.BigDecimal(v.toString());
    }
}