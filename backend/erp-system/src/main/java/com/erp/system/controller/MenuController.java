package com.erp.system.controller;

import com.erp.common.model.R;
import com.erp.system.entity.SysMenu;
import com.erp.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/system/menus")
@RequiredArgsConstructor
public class MenuController {

    private final SysMenuMapper sysMenuMapper;

    @GetMapping("/tree")
    public R<List<SysMenu>> tree() {
        List<SysMenu> all = sysMenuMapper.selectList(null);
        List<SysMenu> roots = all.stream()
                .filter(m -> m.getParentId() == 0)
                .sorted(Comparator.comparing(SysMenu::getSortOrder))
                .peek(m -> m.setChildren(buildTree(all, m.getId())))
                .collect(Collectors.toList());
        return R.ok(roots);
    }

    @GetMapping("/{id}")
    public R<SysMenu> detail(@PathVariable Long id) {
        return R.ok(sysMenuMapper.selectById(id));
    }

    @PostMapping
    public R<Void> create(@RequestBody SysMenu menu) {
        sysMenuMapper.insert(menu);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setId(id);
        sysMenuMapper.updateById(menu);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        sysMenuMapper.deleteById(id);
        return R.ok();
    }

    private List<SysMenu> buildTree(List<SysMenu> all, Long parentId) {
        return all.stream()
                .filter(m -> m.getParentId().equals(parentId))
                .sorted(Comparator.comparing(SysMenu::getSortOrder))
                .peek(m -> m.setChildren(buildTree(all, m.getId())))
                .collect(Collectors.toList());
    }
}
