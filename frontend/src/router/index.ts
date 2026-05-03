import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/login',
      component: () => import('@/views/login/index.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      component: () => import('@/components/layout/AppLayout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '首页', icon: 'HomeFilled' }
        },
        // 占位路由 - 各模块子页面按需加载
        {
          path: 'system/user',
          component: () => import('@/views/system/user/index.vue'),
          meta: { title: '用户管理', permission: 'system:user:list' }
        },
        {
          path: 'system/role',
          component: () => import('@/views/system/role/index.vue'),
          meta: { title: '角色管理', permission: 'system:role:list' }
        },
        {
          path: 'system/menu',
          component: () => import('@/views/system/menu/index.vue'),
          meta: { title: '菜单管理', permission: 'system:menu:list' }
        },
        {
          path: 'system/dept',
          component: () => import('@/views/system/dept/index.vue'),
          meta: { title: '部门管理', permission: 'system:dept:list' }
        },
        {
          path: 'system/log',
          component: () => import('@/views/system/log/index.vue'),
          meta: { title: '操作日志', permission: 'system:log:list' }
        },
        {
          path: 'system/dict',
          component: () => import('@/views/system/dict/index.vue'),
          meta: { title: '数据字典', permission: 'system:dict:list' }
        },
        {
          path: 'system/config',
          component: () => import('@/views/system/config/index.vue'),
          meta: { title: '系统配置', permission: 'system:config:list' }
        },
        // 基础资料
        { path: 'base/supplier', component: () => import('@/views/base/supplier/index.vue'), meta: { title: '供应商管理' } },
        { path: 'base/customer', component: () => import('@/views/base/customer/index.vue'), meta: { title: '客户管理' } },
        { path: 'base/material', component: () => import('@/views/base/material/index.vue'), meta: { title: '物料管理' } },
        { path: 'base/warehouse', component: () => import('@/views/base/warehouse/index.vue'), meta: { title: '仓库管理' } },
        { path: 'base/employee', component: () => import('@/views/base/employee/index.vue'), meta: { title: '员工管理' } },
        // 采购管理
        { path: 'purchase/order', component: () => import('@/views/purchase/order/index.vue'), meta: { title: '采购订单' } },
        { path: 'purchase/receiving', component: () => import('@/views/purchase/receiving/index.vue'), meta: { title: '采购收货' } },
        { path: 'purchase/return', component: () => import('@/views/purchase/return/index.vue'), meta: { title: '采购退货' } },
        // 销售管理
        { path: 'sales/order', component: () => import('@/views/sales/order/index.vue'), meta: { title: '销售订单' } },
        { path: 'sales/delivery', component: () => import('@/views/sales/delivery/index.vue'), meta: { title: '销售发货' } },
        { path: 'sales/return', component: () => import('@/views/sales/return/index.vue'), meta: { title: '销售退货' } },
        // 库存管理
        { path: 'inventory/stock', component: () => import('@/views/inventory/stock/index.vue'), meta: { title: '库存查询' } },
        { path: 'inventory/stock-in', component: () => import('@/views/inventory/stockIn/index.vue'), meta: { title: '入库管理' } },
        { path: 'inventory/stock-out', component: () => import('@/views/inventory/stockOut/index.vue'), meta: { title: '出库管理' } },
        { path: 'inventory/transfer', component: () => import('@/views/inventory/transfer/index.vue'), meta: { title: '库存调拨' } },
        { path: 'inventory/check', component: () => import('@/views/inventory/check/index.vue'), meta: { title: '库存盘点' } },
        // 财务管理
        { path: 'finance/account', component: () => import('@/views/finance/account/index.vue'), meta: { title: '会计科目' } },
        { path: 'finance/voucher', component: () => import('@/views/finance/voucher/index.vue'), meta: { title: '记账凭证' } },
        { path: 'finance/invoice-ar', component: () => import('@/views/finance/invoice/index.vue'), meta: { title: '应收发票' } },
        { path: 'finance/invoice-ap', component: () => import('@/views/finance/invoice/index.vue'), meta: { title: '应付发票' } },
        { path: 'finance/payment', component: () => import('@/views/finance/payment/index.vue'), meta: { title: '收付款' } },
        { path: 'finance/expense', component: () => import('@/views/finance/expense/index.vue'), meta: { title: '费用报销' } },
        // 生产管理
        { path: 'production/bom', component: () => import('@/views/production/bom/index.vue'), meta: { title: 'BOM管理' } },
        { path: 'production/work-order', component: () => import('@/views/production/workOrder/index.vue'), meta: { title: '生产工单' } },
        { path: 'production/mrp', component: () => import('@/views/production/mrp/index.vue'), meta: { title: 'MRP运算' } },
        { path: 'production/process', component: () => import('@/views/production/process/index.vue'), meta: { title: '工艺流程' } },
        // 报表分析
        { path: 'report/purchase', component: () => import('@/views/report/purchase/index.vue'), meta: { title: '采购报表' } },
        { path: 'report/sales', component: () => import('@/views/report/sales/index.vue'), meta: { title: '销售报表' } },
        { path: 'report/inventory', component: () => import('@/views/report/inventory/index.vue'), meta: { title: '库存报表' } },
        { path: 'report/finance', component: () => import('@/views/report/finance/index.vue'), meta: { title: '财务报表' } }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  if (to.path === '/login') {
    next()
    return
  }
  const token = localStorage.getItem('accessToken')
  if (!token) {
    next('/login')
    return
  }
  next()
})

export default router
