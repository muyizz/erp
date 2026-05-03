package com.erp.report.controller;

import com.erp.common.model.R;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports/inventory")
@RequiredArgsConstructor
public class InventoryReportController {

    private final JdbcTemplate jdbc;

    @GetMapping("/snapshot")
    public R<List<Map<String, Object>>> snapshot() {
        return R.ok(jdbc.queryForList(
                "SELECT bm.material_code, bm.material_name, bw.warehouse_name, " +
                "COALESCE(s.quantity, 0) AS quantity, bm.sale_price, " +
                "COALESCE(s.quantity, 0) * COALESCE(bm.sale_price, 0) AS stock_value " +
                "FROM inv_stock s " +
                "LEFT JOIN base_material bm ON s.material_id = bm.id " +
                "LEFT JOIN base_warehouse bw ON s.warehouse_id = bw.id " +
                "WHERE COALESCE(s.quantity, 0) > 0 " +
                "ORDER BY stock_value DESC"));
    }

    @GetMapping("/turnover")
    public R<List<Map<String, Object>>> turnover(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        StringBuilder dateFilter = new StringBuilder();
        if (startDate != null && !startDate.isEmpty()) {
            dateFilter.append(" AND so.created_at >= '").append(startDate).append("'");
        }
        if (endDate != null && !endDate.isEmpty()) {
            dateFilter.append(" AND so.created_at <= '").append(endDate).append("'");
        }
        return R.ok(jdbc.queryForList(
                "SELECT bm.material_code, bm.material_name, " +
                "COALESCE(SUM(soi.quantity), 0) AS out_quantity, " +
                "COALESCE(SUM(soi.amount), 0) AS out_amount " +
                "FROM sale_delivery_item soi " +
                "LEFT JOIN sale_delivery sd ON soi.delivery_id = sd.id " +
                "LEFT JOIN base_material bm ON soi.material_id = bm.id " +
                "WHERE sd.status = 2" + dateFilter +
                " GROUP BY bm.material_code, bm.material_name " +
                "ORDER BY out_amount DESC"));
    }
}
