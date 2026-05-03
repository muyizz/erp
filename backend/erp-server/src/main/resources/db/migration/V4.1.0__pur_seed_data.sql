INSERT INTO pur_order (id, order_no, supplier_id, order_date, expected_date, total_amount, status, created_by) VALUES
(1, 'PO-20260501-001', 1, '2026-05-01', '2026-05-08', 4500, 1, 1),
(2, 'PO-20260502-001', 2, '2026-05-02', '2026-05-09', 2800, 2, 1)
ON CONFLICT DO NOTHING;

INSERT INTO pur_order_item (order_id, line_no, material_id, quantity, unit_price, amount) VALUES
(1,1,1,10000,0.03,300),(1,2,2,20000,0.05,1000),(1,3,8,200,15.00,3000),
(2,1,4,300,5.00,1500),(2,2,5,500,2.00,1000)
ON CONFLICT DO NOTHING;
