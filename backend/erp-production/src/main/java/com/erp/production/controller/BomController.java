package com.erp.production.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.production.entity.*;
import com.erp.production.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1/production/boms")
@RequiredArgsConstructor
public class BomController {
    private final PrdBomMapper mapper;
    private final PrdBomItemMapper itemMapper;
    @GetMapping
    public R<PageResult<PrdBom>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<PrdBom>()
                .eq(productId != null, PrdBom::getProductId, productId)
                .eq(status != null, PrdBom::getStatus, status).orderByDesc(PrdBom::getCreatedAt);
        Page<PrdBom> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { mapper.deleteById(id); return R.ok(); }
    @GetMapping("/{id}")
    public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        PrdBom bom = mapper.selectById(id);
        bom.setItems(itemMapper.selectList(new LambdaQueryWrapper<PrdBomItem>().eq(PrdBomItem::getBomId, id).orderByAsc(PrdBomItem::getSortOrder)));
        r.put("header", bom); return R.ok(r);
    }
    @PostMapping public R<Void> create(@RequestBody Map<String,Object> body) {
        PrdBom h = parse(body.get("header"), PrdBom.class);
        h.setBomCode("BOM-" + System.currentTimeMillis()%100000); mapper.insert(h);
        List<Map<String,Object>> items = (List<Map<String,Object>>) body.get("items");
        if (items != null) for (int i=0;i<items.size();i++) {
            PrdBomItem item = parse(items.get(i), PrdBomItem.class); item.setBomId(h.getId()); item.setSortOrder(i+1); itemMapper.insert(item);
        }
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