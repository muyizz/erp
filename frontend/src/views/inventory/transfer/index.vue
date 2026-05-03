<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useTable } from '@/composables/useTable'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/inventory/transfers', { params: p }), { docNo: '', fromWarehouseId: undefined, toWarehouseId: undefined, dateRange: [] as string[] })

// Create transfer
const visible = ref(false)
const formLoading = ref(false)
const formRef = ref()
const formData = reactive({
  fromWarehouseId: undefined as number | undefined,
  toWarehouseId: undefined as number | undefined,
  remark: ''
})
const items = ref<any[]>([])

function handleAddItem() {
  items.value.push({ materialId: null, materialCode: '', materialName: '', unit: '', quantity: 0, remark: '' })
}

function handleRemoveItem(index: number) {
  items.value.splice(index, 1)
}

function openCreate() {
  formData.fromWarehouseId = undefined
  formData.toWarehouseId = undefined
  formData.remark = ''
  items.value = [{ materialId: null, materialCode: '', materialName: '', unit: '', quantity: 0, remark: '' }]
  visible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (items.value.length === 0) {
    ElMessage.warning('请至少添加一个调拨物料')
    return
  }
  formLoading.value = true
  try {
    await request.post('/inventory/transfers', { header: { ...formData }, items: items.value })
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
  ElMessageBox.confirm('确认调拨后库存将从来源仓库转移到目标仓库，确定继续？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/inventory/transfers/${row.id}/confirm`)
    ElMessage.success('调拨确认成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.docNo" placeholder="单据号" clearable style="width: 160px" />
      <el-input v-model="searchParams.fromWarehouseId" placeholder="来源仓库ID" clearable style="width: 140px" />
      <el-input v-model="searchParams.toWarehouseId" placeholder="目标仓库ID" clearable style="width: 140px" />
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
      <el-button type="primary" @click="openCreate">新增调拨单</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="docNo" label="单据号" width="150" />
      <el-table-column prop="fromWarehouseName" label="来源仓库" width="130" />
      <el-table-column prop="toWarehouseName" label="目标仓库" width="130" />
      <el-table-column prop="totalQty" label="总数量" width="90" align="right" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status).type as any">{{ getStatusTag(row.status).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="170" />
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

    <!-- Create Transfer Dialog -->
    <el-dialog title="新增调拨单" v-model="visible" width="700px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="来源仓库" prop="fromWarehouseId" :rules="[{ required: true, message: '请选择来源仓库' }]">
          <el-input-number v-model="formData.fromWarehouseId" :min="1" placeholder="仓库ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="目标仓库" prop="toWarehouseId" :rules="[{ required: true, message: '请选择目标仓库' }]">
          <el-input-number v-model="formData.toWarehouseId" :min="1" placeholder="仓库ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">调拨明细</el-divider>
      <el-button size="small" type="primary" @click="handleAddItem" style="margin-bottom: 8px">添加物料</el-button>
      <el-table :data="items" border size="small">
        <el-table-column label="物料编码" width="140">
          <template #default="{ $index }">
            <el-input v-model="items[$index].materialCode" size="small" placeholder="编码" />
          </template>
        </el-table-column>
        <el-table-column label="物料名称" min-width="140">
          <template #default="{ $index }">
            <el-input v-model="items[$index].materialName" size="small" placeholder="名称" />
          </template>
        </el-table-column>
        <el-table-column label="单位" width="80">
          <template #default="{ $index }">
            <el-input v-model="items[$index].unit" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="数量" width="120">
          <template #default="{ $index }">
            <el-input-number v-model="items[$index].quantity" :min="1" size="small" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="备注" width="120">
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

      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
