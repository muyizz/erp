<script setup lang="ts">
import { onMounted } from 'vue'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/base/suppliers', { params: p }), { supplierCode: '', supplierName: '', status: null })

const { visible, loading: formLoading, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const url = d.isEdit ? `/base/suppliers/${d.id}` : '/base/suppliers'
    await request({ method: d.isEdit ? 'put' : 'post', url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadData()
  }, { supplierCode: '', supplierName: '', contactPerson: '', contactPhone: '', contactEmail: '', address: '', bankName: '', bankAccount: '', taxId: '', remark: '', status: 1 })

function handleDelete(row: any) {
  ElMessageBox.confirm('确定删除该供应商吗？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/base/suppliers/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.supplierCode" placeholder="供应商编码" clearable style="width: 160px" />
      <el-input v-model="searchParams.supplierName" placeholder="供应商名称" clearable style="width: 160px" />
      <el-select v-model="searchParams.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    <div class="toolbar">
      <el-button type="primary" @click="open(false)">新增供应商</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="supplierCode" label="供应商编码" width="130" />
      <el-table-column prop="supplierName" label="供应商名称" min-width="160" />
      <el-table-column prop="contactPerson" label="联系人" width="100" />
      <el-table-column prop="contactPhone" label="电话" width="130" />
      <el-table-column prop="contactEmail" label="邮箱" width="160" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
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

    <el-dialog :title="isEdit ? '编辑供应商' : '新增供应商'" v-model="visible" width="580px" @close="close">
      <el-form ref="formRef" :model="formData" label-width="90px">
        <el-form-item label="供应商编码" prop="supplierCode" :rules="[{ required: true, message: '请输入编码' }]">
          <el-input v-model="formData.supplierCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName" :rules="[{ required: true, message: '请输入名称' }]">
          <el-input v-model="formData.supplierName" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="formData.contactPerson" />
        </el-form-item>
        <el-form-item label="电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="contactEmail">
          <el-input v-model="formData.contactEmail" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="close">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
