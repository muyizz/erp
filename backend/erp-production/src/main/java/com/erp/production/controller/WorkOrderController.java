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
@RequestMapping("/api/v1/production/work-orders")
@RequiredArgsConstructor
public class WorkOrderController {
    private final PrdWorkOrderMapper mapper;
    private final PrdWorkOrderMaterialMapper matMapper;
    @GetMapping
    public R<PageResult<PrdWorkOrder>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<PrdWorkOrder>()
                .eq(productId != null, PrdWorkOrder::getProductId, productId)
                .eq(status != null, PrdWorkOrder::getStatus, status).orderByDesc(PrdWorkOrder::getCreatedAt);
        Page<PrdWorkOrder> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        r.put("header", mapper.selectById(id));
        r.put("materials", matMapper.selectList(new LambdaQueryWrapper<PrdWorkOrderMaterial>().eq(PrdWorkOrderMaterial::getWorkOrderId, id)));
        return R.ok(r);
    }
    @PostMapping public R<Void> create(@RequestBody PrdWorkOrder e) { e.setOrderNo("WO-" + java.time.LocalDate.now().toString().replace("-","") + "-" + System.currentTimeMillis()%10000); e.setStatus(1); mapper.insert(e); return R.ok(); }
    @PostMapping("/{id}/release") public R<Void> release(@PathVariable Long id) { PrdWorkOrder e = new PrdWorkOrder(); e.setId(id); e.setStatus(2); mapper.updateById(e); return R.ok(); }
    @PostMapping("/{id}/start") public R<Void> start(@PathVariable Long id) { PrdWorkOrder e = new PrdWorkOrder(); e.setId(id); e.setStatus(3); e.setActualStart(java.time.LocalDate.now()); mapper.updateById(e); return R.ok(); }
    @PostMapping("/{id}/complete") public R<Void> complete(@PathVariable Long id) { PrdWorkOrder e = new PrdWorkOrder(); e.setId(id); e.setStatus(4); e.setActualEnd(java.time.LocalDate.now()); mapper.updateById(e); return R.ok(); }
}