package com.erp.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.system.entity.SysConfig;
import com.erp.system.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/system/configs")
@RequiredArgsConstructor
public class ConfigController {

    private final SysConfigMapper sysConfigMapper;

    @GetMapping
    public R<PageResult<SysConfig>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize,
            @RequestParam(required = false) String configKey) {
        var wrapper = new LambdaQueryWrapper<SysConfig>()
                .like(configKey != null, SysConfig::getConfigKey, configKey)
                .orderByDesc(SysConfig::getCreatedAt);
        Page<SysConfig> p = sysConfigMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }

    @GetMapping("/{id}")
    public R<SysConfig> detail(@PathVariable Long id) {
        return R.ok(sysConfigMapper.selectById(id));
    }

    @PostMapping
    public R<Void> create(@RequestBody SysConfig config) {
        sysConfigMapper.insert(config);
        return R.ok();
    }

    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody SysConfig config) {
        config.setId(id);
        sysConfigMapper.updateById(config);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        sysConfigMapper.deleteById(id);
        return R.ok();
    }
}
