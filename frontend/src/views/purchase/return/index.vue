<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useTable } from '@/composables/useTable'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/purchase/returns', { params: p }), { docNo: '', supplierId: undefined, dateRange: [] as string[] })

const visible = ref(false)
const formLoading = ref(false)
const formRef = ref()
const formData = reactive({
  receivingId: undefined as number | undefined,
  supplierId: undefined as number | undefined,
  returnDate: '',
  reason: ''
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
  formData.receivingId = undefined
  formData.supplierId = undefined
  formData.returnDate = ''
  formData.reason = ''
  items.value = [{ materialId: null, materialCode: '', materialName: '', spec: '', unit: '', quantity: 0, unitPrice: 0, amount: 0, remark: '' }]
  visible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (items.value.length === 0) {
    ElMessage.warning('请至少添加一个退货物料')
    return
  }
  formLoading.value = true
  try {
    await request.post('/purchase/returns', { header: { ...formData }, items: items.value })
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
  ElMessageBox.confirm('确认退货后库存将减少，确定继续？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/purchase/returns/${row.id}/confirm`)
    ElMessage.success('退货确认成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.docNo" placeholder="退货单号" clearable style="width: 160px" />
      <el-input v-model="searchParams.supplierId" placeholder="供应商ID" clearable style="width: 140px" />
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
      <el-button type="primary" @click="openCreate">新增退货单</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="docNo" label="退货单号" width="150" />
      <el-table-column prop="receivingNo" label="关联收货单" width="150" />
      <el-table-column prop="supplierName" label="供应商" min-width="150" />
      <el-table-column prop="totalQty" label="总数量" width="90" align="right" />
      <el-table-column prop="totalAmount" label="总金额" width="110" align="right" />
      <el-table-column prop="reason" label="退货原因" width="120" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status).type as any">{{ getStatusTag(row.status).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="returnDate" label="退货日期" width="110" />
      <el-table-column label="操作" width="160" fixed="right">
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

    <el-dialog title="新增退货单" v-model="visible" width="800px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="关联收货单" prop="receivingId" :rules="[{ required: true, message: '请输入收货单ID' }]">
              <el-input-number v-model="formData.receivingId" :min="1" placeholder="收货单ID" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退货日期" prop="returnDate" :rules="[{ required: true, message: '请选择日期' }]">
              <el-date-picker v-model="formData.returnDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="退货原因" prop="reason">
          <el-select v-model="formData.reason" placeholder="请选择" style="width: 100%">
            <el-option label="质量问题" value="quality" />
            <el-option label="数量不符" value="quantity" />
            <el-option label="规格不符" value="spec" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-divider content-position="left">退货明细</el-divider>
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
