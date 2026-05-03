package com.erp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.system.entity.SysRole;
import com.erp.system.entity.SysMenu;
import com.erp.system.mapper.SysMenuMapper;
import com.erp.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system/roles")
@RequiredArgsConstructor
public class RoleController {

    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;

    @GetMapping
    public R<PageResult<SysRole>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String roleName) {
        var wrapper = new LambdaQueryWrapper<SysRole>()
                .like(roleName != null, SysRole::getRoleName, roleName)
                .orderByAsc(SysRole::getSortOrder);
        Page<SysRole> p = sysRoleMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }

    @GetMapping("/{id}")
    public R<SysRole> detail(@PathVariable Long id) {
        return R.ok(sysRoleMapper.selectById(id));
    }

    @PostMapping
    public R<Void> create(@RequestBody SysRole role) {
        sysRoleMapper.insert(role);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        sysRoleMapper.updateById(role);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        sysRoleMapper.deleteById(id);
        return R.ok();
    }

    @PutMapping("/{id}/menus")
    public R<Void> assignMenus(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        sysRoleMapper.deleteRoleMenus(id);
        List<Long> menuIds = body.get("menuIds");
        if (menuIds != null && !menuIds.isEmpty()) {
            sysRoleMapper.insertRoleMenus(id, menuIds);
        }
        return R.ok();
    }

    @GetMapping("/{id}/menus")
    public R<List<Long>> getRoleMenus(@PathVariable Long id) {
        return R.ok(sysRoleMapper.getMenuIdsByRoleId(id));
    }
}
