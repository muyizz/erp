package com.erp.inventory.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.common.exception.BusinessException;
import com.erp.inventory.entity.*;
import com.erp.inventory.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
@RestController
@RequestMapping("/api/v1/inventory/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final InvTransferMapper mapper;
    private final InvTransferItemMapper itemMapper;
    private final InvStockMapper stockMapper;
    @GetMapping
    public R<PageResult<InvTransfer>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) Long fromWarehouse,
            @RequestParam(required = false) Long toWarehouse,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<InvTransfer>()
                .eq(fromWarehouse != null, InvTransfer::getFromWarehouse, fromWarehouse)
                .eq(toWarehouse != null, InvTransfer::getToWarehouse, toWarehouse)
                .eq(status != null, InvTransfer::getStatus, status)
                .orderByDesc(InvTransfer::getCreatedAt);
        Page<InvTransfer> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}")
    public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        r.put("header", mapper.selectById(id));
        r.put("items", itemMapper.selectList(new LambdaQueryWrapper<InvTransferItem>().eq(InvTransferItem::getTransferId, id)));
        return R.ok(r);
    }
    @PostMapping
    public R<Void> create(@RequestBody Map<String,Object> body) {
        InvTransfer h = parse(body.get("header"), InvTransfer.class);
        h.setTransferNo("TRF-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000);
        h.setStatus(1); mapper.insert(h);
        saveItems(body, h.getId());
        return R.ok();
    }
    @PostMapping("/{id}/confirm")
    @Transactional
    public R<Void> confirm(@PathVariable Long id) {
        InvTransfer h = mapper.selectById(id);
        List<InvTransferItem> items = itemMapper.selectList(new LambdaQueryWrapper<InvTransferItem>().eq(InvTransferItem::getTransferId, id));
        for (InvTransferItem item : items) {
            int rows = stockMapper.deduct(item.getMaterialId(), h.getFromWarehouse(), item.getQuantity());
            if (rows == 0) throw new BusinessException("物料ID=" + item.getMaterialId() + " 调出仓库库存不足");
            stockMapper.upsert(item.getMaterialId(), h.getToWarehouse(), item.getQuantity());
        }
        h.setStatus(2); mapper.updateById(h);
        return R.ok();
    }
    private void saveItems(Map<String,Object> body, Long pid) {
        List<Map<String,Object>> items = (List<Map<String,Object>>) body.get("items");
        if (items == null) return;
        for (Map<String,Object> im : items) {
            InvTransferItem item = parse(im, InvTransferItem.class);
            item.setTransferId(pid);
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