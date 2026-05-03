package com.erp.sales.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.common.exception.BusinessException;
import com.erp.sales.entity.*;
import com.erp.sales.mapper.*;
import com.erp.inventory.mapper.InvStockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1/sales/deliveries")
@RequiredArgsConstructor
public class DeliveryController {
    private final SaleDeliveryMapper mapper;
    private final SaleDeliveryItemMapper itemMapper;
    private final InvStockMapper stockMapper;
    @GetMapping
    public R<PageResult<SaleDelivery>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<SaleDelivery>()
                .eq(customerId != null, SaleDelivery::getCustomerId, customerId)
                .eq(status != null, SaleDelivery::getStatus, status).orderByDesc(SaleDelivery::getCreatedAt);
        Page<SaleDelivery> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        r.put("header", mapper.selectById(id));
        r.put("items", itemMapper.selectList(new LambdaQueryWrapper<SaleDeliveryItem>().eq(SaleDeliveryItem::getDeliveryId, id)));
        return R.ok(r);
    }
    @PostMapping public R<Void> create(@RequestBody Map<String,Object> body) {
        SaleDelivery h = parse(body.get("header"), SaleDelivery.class);
        h.setDeliveryNo("DLV-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000);
        h.setStatus(1); mapper.insert(h); saveItems(body, h.getId()); return R.ok();
    }
    @PostMapping("/{id}/confirm") @Transactional
    public R<Void> confirm(@PathVariable Long id) {
        SaleDelivery h = mapper.selectById(id);
        if (h.getStatus() != 1) throw new BusinessException("只有草稿状态可以确认");
        List<SaleDeliveryItem> items = itemMapper.selectList(new LambdaQueryWrapper<SaleDeliveryItem>().eq(SaleDeliveryItem::getDeliveryId, id));
        for (SaleDeliveryItem item : items) {
            int rows = stockMapper.deduct(item.getMaterialId(), h.getWarehouseId(), item.getQuantity());
            if (rows == 0) throw new BusinessException("物料ID=" + item.getMaterialId() + " 库存不足");
        }
        h.setStatus(2); mapper.updateById(h); return R.ok();
    }
    private void saveItems(Map<String,Object> body, Long did) {
        List<Map<String,Object>> items = (List<Map<String,Object>>) body.get("items");
        if (items == null) return;
        for (Map<String,Object> im : items) {
            SaleDeliveryItem item = parse(im, SaleDeliveryItem.class); item.setDeliveryId(did);
            if (item.getAmount() == null && item.getQuantity() != null && item.getUnitPrice() != null)
                item.setAmount(item.getQuantity().multiply(item.getUnitPrice()));
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