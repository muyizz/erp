package com.erp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.system.entity.SysDictType;
import com.erp.system.entity.SysDictItem;
import com.erp.system.mapper.SysDictTypeMapper;
import com.erp.system.mapper.SysDictItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/system/dicts")
@RequiredArgsConstructor
public class DictController {
    private final SysDictTypeMapper typeMapper;
    private final SysDictItemMapper itemMapper;

    @GetMapping("/types")
    public R<PageResult<SysDictType>> listTypes(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) String typeName) {
        var w = new LambdaQueryWrapper<SysDictType>()
                .like(typeName != null, SysDictType::getDictName, typeName);
        Page<SysDictType> p = typeMapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }

    @GetMapping("/types/{id}")
    public R<SysDictType> detailType(@PathVariable Long id) { return R.ok(typeMapper.selectById(id)); }

    @PostMapping("/types")
    public R<Void> createType(@RequestBody SysDictType t) { typeMapper.insert(t); return R.ok(); }

    @PutMapping("/types/{id}")
    public R<Void> updateType(@PathVariable Long id, @RequestBody SysDictType t) { t.setId(id); typeMapper.updateById(t); return R.ok(); }

    @DeleteMapping("/types/{id}")
    public R<Void> deleteType(@PathVariable Long id) { typeMapper.deleteById(id); return R.ok(); }

    @GetMapping("/types/{typeId}/items")
    public R<PageResult<SysDictItem>> listItems(@PathVariable Long typeId,
            @RequestParam(defaultValue = "1") long page, @RequestParam(defaultValue = "50") long pageSize) {
        var w = new LambdaQueryWrapper<SysDictItem>().eq(SysDictItem::getDictTypeId, typeId).orderByAsc(SysDictItem::getSortOrder);
        Page<SysDictItem> p = itemMapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }

    @GetMapping("/types/{typeId}/items/{id}")
    public R<SysDictItem> detailItem(@PathVariable Long id) { return R.ok(itemMapper.selectById(id)); }

    @PostMapping("/types/{typeId}/items")
    public R<Void> createItem(@RequestBody SysDictItem i) { itemMapper.insert(i); return R.ok(); }

    @PutMapping("/types/{typeId}/items/{id}")
    public R<Void> updateItem(@PathVariable Long id, @RequestBody SysDictItem i) { i.setId(id); itemMapper.updateById(i); return R.ok(); }

    @DeleteMapping("/types/{typeId}/items/{id}")
    public R<Void> deleteItem(@PathVariable Long id) { itemMapper.deleteById(id); return R.ok(); }
}
