package com.erp.sales.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.sales.entity.*;
import com.erp.sales.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1/sales/orders")
@RequiredArgsConstructor
public class SaleOrderController {
    private final SaleOrderMapper mapper;
    private final SaleOrderItemMapper itemMapper;
    @GetMapping
    public R<PageResult<SaleOrder>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String orderNo, @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<SaleOrder>()
                .like(orderNo != null, SaleOrder::getOrderNo, orderNo)
                .eq(customerId != null, SaleOrder::getCustomerId, customerId)
                .eq(status != null, SaleOrder::getStatus, status).orderByDesc(SaleOrder::getCreatedAt);
        Page<SaleOrder> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        r.put("header", mapper.selectById(id));
        r.put("items", itemMapper.selectList(new LambdaQueryWrapper<SaleOrderItem>().eq(SaleOrderItem::getOrderId, id).orderByAsc(SaleOrderItem::getLineNo)));
        return R.ok(r);
    }
    @PostMapping public R<Void> create(@RequestBody Map<String,Object> body) {
        SaleOrder h = parse(body.get("header"), SaleOrder.class);
        h.setOrderNo("SO-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000);
        h.setStatus(1); h.setTotalAmount(java.math.BigDecimal.ZERO); mapper.insert(h);
        List<Map<String,Object>> items = (List<Map<String,Object>>) body.get("items");
        if (items != null) {
            java.math.BigDecimal total = java.math.BigDecimal.ZERO;
            for (int i=0;i<items.size();i++) {
                SaleOrderItem item = parse(items.get(i), SaleOrderItem.class);
                item.setOrderId(h.getId()); item.setLineNo(i+1);
                if (item.getAmount() == null) item.setAmount(item.getQuantity().multiply(item.getUnitPrice()));
                itemMapper.insert(item); total = total.add(item.getAmount());
            }
            h.setTotalAmount(total); mapper.updateById(h);
        }
        return R.ok();
    }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody SaleOrder e) { e.setId(id); mapper.updateById(e); return R.ok(); }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { SaleOrder e = new SaleOrder(); e.setId(id); e.setStatus(6); mapper.updateById(e); return R.ok(); }
    @PostMapping("/{id}/approve") public R<Void> approve(@PathVariable Long id) { SaleOrder e = new SaleOrder(); e.setId(id); e.setStatus(2); mapper.updateById(e); return R.ok(); }
    @PostMapping("/{id}/cancel") public R<Void> cancel(@PathVariable Long id) { SaleOrder e = new SaleOrder(); e.setId(id); e.setStatus(6); mapper.updateById(e); return R.ok(); }
    
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