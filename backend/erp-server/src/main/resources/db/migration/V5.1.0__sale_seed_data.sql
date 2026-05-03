INSERT INTO sale_order (id, order_no, customer_id, order_date, delivery_date, total_amount, status, created_by) VALUES
(1, 'SO-20260501-001', 1, '2026-04-30', '2026-05-05', 19900, 1, 1),
(2, 'SO-20260502-001', 2, '2026-05-02', '2026-05-08', 15000, 1, 1)
ON CONFLICT DO NOTHING;

INSERT INTO sale_order_item (order_id, line_no, material_id, quantity, unit_price, amount) VALUES
(1,1,7,100,199.00,19900),
(2,1,7,50,199.00,9950),(2,2,9,100,18.00,1800)
ON CONFLICT DO NOTHING;
