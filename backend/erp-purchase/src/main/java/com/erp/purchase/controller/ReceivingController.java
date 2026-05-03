package com.erp.purchase.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.purchase.entity.*;
import com.erp.purchase.mapper.*;
import com.erp.inventory.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1/purchase/receivings")
@RequiredArgsConstructor
public class ReceivingController {
    private final PurReceivingMapper mapper;
    private final PurReceivingItemMapper itemMapper;
    private final InvStockMapper stockMapper;
    @GetMapping
    public R<PageResult<PurReceiving>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<PurReceiving>()
                .eq(supplierId != null, PurReceiving::getSupplierId, supplierId)
                .eq(status != null, PurReceiving::getStatus, status).orderByDesc(PurReceiving::getCreatedAt);
        Page<PurReceiving> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}")
    public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        r.put("header", mapper.selectById(id));
        r.put("items", itemMapper.selectList(new LambdaQueryWrapper<PurReceivingItem>().eq(PurReceivingItem::getReceivingId, id)));
        return R.ok(r);
    }
    @PostMapping public R<Void> create(@RequestBody Map<String,Object> body) {
        PurReceiving h = parseObj(body.get("header"), PurReceiving.class);
        h.setReceivingNo("RCV-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000);
        h.setStatus(1);
        mapper.insert(h);
        saveItems(body, h.getId());
        return R.ok();
    }
    @PostMapping("/{id}/confirm")
    @Transactional
    public R<Void> confirm(@PathVariable Long id) {
        PurReceiving h = mapper.selectById(id);
        if (h.getStatus() != 1) throw new com.erp.common.exception.BusinessException("只有草稿状态可以确认");
        List<PurReceivingItem> items = itemMapper.selectList(
            new LambdaQueryWrapper<PurReceivingItem>().eq(PurReceivingItem::getReceivingId, id));
        for (PurReceivingItem item : items) {
            stockMapper.upsert(item.getMaterialId(), h.getWarehouseId(), item.getQuantity());
        }
        h.setStatus(2); mapper.updateById(h);
        return R.ok();
    }
    private void saveItems(Map<String,Object> body, Long rid) {
        List<Map<String,Object>> items = (List<Map<String,Object>>) body.get("items");
        if (items == null) return;
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (Map<String,Object> im : items) {
            PurReceivingItem item = parseObj(im, PurReceivingItem.class);
            item.setReceivingId(rid);
            if (item.getAmount() == null && item.getQuantity() != null && item.getUnitPrice() != null)
                item.setAmount(item.getQuantity().multiply(item.getUnitPrice()));
            itemMapper.insert(item);
            if (item.getAmount() != null) total = total.add(item.getAmount());
        }
        PurReceiving h = mapper.selectById(rid);
        h.setTotalAmount(total); mapper.updateById(h);
    }
    private <T> T parseObj(Object data, Class<T> clz) {
        try {
            T obj = clz.getDeclaredConstructor().newInstance();
            Map<String,Object> m = (Map<String,Object>) data;
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