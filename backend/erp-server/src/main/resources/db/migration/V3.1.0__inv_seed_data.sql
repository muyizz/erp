INSERT INTO inv_stock (material_id, warehouse_id, quantity) VALUES
(1,1,5000),(2,1,8000),(3,1,200),(4,1,150),(5,1,300),(6,1,3000),(7,2,80),(8,1,200),(9,1,200),(10,1,150)
ON CONFLICT (material_id, warehouse_id) DO NOTHING;

INSERT INTO inv_stock_in (id, doc_no, doc_type, warehouse_id, in_date, created_by) VALUES
(1, 'IN-20260501-001', 6, 1, '2026-05-01', 1),
(2, 'IN-20260501-002', 6, 2, '2026-05-01', 1)
ON CONFLICT DO NOTHING;

INSERT INTO inv_stock_in_item (stock_in_id, material_id, quantity, unit_cost) VALUES
(1,1,5000,0.03),(1,2,8000,0.05),(1,3,200,10.00),(2,7,80,85.00)
ON CONFLICT DO NOTHING;
