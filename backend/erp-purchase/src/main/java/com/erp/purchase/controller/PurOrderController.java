package com.erp.purchase.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.purchase.entity.*;
import com.erp.purchase.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1/purchase/orders")
@RequiredArgsConstructor
public class PurOrderController {
    private final PurOrderMapper mapper;
    private final PurOrderItemMapper itemMapper;
    @GetMapping
    public R<PageResult<PurOrder>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String orderNo, @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<PurOrder>()
                .like(orderNo != null, PurOrder::getOrderNo, orderNo)
                .eq(supplierId != null, PurOrder::getSupplierId, supplierId)
                .eq(status != null, PurOrder::getStatus, status).orderByDesc(PurOrder::getCreatedAt);
        Page<PurOrder> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}")
    public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        r.put("header", mapper.selectById(id));
        r.put("items", itemMapper.selectList(new LambdaQueryWrapper<PurOrderItem>().eq(PurOrderItem::getOrderId, id).orderByAsc(PurOrderItem::getLineNo)));
        return R.ok(r);
    }
    @PostMapping public R<Void> create(@RequestBody Map<String,Object> body) {
        PurOrder h = parseObj(body.get("header"), PurOrder.class);
        h.setOrderNo("PO-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000);
        h.setStatus(1); h.setTotalAmount(java.math.BigDecimal.ZERO);
        mapper.insert(h);
        List<Map<String,Object>> items = (List<Map<String,Object>>) body.get("items");
        if (items != null) {
            java.math.BigDecimal total = java.math.BigDecimal.ZERO;
            for (int i=0;i<items.size();i++) {
                PurOrderItem item = parseObj(items.get(i), PurOrderItem.class);
                item.setOrderId(h.getId()); item.setLineNo(i+1);
                if (item.getAmount() == null) item.setAmount(item.getQuantity().multiply(item.getUnitPrice()));
                itemMapper.insert(item);
                total = total.add(item.getAmount());
            }
            h.setTotalAmount(total); mapper.updateById(h);
        }
        return R.ok();
    }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody PurOrder e) { e.setId(id); mapper.updateById(e); return R.ok(); }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { PurOrder e = new PurOrder(); e.setId(id); e.setStatus(6); mapper.updateById(e); return R.ok(); }
    @PostMapping("/{id}/approve") public R<Void> approve(@PathVariable Long id) { PurOrder e = new PurOrder(); e.setId(id); e.setStatus(2); mapper.updateById(e); return R.ok(); }
    @PostMapping("/{id}/cancel") public R<Void> cancel(@PathVariable Long id) { PurOrder e = new PurOrder(); e.setId(id); e.setStatus(6); mapper.updateById(e); return R.ok(); }
    @SuppressWarnings("unchecked")
    private <T> T parseObj(Object data, Class<T> clz) {
        try {
            T obj = clz.getDeclaredConstructor().newInstance();
            Map<String,Object> m = (Map<String,Object>) data;
            for (java.lang.reflect.Field f : clz.getDeclaredFields()) {
                f.setAccessible(true);
                if (m.containsKey(f.getName()) && m.get(f.getName()) != null) {
                    Object v = m.get(f.getName());
                    if (v instanceof Integer && f.getType() == Long.class) v = ((Integer)v).longValue();
                    if (v instanceof String && f.getType() == Long.class) v = Long.valueOf((String)v);
                    if (v instanceof String && f.getType() == java.math.BigDecimal.class) v = new java.math.BigDecimal((String)v);
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