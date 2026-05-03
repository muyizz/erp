<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useTable } from '@/composables/useTable'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/sales/deliveries', { params: p }), { docNo: '', customerId: undefined, orderNo: '', dateRange: [] as string[] })

const visible = ref(false)
const formLoading = ref(false)
const formRef = ref()
const formData = reactive({
  orderId: undefined as number | undefined,
  warehouseId: undefined as number | undefined,
  deliveryDate: '',
  remark: ''
})
const items = ref<any[]>([])

async function loadOrderItemsForDelivery(orderId: number) {
  try {
    const res = await request.get(`/sales/orders/${orderId}`)
    items.value = ((res.data && res.data.items) || []).map((item: any) => ({
      ...item,
      orderQty: item.quantity,
      quantity: item.quantity,
      deliveryQty: item.quantity
    }))
  } catch {
    items.value = []
  }
}

function openCreate() {
  formData.orderId = undefined
  formData.warehouseId = undefined
  formData.deliveryDate = ''
  formData.remark = ''
  items.value = []
  visible.value = true
}

async function handleOrderChange(orderId: number) {
  if (orderId) {
    await loadOrderItemsForDelivery(orderId)
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (items.value.length === 0) {
    ElMessage.warning('请先选择销售订单')
    return
  }
  formLoading.value = true
  try {
    await request.post('/sales/deliveries', { header: { ...formData }, items: items.value })
    ElMessage.success('创建成功')
    visible.value = false
    loadData()
  } finally {
    formLoading.value = false
  }
}

function getStatusTag(status: string) {
  const map: Record<string, { text: string; type: string }> = {
    draft: { text: '草稿', type: 'info' },
    confirmed: { text: '已确认', type: 'success' },
    cancelled: { text: '已取消', type: 'danger' }
  }
  return map[status] || { text: status, type: 'info' }
}

function handleConfirm(row: any) {
  ElMessageBox.confirm('确认发货后库存将减少，确定继续？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/sales/deliveries/${row.id}/confirm`)
    ElMessage.success('发货确认成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.docNo" placeholder="发货单号" clearable style="width: 160px" />
      <el-input v-model="searchParams.orderNo" placeholder="销售订单号" clearable style="width: 160px" />
      <el-input v-model="searchParams.customerId" placeholder="客户ID" clearable style="width: 140px" />
      <el-date-picker
        v-model="searchParams.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 260px"
      />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    <div class="toolbar">
      <el-button type="primary" @click="openCreate">新增发货单</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="docNo" label="发货单号" width="150" />
      <el-table-column prop="orderNo" label="销售订单号" width="150" />
      <el-table-column prop="customerName" label="客户" min-width="150" />
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column prop="totalQty" label="总数量" width="90" align="right" />
      <el-table-column prop="totalAmount" label="总金额" width="110" align="right" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status).type as any">{{ getStatusTag(row.status).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="deliveredAt" label="发货日期" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="success" @click="handleConfirm(row)">确认发货</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page" v-model:page-size="pageSize" :total="total"
      :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next"
      @current-change="handlePageChange" @size-change="handleSizeChange"
      style="margin-top: 16px; justify-content: flex-end"
    />

    <el-dialog title="新增发货单" v-model="visible" width="800px">
      <el-form ref="formRef" :model="formData" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="销售订单" prop="orderId" :rules="[{ required: true, message: '请选择销售订单' }]">
              <el-input-number v-model="formData.orderId" :min="1" placeholder="订单ID" style="width: 100%" @change="handleOrderChange" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发货仓库" prop="warehouseId" :rules="[{ required: true, message: '请选择仓库' }]">
              <el-input-number v-model="formData.warehouseId" :min="1" placeholder="仓库ID" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="发货日期" prop="deliveryDate">
          <el-date-picker v-model="formData.deliveryDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">发货明细</el-divider>
      <el-table :data="items" border size="small">
        <el-table-column prop="materialCode" label="物料编码" width="120" />
        <el-table-column prop="materialName" label="物料名称" width="140" />
        <el-table-column prop="spec" label="规格" width="80" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column prop="orderQty" label="订单数量" width="90" align="right" />
        <el-table-column label="发货数量" width="120">
          <template #default="{ $index }">
            <el-input-number v-model="items[$index].deliveryQty" :min="0" :max="items[$index].orderQty" size="small" style="width: 100%" />
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
