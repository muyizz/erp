package com.erp.report.controller;

import com.erp.common.model.R;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports/finance")
@RequiredArgsConstructor
public class FinanceReportController {

    private final JdbcTemplate jdbc;

    @GetMapping("/trial-balance")
    public R<List<Map<String, Object>>> trialBalance() {
        return R.ok(jdbc.queryForList(
                "SELECT fa.account_code, fa.account_name, fa.account_type, " +
                "COALESCE(SUM(fvi.debit), 0) AS debit_total, " +
                "COALESCE(SUM(fvi.credit), 0) AS credit_total, " +
                "COALESCE(SUM(fvi.debit), 0) - COALESCE(SUM(fvi.credit), 0) AS balance " +
                "FROM fin_account fa " +
                "LEFT JOIN fin_voucher_item fvi ON fa.id = fvi.account_id " +
                "LEFT JOIN fin_voucher fv ON fvi.voucher_id = fv.id AND fv.status = 2 " +
                "WHERE fa.status = 1 " +
                "GROUP BY fa.id, fa.account_code, fa.account_name, fa.account_type " +
                "ORDER BY fa.account_code"));
    }

    @GetMapping("/income-statement")
    public R<List<Map<String, Object>>> incomeStatement(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        StringBuilder dateFilter = new StringBuilder();
        if (startDate != null && !startDate.isEmpty()) {
            dateFilter.append(" AND fv.voucher_date >= '").append(startDate).append("'");
        }
        if (endDate != null && !endDate.isEmpty()) {
            dateFilter.append(" AND fv.voucher_date <= '").append(endDate).append("'");
        }
        return R.ok(jdbc.queryForList(
                "SELECT fa.account_code, fa.account_name, fa.account_type, " +
                "COALESCE(SUM(fvi.debit), 0) AS debit_amount, " +
                "COALESCE(SUM(fvi.credit), 0) AS credit_amount " +
                "FROM fin_voucher_item fvi " +
                "LEFT JOIN fin_voucher fv ON fvi.voucher_id = fv.id " +
                "LEFT JOIN fin_account fa ON fvi.account_id = fa.id " +
                "WHERE fv.status = 2 AND (fa.account_type = 4 OR fa.account_type = 5)" + dateFilter +
                " GROUP BY fa.account_code, fa.account_name, fa.account_type " +
                "ORDER BY fa.account_code"));
    }

    @GetMapping("/ar-aging")
    public R<List<Map<String, Object>>> arAging() {
        return R.ok(jdbc.queryForList(
                "SELECT fi.invoice_no, fi.invoice_type, fi.invoice_date, fi.due_date, " +
                "fi.amount, COALESCE(fi.paid_amount, 0) AS paid_amount, " +
                "fi.amount - COALESCE(fi.paid_amount, 0) AS outstanding, " +
                "fi.status, bc.customer_name AS company_name " +
                "FROM fin_invoice fi " +
                "LEFT JOIN base_customer bc ON fi.company_id = bc.id " +
                "WHERE fi.invoice_type = 1 AND fi.status IN (1, 2) " +
                "ORDER BY fi.due_date"));
    }

    @GetMapping("/ap-aging")
    public R<List<Map<String, Object>>> apAging() {
        return R.ok(jdbc.queryForList(
                "SELECT fi.invoice_no, fi.invoice_type, fi.invoice_date, fi.due_date, " +
                "fi.amount, COALESCE(fi.paid_amount, 0) AS paid_amount, " +
                "fi.amount - COALESCE(fi.paid_amount, 0) AS outstanding, " +
                "fi.status, bs.supplier_name AS company_name " +
                "FROM fin_invoice fi " +
                "LEFT JOIN base_supplier bs ON fi.company_id = bs.id " +
                "WHERE fi.invoice_type = 2 AND fi.status IN (1, 2) " +
                "ORDER BY fi.due_date"));
    }
}
