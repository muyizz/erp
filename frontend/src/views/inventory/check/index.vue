<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useTable } from '@/composables/useTable'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/inventory/checks', { params: p }), { docNo: '', warehouseId: undefined, dateRange: [] as string[] })

// Create check
const visible = ref(false)
const formLoading = ref(false)
const formRef = ref()
const formData = reactive({
  warehouseId: undefined as number | undefined,
  checkDate: '',
  remark: ''
})
const items = ref<any[]>([])

function handleAddItem() {
  items.value.push({ materialId: null, materialCode: '', materialName: '', unit: '', bookQty: 0, actualQty: 0, remark: '' })
}

function handleRemoveItem(index: number) {
  items.value.splice(index, 1)
}

function openCreate() {
  formData.warehouseId = undefined
  formData.checkDate = ''
  formData.remark = ''
  items.value = [{ materialId: null, materialCode: '', materialName: '', unit: '', bookQty: 0, actualQty: 0, remark: '' }]
  visible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (items.value.length === 0) {
    ElMessage.warning('请至少添加一个盘点物料')
    return
  }
  formLoading.value = true
  try {
    await request.post('/inventory/checks', { header: { ...formData }, items: items.value })
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
  ElMessageBox.confirm('确认盘点后将更新库存，确定继续？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/inventory/checks/${row.id}/confirm`)
    ElMessage.success('盘点确认成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.docNo" placeholder="盘点单号" clearable style="width: 160px" />
      <el-input v-model="searchParams.warehouseId" placeholder="仓库ID" clearable style="width: 130px" />
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
      <el-button type="primary" @click="openCreate">新增盘点单</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="docNo" label="盘点单号" width="150" />
      <el-table-column prop="warehouseName" label="盘点仓库" width="130" />
      <el-table-column prop="itemCount" label="物料数" width="80" align="right" />
      <el-table-column prop="profitQty" label="盘盈数量" width="90" align="right" />
      <el-table-column prop="lossQty" label="盘亏数量" width="90" align="right" />
      <el-table-column prop="checkDate" label="盘点日期" width="110" />
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

    <!-- Create Check Dialog -->
    <el-dialog title="新增盘点单" v-model="visible" width="750px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="盘点仓库" prop="warehouseId" :rules="[{ required: true, message: '请选择仓库' }]">
              <el-input-number v-model="formData.warehouseId" :min="1" placeholder="仓库ID" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="盘点日期" prop="checkDate" :rules="[{ required: true, message: '请选择日期' }]">
              <el-date-picker v-model="formData.checkDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">盘点明细</el-divider>
      <el-button size="small" type="primary" @click="handleAddItem" style="margin-bottom: 8px">添加物料</el-button>
      <el-table :data="items" border size="small">
        <el-table-column label="物料编码" width="130">
          <template #default="{ $index }">
            <el-input v-model="items[$index].materialCode" size="small" placeholder="编码" />
          </template>
        </el-table-column>
        <el-table-column label="物料名称" min-width="130">
          <template #default="{ $index }">
            <el-input v-model="items[$index].materialName" size="small" placeholder="名称" />
          </template>
        </el-table-column>
        <el-table-column label="单位" width="70">
          <template #default="{ $index }">
            <el-input v-model="items[$index].unit" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="账面数量" width="100">
          <template #default="{ $index }">
            <el-input-number v-model="items[$index].bookQty" :min="0" size="small" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="实盘数量" width="100">
          <template #default="{ $index }">
            <el-input-number v-model="items[$index].actualQty" :min="0" size="small" style="width: 100%" />
          </template>
        </el-table-column>
        <el-table-column label="差异" width="80">
          <template #default="{ $index }">
            <span :style="{ color: items[$index].actualQty - items[$index].bookQty >= 0 ? '#67c23a' : '#f56c6c' }">
              {{ items[$index].actualQty - items[$index].bookQty }}
            </span>
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
