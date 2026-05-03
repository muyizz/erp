package com.erp.finance.controller;
import com.erp.common.model.R;
import com.erp.finance.entity.FinAccount;
import com.erp.finance.mapper.FinAccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/v1/finance/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final FinAccountMapper mapper;
    @GetMapping("/tree")
    public R<List<FinAccount>> tree() {
        List<FinAccount> all = mapper.selectList(null);
        return R.ok(build(all, 0L));
    }
    @PostMapping public R<Void> create(@RequestBody FinAccount e) { mapper.insert(e); return R.ok(); }
    @PutMapping("/{id}") public R<Void> update(@PathVariable Long id, @RequestBody FinAccount e) { e.setId(id); mapper.updateById(e); return R.ok(); }
    @DeleteMapping("/{id}") public R<Void> delete(@PathVariable Long id) { mapper.deleteById(id); return R.ok(); }
    private List<FinAccount> build(List<FinAccount> all, Long pid) {
        return all.stream().filter(a -> a.getParentId().equals(pid))
                .sorted(Comparator.comparing(FinAccount::getAccountCode))
                .peek(a -> a.setChildren(build(all, a.getId()))).collect(Collectors.toList());
    }
}