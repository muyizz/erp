package com.erp.report.controller;

import com.erp.common.model.R;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports/purchase")
@RequiredArgsConstructor
public class PurchaseReportController {

    private final JdbcTemplate jdbc;

    @GetMapping("/summary")
    public R<List<Map<String, Object>>> summary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long supplierId) {
        StringBuilder sql = new StringBuilder(
                "SELECT s.supplier_name, COUNT(po.id) AS order_count, " +
                "COALESCE(SUM(po.total_amount), 0) AS total_amount " +
                "FROM pur_order po LEFT JOIN base_supplier s ON po.supplier_id = s.id WHERE 1=1");
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND po.order_date >= '").append(startDate).append("'");
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND po.order_date <= '").append(endDate).append("'");
        }
        if (supplierId != null) {
            sql.append(" AND po.supplier_id = ").append(supplierId);
        }
        sql.append(" GROUP BY s.supplier_name ORDER BY total_amount DESC");
        return R.ok(jdbc.queryForList(sql.toString()));
    }
}
