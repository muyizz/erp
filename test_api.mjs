// ERP API 测试脚本
const BASE = 'http://localhost:8080/api/v1';
let token = '', pass = 0, fail = 0;

async function test(desc, method, url, data, expectStatus = 200) {
    try {
        const headers = { 'Content-Type': 'application/json' };
        if (token) headers['Authorization'] = `Bearer ${token}`;
        const opts = { method, headers };
        if (data) opts.body = JSON.stringify(data);
        const resp = await fetch(`${BASE}${url}`, opts);
        const body = await resp.json();
        const ok = resp.status === expectStatus || (resp.status === 200 && body.code === 200);
        if (ok) { console.log(`  [PASS] ${desc}`); pass++; }
        else { console.log(`  [FAIL] ${desc} (exp ${expectStatus}, got ${resp.status})`, JSON.stringify(body).slice(0,150)); fail++; }
        return body;
    } catch (e) { console.log(`  [FAIL] ${desc} - ${e.message}`); fail++; return null; }
}

async function main() {
    console.log('=== ERP 全功能 API 测试 ===\n');
    const now = Date.now();

    // === 认证 ===
    console.log('--- 认证 ---');
    const loginRes = await test('登录 admin', 'POST', '/auth/login', { username: 'admin', password: 'admin123' });
    if (loginRes?.data?.accessToken) token = loginRes.data.accessToken;
    await test('错误密码', 'POST', '/auth/login', { username: 'admin', password: 'wrong' }, 200); // body: code=400
    await test('用户信息', 'GET', '/auth/userinfo');

    // === 系统管理 ===
    console.log('\n--- 系统管理 ---');
    await test('用户列表', 'GET', '/system/users?page=1&pageSize=5');
    await test('角色列表', 'GET', '/system/roles?page=1&pageSize=5');
    await test('菜单树', 'GET', '/system/menus/tree');
    await test('部门树', 'GET', '/system/depts/tree');
    await test('系统配置', 'GET', '/system/configs?page=1&pageSize=5');
    await test('操作日志', 'GET', '/system/logs?page=1&pageSize=5');
    await test('字典类型', 'GET', '/system/dicts/types?page=1&pageSize=5');
    await test('创建用户', 'POST', '/system/users', { username: 'u'+now, password: 'test123', realName: '测试员', status: 1 });

    // === 基础资料 ===
    console.log('\n--- 基础资料 ---');
    await test('供应商列表', 'GET', '/base/suppliers?page=1&pageSize=5');
    await test('客户列表', 'GET', '/base/customers?page=1&pageSize=5');
    await test('物料列表', 'GET', '/base/materials?page=1&pageSize=5');
    await test('物料分类树', 'GET', '/base/materials/categories/tree');
    await test('仓库列表', 'GET', '/base/warehouses?page=1&pageSize=5');
    await test('员工列表', 'GET', '/base/employees?page=1&pageSize=5');

    // === 库存管理 ===
    console.log('\n--- 库存管理 ---');
    await test('库存查询', 'GET', '/inventory/stocks?page=1&pageSize=5');
    await test('入库单列表', 'GET', '/inventory/stock-ins?page=1&pageSize=5');
    await test('出库单列表', 'GET', '/inventory/stock-outs?page=1&pageSize=5');
    await test('调拨单列表', 'GET', '/inventory/transfers?page=1&pageSize=5');
    await test('盘点单列表', 'GET', '/inventory/checks?page=1&pageSize=5');
    await test('安全库存预警', 'GET', '/inventory/stocks/alerts/safety-stock');

    // === 采购管理 ===
    console.log('\n--- 采购管理 ---');
    await test('采购订单列表', 'GET', '/purchase/orders?page=1&pageSize=5');
    await test('采购收货列表', 'GET', '/purchase/receivings?page=1&pageSize=5');
    await test('采购退货列表', 'GET', '/purchase/returns?page=1&pageSize=5');

    // === 销售管理 ===
    console.log('\n--- 销售管理 ---');
    await test('销售订单列表', 'GET', '/sales/orders?page=1&pageSize=5');
    await test('销售发货列表', 'GET', '/sales/deliveries?page=1&pageSize=5');
    await test('销售退货列表', 'GET', '/sales/returns?page=1&pageSize=5');

    // === 财务管理 ===
    console.log('\n--- 财务管理 ---');
    await test('会计科目树', 'GET', '/finance/accounts/tree');
    await test('凭证列表', 'GET', '/finance/vouchers?page=1&pageSize=5');
    await test('发票列表', 'GET', '/finance/invoices?page=1&pageSize=5');
    await test('收付款列表', 'GET', '/finance/payments?page=1&pageSize=5');
    await test('报销列表', 'GET', '/finance/expenses?page=1&pageSize=5');

    // === 生产管理 ===
    console.log('\n--- 生产管理 ---');
    await test('BOM列表', 'GET', '/production/boms?page=1&pageSize=5');
    await test('BOM详情', 'GET', '/production/boms/1');
    await test('工单列表', 'GET', '/production/work-orders?page=1&pageSize=5');
    await test('工艺列表', 'GET', '/production/processes?page=1&pageSize=5');

    // === 报表分析 ===
    console.log('\n--- 报表分析 ---');
    await test('采购报表', 'GET', '/reports/purchase/summary?startDate=2026-01-01&endDate=2026-12-31');
    await test('销售报表', 'GET', '/reports/sales/summary?startDate=2026-01-01&endDate=2026-12-31');
    await test('库存快照', 'GET', '/reports/inventory/snapshot');
    await test('库存周转', 'GET', '/reports/inventory/turnover');
    await test('试算平衡', 'GET', '/reports/finance/trial-balance');
    await test('应收账龄', 'GET', '/reports/finance/ar-aging');
    await test('应付账龄', 'GET', '/reports/finance/ap-aging');

    // === CRUD 创建测试 ===
    console.log('\n--- CRUD 创建测试 ---');
    await test('新增供应商', 'POST', '/base/suppliers',
        { supplierCode: 'SUP-'+now, supplierName: '测试供应商', contactPerson: '测', status: 1 });
    await test('新增客户', 'POST', '/base/customers',
        { customerCode: 'CUS-'+now, customerName: '测试客户', contactPerson: '试', status: 1 });
    await test('新增物料', 'POST', '/base/materials',
        { materialCode: 'MAT-'+now, materialName: '测试物料', categoryId: 1, unit: '个', salePrice: 10, purchasePrice: 5, safetyStock: 100, status: 1 });
    await test('创建采购订单', 'POST', '/purchase/orders',
        { header: { supplierId: 1, orderDate: '2026-05-03' }, items: [{ materialId: 1, lineNo: 1, quantity: 100, unitPrice: 0.04 }] });
    await test('创建销售订单', 'POST', '/sales/orders',
        { header: { customerId: 1, orderDate: '2026-05-03' }, items: [{ materialId: 7, lineNo: 1, quantity: 10, unitPrice: 199 }] });
    await test('创建入库单', 'POST', '/inventory/stock-ins',
        { header: { warehouseId: 1, docType: 6, inDate: '2026-05-03' }, items: [{ materialId: 1, quantity: 500, unitCost: 0.03 }] });
    await test('创建凭证', 'POST', '/finance/vouchers',
        { header: { voucherDate: '2026-05-03', voucherType: 1, summary: '测试' }, items: [{ accountId: 6601, debit: 100, credit: 0 }, { accountId: 1002, debit: 0, credit: 100 }] });
    await test('MRP计算', 'POST', '/production/mrp', { demands: [{ productId: 7, quantity: 100 }] });

    console.log(`\n=== 完成: ${pass} 通过, ${fail} 失败 ===`);
}

main().catch(e => { console.error('错误:', e.message); });
