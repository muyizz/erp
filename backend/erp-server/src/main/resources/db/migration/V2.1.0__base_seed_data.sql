-- 供应商测试数据
INSERT INTO base_supplier (supplier_code, supplier_name, contact_person, contact_phone, contact_email, address, bank_name, bank_account, tax_id) VALUES
('SUP-001', '深圳电子科技有限公司', '张三', '13800138001', 'zhangsan@szdz.com', '深圳市南山区科技园1号', '工商银行深圳分行', '6222021234567890', '91440300MA5ABCDE'),
('SUP-002', '广州包装材料有限公司', '李四', '13800138002', 'lisi@gzpack.com', '广州市白云区工业园8号', '建设银行广州分行', '6227001234567891', '91440100MA5FGHIJ'),
('SUP-003', '东莞五金配件厂', '王五', '13800138003', 'wangwu@dgwj.com', '东莞市长安镇工业区3号', '农业银行东莞分行', '6228481234567892', '91441900MA5KLMNO')
ON CONFLICT DO NOTHING;

-- 客户测试数据
INSERT INTO base_customer (customer_code, customer_name, contact_person, contact_phone, address, credit_limit) VALUES
('CUS-001', '上海贸易有限公司', '赵六', '13900139001', '上海市浦东新区陆家嘴100号', 500000),
('CUS-002', '北京科技有限公司', '孙七', '13900139002', '北京市海淀区中关村1号', 300000),
('CUS-003', '成都商贸有限公司', '周八', '13900139003', '成都市高新区天府大道99号', 200000)
ON CONFLICT DO NOTHING;

-- 物料分类
INSERT INTO base_material_category (id, parent_id, name, sort_order) VALUES
(1,0,'原材料',1),(2,1,'电子元器件',1),(3,1,'包装材料',2),(4,1,'五金配件',3),(5,0,'产成品',2)
ON CONFLICT DO NOTHING;

-- 物料
INSERT INTO base_material (material_code, material_name, category_id, spec, unit, sale_price, purchase_price, safety_stock) VALUES
('MAT-001', '贴片电阻 10KΩ', 2, '0805 ±5%', '个', 0.05, 0.03, 10000),
('MAT-002', '贴片电容 1μF', 2, '0805 50V', '个', 0.08, 0.05, 10000),
('MAT-003', 'PCB电路板 V2.0', 1, '100x80mm FR-4', '块', 15.00, 10.00, 500),
('MAT-004', '塑料外壳 A型', 3, 'ABS 黑色 120x80x30mm', '个', 8.00, 5.00, 300),
('MAT-005', '纸箱包装 B型', 3, '瓦楞纸 300x200x150mm', '个', 3.00, 2.00, 500),
('MAT-006', '不锈钢螺丝 M3x10', 4, '304不锈钢', '个', 0.10, 0.06, 5000),
('MAT-007', '智能温控器 T100', 5, 'WiFi版 白色', '台', 199.00, 85.00, 100),
('MAT-008', '电源适配器 12V2A', 2, 'DC 5.5x2.1mm', '个', 25.00, 15.00, 500),
('MAT-009', '温湿度传感器模块', 2, 'DHT22 数字输出', '个', 18.00, 12.00, 500),
('MAT-010', 'LCD显示屏 128x64', 2, 'SPI接口 蓝底白字', '个', 22.00, 14.00, 300)
ON CONFLICT DO NOTHING;

-- 仓库
INSERT INTO base_warehouse (warehouse_code, warehouse_name, address, manager, phone) VALUES
('WH-001', '原材料仓', '深圳工厂A栋1楼', '刘库管', '13800138010'),
('WH-002', '成品仓', '深圳工厂A栋2楼', '陈库管', '13800138011'),
('WH-003', '辅料仓', '深圳工厂B栋1楼', '吴库管', '13800138012')
ON CONFLICT DO NOTHING;

-- 员工
INSERT INTO base_employee (employee_code, employee_name, dept_id, position, phone, email, hire_date) VALUES
('EMP-001', '张三丰', 1, '生产主管', '13800138100', 'zhangsf@erp.com', '2024-01-15'),
('EMP-002', '李思思', 1, '采购专员', '13800138101', 'liss@erp.com', '2024-03-01'),
('EMP-003', '王大伟', 2, '销售经理', '13800138102', 'wangdw@erp.com', '2024-06-01')
ON CONFLICT DO NOTHING;
