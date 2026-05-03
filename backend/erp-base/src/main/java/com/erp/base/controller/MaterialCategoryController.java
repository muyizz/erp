package com.erp.base.controller;
import com.erp.common.model.R;
import com.erp.base.entity.BaseMaterialCategory;
import com.erp.base.mapper.BaseMaterialCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/v1/base/materials/categories")
@RequiredArgsConstructor
public class MaterialCategoryController {
    private final BaseMaterialCategoryMapper mapper;
    @GetMapping("/tree")
    public R<List<BaseMaterialCategory>> tree() {
        List<BaseMaterialCategory> all = mapper.selectList(null);
        return R.ok(build(all, 0L));
    }
    @PostMapping public R<Void> create(@RequestBody BaseMaterialCategory e) { mapper.insert(e); return R.ok(); }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody BaseMaterialCategory e) { e.setId(id); mapper.updateById(e); return R.ok(); }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { mapper.deleteById(id); return R.ok(); }
    private List<BaseMaterialCategory> build(List<BaseMaterialCategory> all, Long pid) {
        return all.stream().filter(c -> c.getParentId().equals(pid))
                .sorted(Comparator.comparing(BaseMaterialCategory::getSortOrder))
                .peek(c -> c.setChildren(build(all, c.getId()))).collect(Collectors.toList());
    }
}