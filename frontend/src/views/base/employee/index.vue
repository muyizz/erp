<script setup lang="ts">
import { onMounted } from 'vue'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/base/employees', { params: p }), { employeeCode: '', name: '', deptId: undefined })

const { visible, loading: formLoading, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const url = d.isEdit ? `/base/employees/${d.id}` : '/base/employees'
    await request({ method: d.isEdit ? 'put' : 'post', url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadData()
  }, { employeeCode: '', name: '', deptId: undefined, position: '', phone: '', email: '', hireDate: '', remark: '', status: 1 })

function handleDelete(row: any) {
  ElMessageBox.confirm('确定删除该员工吗？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/base/employees/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.employeeCode" placeholder="员工编码" clearable style="width: 150px" />
      <el-input v-model="searchParams.employeeName" placeholder="员工姓名" clearable style="width: 150px" />
      <el-input v-model="searchParams.deptId" placeholder="部门ID" clearable style="width: 120px" />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    <div class="toolbar">
      <el-button type="primary" @click="open(false)">新增员工</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="employeeCode" label="员工编码" width="120" />
      <el-table-column prop="employeeName" label="姓名" width="100" />
      <el-table-column prop="deptName" label="部门" width="120" />
      <el-table-column prop="position" label="岗位" width="120" />
      <el-table-column prop="phone" label="电话" width="130" />
      <el-table-column prop="email" label="邮箱" width="160" />
      <el-table-column prop="hireDate" label="入职日期" width="110" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '在职' : '离职' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="open(true, row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page" v-model:page-size="pageSize" :total="total"
      :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next"
      @current-change="handlePageChange" @size-change="handleSizeChange"
      style="margin-top: 16px; justify-content: flex-end"
    />

    <el-dialog :title="isEdit ? '编辑员工' : '新增员工'" v-model="visible" width="520px" @close="close">
      <el-form ref="formRef" :model="formData" label-width="80px">
        <el-form-item label="员工编码" prop="employeeCode" :rules="[{ required: true, message: '请输入编码' }]">
          <el-input v-model="formData.employeeCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="employeeName" :rules="[{ required: true, message: '请输入姓名' }]">
          <el-input v-model="formData.employeeName" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-input-number v-model="formData.deptId" :min="0" style="width: 100%" placeholder="请输入部门ID" />
        </el-form-item>
        <el-form-item label="岗位" prop="position">
          <el-input v-model="formData.position" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>
        <el-form-item label="入职日期" prop="hireDate">
          <el-date-picker v-model="formData.hireDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" active-text="在职" inactive-text="离职" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="close">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
