<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('ar')
const loading = ref(false)
const data = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const searchParams = ref({ invoiceNo: '', companyName: '', status: null as number | null })

const invoiceType = computed(() => activeTab.value === 'ar' ? 1 : 2)

async function loadData() {
  loading.value = true
  try {
    const res = await request.get('/finance/invoices', {
      params: {
        page: page.value, pageSize: pageSize.value,
        invoiceType: invoiceType.value,
        ...(searchParams.value.invoiceNo ? { invoiceNo: searchParams.value.invoiceNo } : {}),
        ...(searchParams.value.status !== null ? { status: searchParams.value.status } : {})
      }
    })
    data.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  page.value = 1
  searchParams.value = { invoiceNo: '', companyName: '', status: null }
  loadData()
}
function handleSearch() { page.value = 1; loadData() }
function handleReset() { searchParams.value = { invoiceNo: '', companyName: '', status: null }; handleSearch() }
function handlePageChange(p: number) { page.value = p; loadData() }
function handleSizeChange(s: number) { pageSize.value = s; page.value = 1; loadData() }

const statusMap: Record<number, string> = { 1: '草稿', 2: '部分付款', 3: '已付清' }

function handleDelete(row: any) {
  ElMessageBox.confirm('确定删除该发票吗？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/finance/invoices/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="应收发票 (AR)" name="ar" />
      <el-tab-pane label="应付发票 (AP)" name="ap" />
    </el-tabs>

    <div class="search-bar">
      <el-input v-model="searchParams.invoiceNo" placeholder="发票号" clearable style="width: 180px" />
      <el-select v-model="searchParams.status" placeholder="状态" clearable style="width: 130px">
        <el-option label="草稿" :value="1" />
        <el-option label="部分付款" :value="2" />
        <el-option label="已付清" :value="3" />
      </el-select>
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="invoiceNo" label="发票号" width="160" />
      <el-table-column prop="companyId" label="往来单位ID" width="100" />
      <el-table-column prop="amount" label="金额" width="120" align="right" />
      <el-table-column prop="paidAmount" label="已付" width="120" align="right" />
      <el-table-column prop="taxAmount" label="税额" width="100" align="right" />
      <el-table-column prop="invoiceDate" label="开票日期" width="110" />
      <el-table-column prop="dueDate" label="到期日" width="110" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 3 ? 'success' : row.status === 2 ? 'warning' : 'info'">{{ statusMap[row.status] || row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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
