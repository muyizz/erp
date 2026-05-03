package com.erp.production.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.model.PageResult;
import com.erp.common.model.R;
import com.erp.production.entity.*;
import com.erp.production.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1/production/processes")
@RequiredArgsConstructor
public class ProcessController {
    private final PrdProcessMapper mapper;
    private final PrdProcessStepMapper stepMapper;
    @GetMapping
    public R<PageResult<PrdProcess>> list(@RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long pageSize, @RequestParam(required = false) String processName) {
        var w = new LambdaQueryWrapper<PrdProcess>().like(processName != null, PrdProcess::getProcessName, processName);
        Page<PrdProcess> p = mapper.selectPage(new Page<>(page, pageSize), w);
        return R.ok(PageResult.of(p.getTotal(), p.getCurrent(), p.getSize(), p.getRecords()));
    }
    @GetMapping("/{id}") public R<Map<String,Object>> detail(@PathVariable Long id) {
        Map<String,Object> r = new HashMap<>();
        PrdProcess proc = mapper.selectById(id);
        proc.setSteps(stepMapper.selectList(new LambdaQueryWrapper<PrdProcessStep>().eq(PrdProcessStep::getProcessId, id).orderByAsc(PrdProcessStep::getSortOrder)));
        r.put("header", proc); return R.ok(r);
    }
    @PostMapping public R<Void> create(@RequestBody PrdProcess e) { e.setProcessCode("PRC-" + System.currentTimeMillis()%100000); mapper.insert(e); return R.ok(); }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody PrdProcess e) { e.setId(id); mapper.updateById(e); return R.ok(); }
}