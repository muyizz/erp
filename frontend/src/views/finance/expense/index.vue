<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/finance/expenses', { params: p }), { docNo: '', employeeId: undefined, status: null, dateRange: [] as string[] })

const { visible, loading: formLoading, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const url = d.isEdit ? `/finance/expenses/${d.id}` : '/finance/expenses'
    await request({ method: d.isEdit ? 'put' : 'post', url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadData()
  }, { employeeId: undefined, expenseDate: '', expenseType: '', amount: undefined, description: '', remark: '' })

function getStatusTag(status: string) {
  const map: Record<string, { text: string; type: string }> = {
    draft: { text: '草稿', type: 'info' },
    submitted: { text: '已提交', type: 'warning' },
    approved: { text: '已审批', type: 'primary' },
    reimbursed: { text: '已报销', type: 'success' },
    rejected: { text: '已驳回', type: 'danger' },
    cancelled: { text: '已取消', type: 'danger' }
  }
  return map[status] || { text: status, type: 'info' }
}

function handleSubmitForApproval(row: any) {
  ElMessageBox.confirm('确定提交审批吗？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/finance/expenses/${row.id}/submit`)
    ElMessage.success('提交成功')
    loadData()
  })
}

function handleApprove(row: any) {
  ElMessageBox.confirm('确定审批通过该报销申请吗？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/finance/expenses/${row.id}/approve`)
    ElMessage.success('审批通过')
    loadData()
  })
}

function handleReimburse(row: any) {
  ElMessageBox.confirm('确定标记为已报销吗？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/finance/expenses/${row.id}/reimburse`)
    ElMessage.success('报销完成')
    loadData()
  })
}

function handleCancel(row: any) {
  ElMessageBox.confirm('确定取消该报销申请吗？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/finance/expenses/${row.id}/cancel`)
    ElMessage.success('已取消')
    loadData()
  })
}

function handleDelete(row: any) {
  ElMessageBox.confirm('确定删除该报销申请吗？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/finance/expenses/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.docNo" placeholder="单据号" clearable style="width: 160px" />
      <el-input v-model="searchParams.employeeId" placeholder="员工ID" clearable style="width: 130px" />
      <el-select v-model="searchParams.status" placeholder="状态" clearable style="width: 130px">
        <el-option label="草稿" value="draft" />
        <el-option label="已提交" value="submitted" />
        <el-option label="已审批" value="approved" />
        <el-option label="已报销" value="reimbursed" />
        <el-option label="已驳回" value="rejected" />
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
      <el-button type="primary" @click="open(false)">新增报销</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="docNo" label="单据号" width="150" />
      <el-table-column prop="employeeName" label="申请人" width="100" />
      <el-table-column prop="expenseType" label="费用类型" width="100" />
      <el-table-column prop="amount" label="金额" width="110" align="right" />
      <el-table-column prop="description" label="费用描述" min-width="160" show-overflow-tooltip />
      <el-table-column prop="expenseDate" label="发生日期" width="110" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status).type as any">{{ getStatusTag(row.status).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" size="small" type="warning" @click="handleSubmitForApproval(row)">提交</el-button>
          <el-button v-if="row.status === 'submitted'" size="small" type="primary" @click="handleApprove(row)">审批</el-button>
          <el-button v-if="row.status === 2" size="small" type="success" @click="handleReimburse(row)">报销</el-button>
          <el-button v-if="row.status === 1" size="small" @click="open(true, row)">编辑</el-button>
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

    <el-dialog :title="isEdit ? '编辑报销' : '新增报销'" v-model="visible" width="520px" @close="close">
      <el-form ref="formRef" :model="formData" label-width="80px">
        <el-form-item label="申请人" prop="employeeId" :rules="[{ required: true, message: '请输入员工ID' }]">
          <el-input-number v-model="formData.employeeId" :min="1" placeholder="员工ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="费用类型" prop="expenseType" :rules="[{ required: true, message: '请选择费用类型' }]">
          <el-select v-model="formData.expenseType" placeholder="请选择" style="width: 100%">
            <el-option label="差旅费" value="travel" />
            <el-option label="办公费" value="office" />
            <el-option label="交通费" value="transport" />
            <el-option label="招待费" value="entertainment" />
            <el-option label="通讯费" value="communication" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount" :rules="[{ required: true, message: '请输入金额' }]">
          <el-input-number v-model="formData.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="发生日期" prop="expenseDate" :rules="[{ required: true, message: '请选择日期' }]">
          <el-date-picker v-model="formData.expenseDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="费用描述" prop="description" :rules="[{ required: true, message: '请输入描述' }]">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="close">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
