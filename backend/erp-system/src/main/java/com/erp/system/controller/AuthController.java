package com.erp.system.controller;

import com.erp.common.model.R;
import com.erp.security.service.TokenService;
import com.erp.system.entity.SysMenu;
import com.erp.system.mapper.SysMenuMapper;
import com.erp.system.mapper.SysUserMapper;
import com.erp.system.entity.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserMapper sysUserMapper;
    private final SysMenuMapper sysMenuMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return R.fail(400, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return R.fail(400, "账号已被禁用");
        }

        List<String> permissions = sysUserMapper.getPermissionsByUserId(user.getId());

        String accessToken = tokenService.generateAccessToken(user.getId(), user.getUsername(), permissions);
        String refreshToken = tokenService.generateRefreshToken(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("permissions", permissions);

        return R.ok(result);
    }

    @GetMapping("/userinfo")
    public R<Map<String, Object>> userinfo(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        var claims = tokenService.parseToken(token);
        Long userId = Long.valueOf(claims.getSubject());

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return R.fail(401, "用户不存在");
        }

        List<String> permissions = (List<String>) claims.get("permissions");
        List<SysMenu> allMenus = sysMenuMapper.getMenusByUserId(userId);
        List<SysMenu> menus = buildMenuTree(allMenus, 0L);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("avatar", user.getAvatar());
        result.put("email", user.getEmail());
        result.put("permissions", permissions);
        result.put("menus", menus);

        return R.ok(result);
    }

    @PostMapping("/refresh")
    public R<Map<String, Object>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (!tokenService.validateToken(refreshToken)) {
            return R.fail(401, "refresh token 无效或已过期");
        }
        var claims = tokenService.parseToken(refreshToken);
        Long userId = Long.valueOf(claims.getSubject());

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getStatus() == 0) {
            return R.fail(401, "用户不存在或已禁用");
        }

        List<String> permissions = sysUserMapper.getPermissionsByUserId(userId);
        String newAccessToken = tokenService.generateAccessToken(userId, user.getUsername(), permissions);
        String newRefreshToken = tokenService.generateRefreshToken(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", newAccessToken);
        result.put("refreshToken", newRefreshToken);

        return R.ok(result);
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> all, Long parentId) {
        return all.stream()
                .filter(m -> m.getParentId().equals(parentId))
                .sorted(Comparator.comparing(SysMenu::getSortOrder))
                .map(m -> {
                    m.setChildren(buildMenuTree(all, m.getId()));
                    return m;
                })
                .collect(Collectors.toList());
    }
}

@Data
class LoginRequest {
    private String username;
    private String password;
}
