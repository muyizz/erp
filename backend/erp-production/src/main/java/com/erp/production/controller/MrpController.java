package com.erp.production.controller;
import com.erp.common.model.R;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
@RestController
@RequestMapping("/api/v1/production")
@RequiredArgsConstructor
public class MrpController {
    private final JdbcTemplate jdbc;
    @PostMapping("/mrp")
    public R<List<Map<String,Object>>> calculate(@RequestBody Map<String,Object> body) {
        List<Map<String,Object>> demands = (List<Map<String,Object>>) body.get("demands");
        List<Map<String,Object>> result = new ArrayList<>();
        if (demands == null) return R.ok(result);
        for (Map<String,Object> demand : demands) {
            Long productId = toLong(demand.get("productId"));
            BigDecimal qty = toDecimal(demand.get("quantity"));
            if (productId == null || qty == null) continue;
            List<Map<String,Object>> bomItems = jdbc.queryForList(
                "SELECT bi.material_id, bm.material_name, bi.quantity * ? as required_qty " +
                "FROM prd_bom b JOIN prd_bom_item bi ON b.id = bi.bom_id " +
                "JOIN base_material bm ON bi.material_id = bm.id " +
                "WHERE b.product_id = ? AND b.status = 1", qty, productId);
            for (Map<String,Object> item : bomItems) {
                Long matId = toLong(item.get("material_id"));
                BigDecimal required = toDecimal(item.get("required_qty"));
                BigDecimal stock = jdbc.queryForObject(
                    "SELECT COALESCE(SUM(quantity),0) FROM inv_stock WHERE material_id = ?",
                    BigDecimal.class, matId);
                if (stock == null) stock = BigDecimal.ZERO;
                BigDecimal shortage = required.subtract(stock);
                if (shortage.compareTo(BigDecimal.ZERO) <= 0) shortage = BigDecimal.ZERO;
                Map<String,Object> row = new HashMap<>();
                row.put("materialId", matId);
                row.put("materialName", item.get("material_name"));
                row.put("requiredQty", required);
                row.put("currentStock", stock);
                row.put("shortage", shortage);
                result.add(row);
            }
        }
        return R.ok(result);
    }
    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Integer) return ((Integer)v).longValue();
        if (v instanceof Number) return ((Number)v).longValue();
        return Long.valueOf(v.toString());
    }
    private BigDecimal toDecimal(Object v) {
        if (v == null) return null;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return BigDecimal.valueOf(((Number)v).doubleValue());
        return new BigDecimal(v.toString());
    }
}