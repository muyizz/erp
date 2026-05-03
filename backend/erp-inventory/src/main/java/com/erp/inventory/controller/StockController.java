package com.erp.inventory.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.inventory.entity.InvStock;
import com.erp.inventory.mapper.InvStockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/inventory/stocks")
@RequiredArgsConstructor
public class StockController {
    private final InvStockMapper mapper;
    @GetMapping
    public R<PageResult<InvStock>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) Long warehouseId) {
        var w = new LambdaQueryWrapper<InvStock>()
                .eq(materialId != null, InvStock::getMaterialId, materialId)
                .eq(warehouseId != null, InvStock::getWarehouseId, warehouseId)
                .gt(InvStock::getQuantity, 0);
        Page<InvStock> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/alerts/safety-stock")
    public R<java.util.List<InvStock>> safetyStockAlerts() {
        return R.ok(mapper.selectList(new LambdaQueryWrapper<InvStock>().gt(InvStock::getQuantity, 0)));
    }
}