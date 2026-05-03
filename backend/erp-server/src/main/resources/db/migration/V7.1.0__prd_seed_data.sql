INSERT INTO prd_bom (id, bom_code, product_id, version, quantity) VALUES
(1, 'BOM-001', 7, 'V1.0', 1)
ON CONFLICT DO NOTHING;

INSERT INTO prd_bom_item (bom_id, material_id, quantity, unit, scrap_rate, sort_order) VALUES
(1,3,1,'块',0,1),(1,1,20,'个',2,2),(1,2,15,'个',0,3),(1,4,1,'个',0,4),(1,8,1,'个',0,5),(1,9,1,'个',0,6),(1,10,1,'个',0,7)
ON CONFLICT DO NOTHING;

INSERT INTO prd_work_order (id, order_no, product_id, bom_id, quantity, planned_start, planned_end, workshop, status, created_by) VALUES
(1, 'WO-20260501-001', 7, 1, 50, '2026-05-02', '2026-05-12', '深圳工厂A栋', 2, 1)
ON CONFLICT DO NOTHING;

INSERT INTO prd_work_order_material (work_order_id, material_id, required_qty, issued_qty, warehouse_id) VALUES
(1,1,1000,500,1),(1,2,750,300,1),(1,3,50,30,1)
ON CONFLICT DO NOTHING;

INSERT INTO prd_process (id, process_code, process_name, product_id, description) VALUES
(1, 'PRC-001', '智能温控器生产工艺', 7, '标准生产流程')
ON CONFLICT DO NOTHING;

INSERT INTO prd_process_step (process_id, step_no, step_name, work_center, standard_hours, sort_order) VALUES
(1,10,'SMT贴片','SMT车间',0.5,1),(1,20,'回流焊接','SMT车间',0.3,2),(1,30,'组装','组装车间',1.0,3),(1,40,'测试','测试车间',0.5,4),(1,50,'包装','包装车间',0.3,5)
ON CONFLICT DO NOTHING;
