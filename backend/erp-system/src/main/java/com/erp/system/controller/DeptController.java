package com.erp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.model.R;
import com.erp.system.entity.SysDept;
import com.erp.system.mapper.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/system/depts")
@RequiredArgsConstructor
public class DeptController {

    private final SysDeptMapper sysDeptMapper;

    @GetMapping("/tree")
    public R<List<SysDept>> tree() {
        List<SysDept> all = sysDeptMapper.selectList(
                new LambdaQueryWrapper<SysDept>().orderByAsc(SysDept::getSortOrder));
        List<SysDept> roots = all.stream()
                .filter(d -> d.getParentId() == null || d.getParentId() == 0)
                .peek(d -> d.setChildren(buildTree(all, d.getId())))
                .collect(Collectors.toList());
        return R.ok(roots);
    }

    @GetMapping("/{id}")
    public R<SysDept> detail(@PathVariable Long id) {
        return R.ok(sysDeptMapper.selectById(id));
    }

    @PostMapping
    public R<Void> create(@RequestBody SysDept dept) {
        sysDeptMapper.insert(dept);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody SysDept dept) {
        dept.setId(id);
        sysDeptMapper.updateById(dept);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        SysDept dept = new SysDept();
        dept.setId(id);
        dept.setStatus(0);
        sysDeptMapper.updateById(dept);
        return R.ok();
    }

    private List<SysDept> buildTree(List<SysDept> all, Long parentId) {
        return all.stream()
                .filter(d -> parentId.equals(d.getParentId()))
                .sorted(Comparator.comparing(SysDept::getSortOrder))
                .peek(d -> d.setChildren(buildTree(all, d.getId())))
                .collect(Collectors.toList());
    }
}
