-- 默认管理员用户 admin/admin123
INSERT INTO sys_user (username, password, real_name, email, status) VALUES
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', 'admin@erp.com', 1);

-- 默认角色
INSERT INTO sys_role (role_name, role_code, description, sort_order) VALUES
('超级管理员', 'ROLE_ADMIN', '拥有所有权限', 1),
('普通用户', 'ROLE_USER', '基础权限', 2);

-- 默认菜单 (目录/菜单/按钮)
INSERT INTO sys_menu (id, parent_id, name, type, path, component, permission_code, icon, sort_order, visible) VALUES
-- 一级目录
(1, 0, '系统管理', 1, '/system', null, null, 'Setting', 1, 1),
(2, 0, '基础资料', 1, '/base', null, null, 'Document', 2, 1),
(3, 0, '采购管理', 1, '/purchase', null, null, 'ShoppingCart', 3, 1),
(4, 0, '销售管理', 1, '/sales', null, null, 'Sell', 4, 1),
(5, 0, '库存管理', 1, '/inventory', null, null, 'Box', 5, 1),
(6, 0, '财务管理', 1, '/finance', null, null, 'Money', 6, 1),
(7, 0, '生产管理', 1, '/production', null, null, 'Cpu', 7, 1),
(8, 0, '报表分析', 1, '/report', null, null, 'DataAnalysis', 8, 1),
-- 系统管理子菜单
(11, 1, '用户管理', 2, '/system/user', 'system/user/index', 'system:user:list', 'User', 1, 1),
(12, 1, '角色管理', 2, '/system/role', 'system/role/index', 'system:role:list', 'Avatar', 2, 1),
(13, 1, '菜单管理', 2, '/system/menu', 'system/menu/index', 'system:menu:list', 'Menu', 3, 1),
(14, 1, '部门管理', 2, '/system/dept', 'system/dept/index', 'system:dept:list', 'OfficeBuilding', 4, 1),
(15, 1, '操作日志', 2, '/system/log', 'system/log/index', 'system:log:list', 'Tickets', 5, 1),
(16, 1, '数据字典', 2, '/system/dict', 'system/dict/index', 'system:dict:list', 'Collection', 6, 1),
(17, 1, '系统配置', 2, '/system/config', 'system/config/index', 'system:config:list', 'Tools', 7, 1),
-- 基础资料子菜单
(21, 2, '供应商管理', 2, '/base/supplier', 'base/supplier/index', 'base:supplier:list', 'Shop', 1, 1),
(22, 2, '客户管理', 2, '/base/customer', 'base/customer/index', 'base:customer:list', 'UserFilled', 2, 1),
(23, 2, '物料管理', 2, '/base/material', 'base/material/index', 'base:material:list', 'Goods', 3, 1),
(24, 2, '仓库管理', 2, '/base/warehouse', 'base/warehouse/index', 'base:warehouse:list', 'HomeFilled', 4, 1),
(25, 2, '员工管理', 2, '/base/employee', 'base/employee/index', 'base:employee:list', 'List', 5, 1),
-- 采购管理子菜单
(31, 3, '采购订单', 2, '/purchase/order', 'purchase/order/index', 'purchase:order:list', 'Tickets', 1, 1),
(32, 3, '采购收货', 2, '/purchase/receiving', 'purchase/receiving/index', 'purchase:receiving:list', 'Checked', 2, 1),
(33, 3, '采购退货', 2, '/purchase/return', 'purchase/return/index', 'purchase:return:list', 'Switch', 3, 1),
-- 销售管理子菜单
(41, 4, '销售订单', 2, '/sales/order', 'sales/order/index', 'sales:order:list', 'Tickets', 1, 1),
(42, 4, '销售发货', 2, '/sales/delivery', 'sales/delivery/index', 'sales:delivery:list', 'Sell', 2, 1),
(43, 4, '销售退货', 2, '/sales/return', 'sales/return/index', 'sales:return:list', 'Switch', 3, 1),
-- 库存管理子菜单
(51, 5, '库存查询', 2, '/inventory/stock', 'inventory/stock/index', 'inventory:stock:list', 'Search', 1, 1),
(52, 5, '入库管理', 2, '/inventory/stock-in', 'inventory/stockIn/index', 'inventory:stockIn:list', 'Upload', 2, 1),
(53, 5, '出库管理', 2, '/inventory/stock-out', 'inventory/stockOut/index', 'inventory:stockOut:list', 'Download', 3, 1),
(54, 5, '库存调拨', 2, '/inventory/transfer', 'inventory/transfer/index', 'inventory:transfer:list', 'Refresh', 4, 1),
(55, 5, '库存盘点', 2, '/inventory/check', 'inventory/check/index', 'inventory:check:list', 'Notebook', 5, 1),
-- 财务管理子菜单
(61, 6, '会计科目', 2, '/finance/account', 'finance/account/index', 'finance:account:list', 'Grid', 1, 1),
(62, 6, '记账凭证', 2, '/finance/voucher', 'finance/voucher/index', 'finance:voucher:list', 'Document', 2, 1),
(63, 6, '应收发票', 2, '/finance/invoice-ar', 'finance/invoice/ar', 'finance:invoice:ar', 'Receipt', 3, 1),
(64, 6, '应付发票', 2, '/finance/invoice-ap', 'finance/invoice/ap', 'finance:invoice:ap', 'Receipt', 4, 1),
(65, 6, '收付款', 2, '/finance/payment', 'finance/payment/index', 'finance:payment:list', 'CreditCard', 5, 1),
(66, 6, '费用报销', 2, '/finance/expense', 'finance/expense/index', 'finance:expense:list', 'Money', 6, 1),
-- 生产管理子菜单
(71, 7, 'BOM管理', 2, '/production/bom', 'production/bom/index', 'production:bom:list', 'SetUp', 1, 1),
(72, 7, '生产工单', 2, '/production/work-order', 'production/workOrder/index', 'production:workOrder:list', 'Operation', 2, 1),
(73, 7, 'MRP运算', 2, '/production/mrp', 'production/mrp/index', 'production:mrp:list', 'TrendCharts', 3, 1),
(74, 7, '工艺流程', 2, '/production/process', 'production/process/index', 'production:process:list', 'Connection', 4, 1),
-- 报表分析子菜单
(81, 8, '采购报表', 2, '/report/purchase', 'report/purchase/index', 'report:purchase:view', 'DataLine', 1, 1),
(82, 8, '销售报表', 2, '/report/sales', 'report/sales/index', 'report:sales:view', 'TrendCharts', 2, 1),
(83, 8, '库存报表', 2, '/report/inventory', 'report/inventory/index', 'report:inventory:view', 'PieChart', 3, 1),
(84, 8, '财务报表', 2, '/report/finance', 'report/finance/index', 'report:finance:view', 'Histogram', 4, 1),
-- 按钮权限 (type=3)
(91, 11, '新增用户', 3, null, null, 'system:user:add', null, 1, 1),
(92, 11, '修改用户', 3, null, null, 'system:user:edit', null, 2, 1),
(93, 11, '删除用户', 3, null, null, 'system:user:delete', null, 3, 1);

-- 管理员角色(ROLE_ADMIN)拥有所有菜单
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 普通用户角色(ROLE_USER)拥有基本查看权限(无系统管理)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 2), (2, 21), (2, 22), (2, 23), (2, 24), (2, 25),
(2, 3), (2, 31), (2, 32), (2, 33),
(2, 4), (2, 41), (2, 42), (2, 43),
(2, 5), (2, 51), (2, 52), (2, 53), (2, 54), (2, 55);

-- 管理员用户分配管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
