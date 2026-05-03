package com.erp.report.controller;

import com.erp.common.model.R;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports/sales")
@RequiredArgsConstructor
public class SalesReportController {

    private final JdbcTemplate jdbc;

    @GetMapping("/summary")
    public R<List<Map<String, Object>>> summary(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT c.customer_name, COUNT(so.id) AS order_count, " +
                "COALESCE(SUM(so.total_amount), 0) AS total_amount " +
                "FROM sale_order so LEFT JOIN base_customer c ON so.customer_id = c.id WHERE 1=1");
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND so.order_date >= '").append(startDate).append("'");
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND so.order_date <= '").append(endDate).append("'");
        }
        sql.append(" GROUP BY c.customer_name ORDER BY total_amount DESC");
        return R.ok(jdbc.queryForList(sql.toString()));
    }

    @GetMapping("/analysis")
    public R<Map<String, Object>> analysis(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        StringBuilder dateFilter = new StringBuilder();
        if (startDate != null && !startDate.isEmpty()) {
            dateFilter.append(" AND so.order_date >= '").append(startDate).append("'");
        }
        if (endDate != null && !endDate.isEmpty()) {
            dateFilter.append(" AND so.order_date <= '").append(endDate).append("'");
        }
        String topCustomers = "SELECT c.customer_name, COUNT(so.id) AS order_count, " +
                "COALESCE(SUM(so.total_amount), 0) AS total_amount " +
                "FROM sale_order so LEFT JOIN base_customer c ON so.customer_id = c.id WHERE 1=1" +
                dateFilter +
                " GROUP BY c.customer_name ORDER BY total_amount DESC LIMIT 10";
        String topProducts = "SELECT bm.material_name, COUNT(soi.id) AS order_count, " +
                "COALESCE(SUM(soi.quantity), 0) AS total_quantity, " +
                "COALESCE(SUM(soi.amount), 0) AS total_amount " +
                "FROM sale_order_item soi LEFT JOIN sale_order so ON soi.order_id = so.id " +
                "LEFT JOIN base_material bm ON soi.material_id = bm.id WHERE 1=1" +
                dateFilter +
                " GROUP BY bm.material_name ORDER BY total_amount DESC LIMIT 10";
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("topCustomers", jdbc.queryForList(topCustomers));
        result.put("topProducts", jdbc.queryForList(topProducts));
        return R.ok(result);
    }
}
