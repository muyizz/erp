<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useTable } from '@/composables/useTable'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/purchase/orders', { params: p }), { orderNo: '', supplierId: undefined, status: null, dateRange: [] as string[] })

// Create/Edit
const visible = ref(false)
const formLoading = ref(false)
const isEdit = ref(false)
const formRef = ref()
const formData = reactive({
  id: undefined as number | undefined,
  supplierId: undefined as number | undefined,
  orderDate: '',
  deliveryDate: '',
  remark: ''
})
const items = ref<any[]>([])

function handleAddItem() {
  items.value.push({ materialId: null, materialCode: '', materialName: '', spec: '', unit: '', quantity: 0, unitPrice: 0, amount: 0, remark: '' })
}

function handleRemoveItem(index: number) {
  items.value.splice(index, 1)
}

function calcAmount(item: any) {
  item.amount = (item.quantity || 0) * (item.unitPrice || 0)
}

function openCreate() {
  isEdit.value = false
  formData.id = undefined
  formData.supplierId = undefined
  formData.orderDate = ''
  formData.deliveryDate = ''
  formData.remark = ''
  items.value = [{ materialId: null, materialCode: '', materialName: '', spec: '', unit: '', quantity: 0, unitPrice: 0, amount: 0, remark: '' }]
  visible.value = true
}

function openEdit(row: any) {
  isEdit.value = true
  formData.id = row.id
  formData.supplierId = row.supplierId
  formData.orderDate = row.orderDate
  formData.deliveryDate = row.deliveryDate
  formData.remark = row.remark
  // Load items from detail
  loadOrderItems(row.id)
  visible.value = true
}

async function loadOrderItems(orderId: number) {
  try {
    const res = await request.get(`/purchase/orders/${orderId}`)
    items.value = (res.data && res.data.items) || []
  } catch {
    items.value = []
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (items.value.length === 0) {
    ElMessage.warning('请至少添加一个物料明细')
    return
  }
  formLoading.value = true
  try {
    const url = isEdit.value ? `/purchase/orders/${formData.id}` : '/purchase/orders'
    await request({ method: isEdit.value ? 'put' : 'post', url, data: { header: { ...formData }, items: items.value } })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    visible.value = false
    loadData()
  } finally {
    formLoading.value = false
  }
}

function getStatusTag(status: string) {
  const map: Record<string, { text: string; type: string }> = {
    draft: { text: '草稿', type: 'info' },
    approved: { text: '已审批', type: 'warning' },
    confirmed: { text: '已确认', type: 'success' },
    cancelled: { text: '已取消', type: 'danger' }
  }
  return map[status] || { text: status, type: 'info' }
}

function handleApprove(row: any) {
  ElMessageBox.confirm('确定审批通过该采购订单吗？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/purchase/orders/${row.id}/approve`)
    ElMessage.success('审批成功')
    loadData()
  })
}

function handleCancel(row: any) {
  ElMessageBox.confirm('确定取消该采购订单吗？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/purchase/orders/${row.id}/cancel`)
    ElMessage.success('已取消')
    loadData()
  })
}

function handleDelete(row: any) {
  ElMessageBox.confirm('确定删除该采购订单吗？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/purchase/orders/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.orderNo" placeholder="订单号" clearable style="width: 160px" />
      <el-input v-model="searchParams.supplierId" placeholder="供应商ID" clearable style="width: 140px" />
      <el-select v-model="searchParams.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="草稿" value="draft" />
        <el-option label="已审批" value="approved" />
        <el-option label="已确认" value="confirmed" />
        <el-option label="已取消" value="cancelled" />
      </el-select>
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
      <el-button type="primary" @click="openCreate">新增采购订单</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="orderNo" label="订单号" width="150" />
      <el-table-column prop="supplierName" label="供应商" min-width="150" />
      <el-table-column prop="orderDate" label="订单日期" width="110" />
      <el-table-column prop="deliveryDate" label="交货日期" width="110" />
      <el-table-column prop="totalQty" label="总数量" width="90" align="right" />
      <el-table-column prop="totalAmount" label="总金额" width="110" align="right" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status).type as any">{{ getStatusTag(row.status).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="warning" @click="handleApprove(row)">审批</el-button>
          <el-button v-if="row.status === 1" size="small" @click="openEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 1" size="small" type="danger" @click="handleCancel(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page" v-model:page-size="pageSize" :total="total"
      :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next"
      @current-change="handlePageChange" @size-change="handleSizeChange"
      style="margin-top: 16px; justify-content: flex-end"
    />

    <!-- Create/Edit Dialog -->
    <el-dialog :title="isEdit ? '编辑采购订单' : '新增采购订单'" v-model="visible" width="800px" @close="visible = false">
      <el-form ref="formRef" :model="formData" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId" :rules="[{ required: true, message: '请选择供应商' }]">
              <el-input-number v-model="formData.supplierId" :min="1" placeholder="供应商ID" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单日期" prop="orderDate" :rules="[{ required: true, message: '请选择日期' }]">
              <el-date-picker v-model="formData.orderDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="交货日期" prop="deliveryDate">
          <el-date-picker v-model="formData.deliveryDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">物料明细</el-divider>
      <el-button size="small" type="primary" @click="handleAddItem" style="margin-bottom: 8px">添加物料</el-button>
      <div style="max-height: 300px; overflow-y: auto">
        <el-table :data="items" border size="small">
          <el-table-column label="物料编码" width="120">
            <template #default="{ $index }">
              <el-input v-model="items[$index].materialCode" size="small" placeholder="编码" />
            </template>
          </el-table-column>
          <el-table-column label="物料名称" width="120">
            <template #default="{ $index }">
              <el-input v-model="items[$index].materialName" size="small" placeholder="名称" />
            </template>
          </el-table-column>
          <el-table-column label="规格" width="80">
            <template #default="{ $index }">
              <el-input v-model="items[$index].spec" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="单位" width="70">
            <template #default="{ $index }">
              <el-input v-model="items[$index].unit" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="数量" width="100">
            <template #default="{ $index }">
              <el-input-number v-model="items[$index].quantity" :min="1" size="small" style="width: 100%" @change="calcAmount(items[$index])" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="100">
            <template #default="{ $index }">
              <el-input-number v-model="items[$index].unitPrice" :min="0" :precision="2" size="small" style="width: 100%" @change="calcAmount(items[$index])" />
            </template>
          </el-table-column>
          <el-table-column label="金额" width="110">
            <template #default="{ $index }">{{ items[$index].amount?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="备注" width="100">
            <template #default="{ $index }">
              <el-input v-model="items[$index].remark" size="small" placeholder="备注" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template #default="{ $index }">
              <el-button size="small" type="danger" @click="handleRemoveItem($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
