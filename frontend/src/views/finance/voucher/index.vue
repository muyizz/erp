<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useTable } from '@/composables/useTable'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/finance/vouchers', { params: p }), { voucherNo: '', status: null, dateRange: [] as string[] })

const visible = ref(false)
const formLoading = ref(false)
const isEdit = ref(false)
const formRef = ref()
const formData = reactive({
  id: undefined as number | undefined,
  voucherDate: '',
  remark: ''
})
const entries = ref<any[]>([])

let debitTotal = ref(0)
let creditTotal = ref(0)

function calcTotals() {
  debitTotal.value = entries.value.reduce((sum: number, e: any) => sum + (e.debitAmount || 0), 0)
  creditTotal.value = entries.value.reduce((sum: number, e: any) => sum + (e.creditAmount || 0), 0)
}

function handleAddEntry() {
  entries.value.push({ accountId: undefined, accountCode: '', accountName: '', summary: '', debitAmount: 0, creditAmount: 0 })
}

function handleRemoveEntry(index: number) {
  entries.value.splice(index, 1)
  calcTotals()
}

function openCreate() {
  isEdit.value = false
  formData.id = undefined
  formData.voucherDate = ''
  formData.remark = ''
  entries.value = [{ accountId: undefined, accountCode: '', accountName: '', summary: '', debitAmount: 0, creditAmount: 0 }]
  debitTotal.value = 0
  creditTotal.value = 0
  visible.value = true
}

function openEdit(row: any) {
  isEdit.value = true
  formData.id = row.id
  formData.voucherDate = row.voucherDate
  formData.remark = row.remark
  loadVoucherEntries(row.id)
  visible.value = true
}

async function loadVoucherEntries(voucherId: number) {
  try {
    const res = await request.get(`/finance/vouchers/${voucherId}/entries`)
    entries.value = res.data || []
    calcTotals()
  } catch {
    entries.value = []
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (entries.value.length < 2) {
    ElMessage.warning('凭证至少需要两条分录')
    return
  }
  if (debitTotal.value !== creditTotal.value) {
    ElMessage.warning('借贷不平衡，请检查金额')
    return
  }
  formLoading.value = true
  try {
    const url = isEdit.value ? `/finance/vouchers/${formData.id}` : '/finance/vouchers'
    await request({ method: isEdit.value ? 'put' : 'post', url, data: { ...formData, entries: entries.value } })
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
    posted: { text: '已过账', type: 'success' },
    cancelled: { text: '已作废', type: 'danger' }
  }
  return map[status] || { text: status, type: 'info' }
}

function handlePost(row: any) {
  ElMessageBox.confirm('过账后将影响科目余额，确定继续？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/finance/vouchers/${row.id}/post`)
    ElMessage.success('过账成功')
    loadData()
  })
}

function handleDelete(row: any) {
  ElMessageBox.confirm('确定删除该凭证吗？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/finance/vouchers/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.voucherNo" placeholder="凭证号" clearable style="width: 160px" />
      <el-select v-model="searchParams.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="草稿" value="draft" />
        <el-option label="已过账" value="posted" />
        <el-option label="已作废" value="cancelled" />
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
      <el-button type="primary" @click="openCreate">新增凭证</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="voucherNo" label="凭证号" width="150" />
      <el-table-column prop="voucherDate" label="日期" width="110" />
      <el-table-column prop="entriesCount" label="分录数" width="70" align="right" />
      <el-table-column prop="debitTotal" label="借方合计" width="110" align="right" />
      <el-table-column prop="creditTotal" label="贷方合计" width="110" align="right" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status).type as any">{{ getStatusTag(row.status).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="success" @click="handlePost(row)">过账</el-button>
          <el-button v-if="row.status === 1" size="small" @click="openEdit(row)">编辑</el-button>
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

    <el-dialog :title="isEdit ? '编辑凭证' : '新增凭证'" v-model="visible" width="900px">
      <el-form ref="formRef" :model="formData" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="凭证日期" prop="voucherDate" :rules="[{ required: true, message: '请选择日期' }]">
              <el-date-picker v-model="formData.voucherDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">分录明细</el-divider>
      <el-button size="small" type="primary" @click="handleAddEntry" style="margin-bottom: 8px">添加分录</el-button>
      <div style="max-height: 350px; overflow-y: auto">
        <el-table :data="entries" border size="small">
          <el-table-column label="科目编码" width="130">
            <template #default="{ $index }">
              <el-input v-model="entries[$index].accountCode" size="small" placeholder="科目编码" />
            </template>
          </el-table-column>
          <el-table-column label="科目名称" width="130">
            <template #default="{ $index }">
              <el-input v-model="entries[$index].accountName" size="small" placeholder="科目名称" />
            </template>
          </el-table-column>
          <el-table-column label="摘要" min-width="150">
            <template #default="{ $index }">
              <el-input v-model="entries[$index].summary" size="small" placeholder="摘要" />
            </template>
          </el-table-column>
          <el-table-column label="借方金额" width="130">
            <template #default="{ $index }">
              <el-input-number v-model="entries[$index].debitAmount" :min="0" :precision="2" size="small" style="width: 100%"
                controls-position="right" @change="calcTotals" />
            </template>
          </el-table-column>
          <el-table-column label="贷方金额" width="130">
            <template #default="{ $index }">
              <el-input-number v-model="entries[$index].creditAmount" :min="0" :precision="2" size="small" style="width: 100%"
                controls-position="right" @change="calcTotals" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="60">
            <template #default="{ $index }">
              <el-button size="small" type="danger" @click="handleRemoveEntry($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div style="margin-top: 8px; display: flex; justify-content: flex-end; gap: 24px; font-weight: 600">
        <span>借方合计：<span style="color: #67c23a">{{ debitTotal.toFixed(2) }}</span></span>
        <span>贷方合计：<span style="color: #f56c6c">{{ creditTotal.toFixed(2) }}</span></span>
        <span :style="{ color: debitTotal === creditTotal ? '#67c23a' : '#f56c6c' }">
          {{ debitTotal === creditTotal ? '借贷平衡' : '借贷不平衡' }}
        </span>
      </div>

      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
