<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'
const userStore = useUserStore()
const stats = ref({ salesOrders: 0, purchaseOrders: 0, pendingReceiving: 0, lowStock: 0, salesAmount: 0, purchaseAmount: 0 })
async function load() {
  try {
    const salOrders = await request.get('/sales/orders', { params: { page: 1, pageSize: 1000 } })
    const purOrders = await request.get('/purchase/orders', { params: { page: 1, pageSize: 1000 } })
    const lowStock = await request.get('/reports/inventory/turnover')
    const salesSummary = await request.get('/reports/sales/summary', { params: { startDate: new Date().toISOString().slice(0,7)+'-01', endDate: new Date().toISOString().slice(0,10) } })
    const purSummary = await request.get('/reports/purchase/summary', { params: { startDate: new Date().toISOString().slice(0,7)+'-01', endDate: new Date().toISOString().slice(0,10) } })
    stats.value.salesOrders = salOrders.data?.total || 0
    stats.value.purchaseOrders = purOrders.data?.total || 0
    stats.value.pendingReceiving = (purOrders.data?.records || []).filter((r:any) => r.status === 1 || r.status === 2).length
    const lt = lowStock.data || []
    stats.value.lowStock = lt.filter((r:any) => r.current_stock <= r.safety_stock).length
    stats.value.salesAmount = (salesSummary.data || []).reduce((a:number,b:any)=>a+(parseFloat(b.total_amount)||0),0)
    stats.value.purchaseAmount = (purSummary.data || []).reduce((a:number,b:any)=>a+(parseFloat(b.total_amount)||0),0)
  } catch(e) { console.error(e) }
}
onMounted(load)
</script>
<template>
  <div class="dashboard">
    <h2>欢迎回来，{{ userStore.realName || userStore.username }}</h2>
    <p style="color:#909399;margin-top:4px">今天是 {{ new Date().toLocaleDateString('zh-CN') }}，系统运行正常</p>
    <el-row :gutter="20" style="margin-top: 24px">
      <el-col :span="8"><el-card shadow="hover"><div class="stat-card"><div class="stat-value">{{ stats.salesOrders }}</div><div class="stat-label">销售订单总数</div><div style="color:#67c23a;font-size:12px;margin-top:4px">本月销售: ¥{{ stats.salesAmount.toLocaleString() }}</div></div></el-card></el-col>
      <el-col :span="8"><el-card shadow="hover"><div class="stat-card"><div class="stat-value">{{ stats.purchaseOrders }}</div><div class="stat-label">采购订单总数</div><div style="color:#409eff;font-size:12px;margin-top:4px">本月采购: ¥{{ stats.purchaseAmount.toLocaleString() }}</div></div></el-card></el-col>
      <el-col :span="8"><el-card shadow="hover"><div class="stat-card"><div class="stat-value" style="color:#e6a23c">{{ stats.pendingReceiving }}</div><div class="stat-label">待收货采购单</div></div></el-card></el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 16px">
      <el-col :span="8"><el-card shadow="hover"><div class="stat-card"><div class="stat-value" style="color:#f56c6c">{{ stats.lowStock }}</div><div class="stat-label">低库存预警</div></div></el-card></el-col>
    </el-row>
    <el-card style="margin-top: 20px">
      <template #header>快捷操作</template>
      <el-space wrap>
        <el-button type="primary" @click="$router.push('/purchase/order')">新建采购订单</el-button>
        <el-button type="success" @click="$router.push('/sales/order')">新建销售订单</el-button>
        <el-button type="warning" @click="$router.push('/inventory/stock')">查看库存</el-button>
        <el-button type="info" @click="$router.push('/report/sales')">销售报表</el-button>
      </el-space>
    </el-card>
  </div>
</template>
<style scoped>
.dashboard h2 { margin: 0; font-size: 20px; }
.stat-card { text-align: center; padding: 8px 0; }
.stat-value { font-size: 32px; font-weight: 700; color: #303133; }
.stat-label { margin-top: 8px; color: #909399; font-size: 14px; }
</style>