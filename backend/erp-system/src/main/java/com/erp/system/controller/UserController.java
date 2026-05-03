package com.erp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system/users")
@RequiredArgsConstructor
public class UserController {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public R<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        var wrapper = new LambdaQueryWrapper<SysUser>()
                .like(username != null, SysUser::getUsername, username)
                .eq(status != null, SysUser::getStatus, status)
                .orderByDesc(SysUser::getCreatedAt);
        Page<SysUser> p = sysUserMapper.selectPage(new Page<>(page, pageSize), wrapper);
        p.getRecords().forEach(u -> u.setPassword(null));
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }

    @GetMapping("/{id}")
    public R<SysUser> detail(@PathVariable Long id) {
        SysUser u = sysUserMapper.selectById(id);
        if (u != null) u.setPassword(null);
        return R.ok(u);
    }

    @PostMapping
    public R<Void> create(@RequestBody SysUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        sysUserMapper.insert(user);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        sysUserMapper.updateById(user);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        SysUser u = new SysUser();
        u.setId(id);
        u.setStatus(0);
        sysUserMapper.updateById(u);
        return R.ok();
    }

    @PutMapping("/{id}/roles")
    public R<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        sysUserMapper.deleteUserRoles(id);
        List<Long> roleIds = body.get("roleIds");
        if (roleIds != null && !roleIds.isEmpty()) {
            sysUserMapper.insertUserRoles(id, roleIds);
        }
        return R.ok();
    }

    @GetMapping("/{id}/roles")
    public R<List<Long>> getUserRoles(@PathVariable Long id) {
        return R.ok(sysUserMapper.getRoleIdsByUserId(id));
    }
}
