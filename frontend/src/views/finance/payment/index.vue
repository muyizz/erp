<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('receipt')
const loading = ref(false)
const data = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const searchParams = ref({ paymentNo: '', companyId: null as number | null })

const paymentType = computed(() => activeTab.value === 'receipt' ? 1 : 2)

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/finance/payments', {
      params: {
        page: page.value, pageSize: pageSize.value,
        paymentType: paymentType.value,
        ...(searchParams.value.paymentNo ? { paymentNo: searchParams.value.paymentNo } : {})
      }
    })
    data.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

function handleTabChange() { page.value = 1; searchParams.value = { paymentNo: '', companyId: null }; loadData() }
function handleSearch() { page.value = 1; loadData() }
function handleReset() { searchParams.value = { paymentNo: '', companyId: null }; handleSearch() }
function handlePageChange(p: number) { page.value = p; loadData() }
function handleSizeChange(s: number) { pageSize.value = s; page.value = 1; loadData() }

const statusMap: Record<number, string> = { 1: '草稿', 2: '已确认' }
const methodMap: Record<number, string> = { 1: '现金', 2: '银行转账', 3: '支票' }

function handleConfirm(row: any) {
  const label = activeTab.value === 'receipt' ? '收款' : '付款'
  ElMessageBox.confirm(`确认该${label}单？`, '提示', { type: 'warning' }).then(async () => {
    await request.put(`/finance/payments/${row.id}`, { ...row, status: 2 })
    ElMessage.success('确认成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="收款单" name="receipt" />
      <el-tab-pane label="付款单" name="disbursement" />
    </el-tabs>

    <div class="search-bar">
      <el-input v-model="searchParams.paymentNo" placeholder="单号" clearable style="width: 180px" />
      <el-input v-model="searchParams.companyId" placeholder="往来单位ID" clearable style="width: 140px" />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="paymentNo" label="单号" width="160" />
      <el-table-column prop="companyId" label="往来单位ID" width="100" />
      <el-table-column prop="amount" label="金额" width="120" align="right" />
      <el-table-column prop="paymentMethod" label="支付方式" width="100">
        <template #default="{ row }">{{ methodMap[row.paymentMethod] || row.paymentMethod }}</template>
      </el-table-column>
      <el-table-column prop="paymentDate" label="日期" width="110" />
      <el-table-column prop="bankAccount" label="银行账号" width="160" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 2 ? 'success' : 'info'">{{ statusMap[row.status] || row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="success" @click="handleConfirm(row)">确认</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page" v-model:page-size="pageSize" :total="total"
      :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next"
      @current-change="handlePageChange" @size-change="handleSizeChange"
      style="margin-top: 16px; justify-content: flex-end"
    />
  </div>
</template>
