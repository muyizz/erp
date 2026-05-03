<script setup lang="ts">
import { onMounted } from 'vue'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/production/processes', { params: p }), { processName: '' })
const { visible, loading: fl, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    await request({ method: d.isEdit ? 'put' : 'post', url: d.isEdit ? `/production/processes/${d.id}` : '/production/processes', data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功'); loadData()
  }, { processName: '', productId: null, description: '' })
async function viewDetail(row: any) {
  const res = await request.get(`/production/processes/${row.id}`)
  const d = res.data?.header || res.data
  const { ElMessageBox } = await import('element-plus')
  ElMessageBox.alert(JSON.stringify(d.steps || d, null, 2), '工序详情', { dangerouslyUseHTMLString: true })
}
onMounted(loadData)
</script>
<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.processName" placeholder="工艺名称" clearable style="width:200px"/>
      <el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button>
    </div>
    <div class="toolbar"><el-button type="primary" @click="open(false)">新增工艺</el-button></div>
    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70"/><el-table-column prop="processCode" label="编码"/><el-table-column prop="processName" label="名称"/><el-table-column prop="productId" label="产品ID"/><el-table-column prop="description" label="描述" show-overflow-tooltip/>
      <el-table-column label="操作" width="200"><template #default="{row}"><el-button size="small" @click="viewDetail(row)">步骤</el-button><el-button size="small" @click="open(true,row)">编辑</el-button></template></el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @current-change="handlePageChange" @size-change="handleSizeChange" style="margin-top:16px;justify-content:flex-end"/>
    <el-dialog :title="isEdit?'编辑工艺':'新增工艺'" v-model="visible" width="500px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="工艺名称" prop="processName" :rules="[{required:true}]"><el-input v-model="formData.processName"/></el-form-item>
        <el-form-item label="产品ID"><el-input-number v-model="formData.productId"/></el-form-item>
        <el-form-item label="描述"><el-input v-model="formData.description" type="textarea"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="close">取消</el-button><el-button type="primary" :loading="fl" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>