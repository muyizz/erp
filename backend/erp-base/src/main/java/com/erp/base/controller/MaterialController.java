package com.erp.base.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.base.entity.BaseMaterial;
import com.erp.base.mapper.BaseMaterialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/base/materials")
@RequiredArgsConstructor
public class MaterialController {
    private final BaseMaterialMapper mapper;
    @GetMapping
    public R<PageResult<BaseMaterial>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String materialCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        var w = new LambdaQueryWrapper<BaseMaterial>()
                .like(materialCode != null, BaseMaterial::getMaterialCode, materialCode)
                .like(materialName != null, BaseMaterial::getMaterialName, materialName)
                .eq(categoryId != null, BaseMaterial::getCategoryId, categoryId)
                .eq(status != null, BaseMaterial::getStatus, status);
        Page<BaseMaterial> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<BaseMaterial> detail(@PathVariable Long id) { return R.ok(mapper.selectById(id)); }
    @PostMapping public R<Void> create(@RequestBody BaseMaterial e) { mapper.insert(e); return R.ok(); }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody BaseMaterial e) { e.setId(id); mapper.updateById(e); return R.ok(); }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { BaseMaterial e = new BaseMaterial(); e.setId(id); e.setStatus(0); mapper.updateById(e); return R.ok(); }
}