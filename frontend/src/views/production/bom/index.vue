<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/production/boms', { params: p }), { productId: null, status: null })
const { visible, loading: fl, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const items = (d as any).items || []
    await request({ method: d.isEdit ? 'put' : 'post', url: d.isEdit ? `/production/boms/${d.id}` : '/production/boms', data: { header: d, items } })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功'); loadData()
  }, { productId: null, version: 'V1.0', quantity: 1, status: 1, items: [] as any[] })
const itemList = ref<any[]>([])
function addItem() { itemList.value.push({ materialId: null, quantity: 1, unit: '个', scrapRate: 0, sortOrder: itemList.value.length + 1 }) }
function handleOpen(edit = false, row?: any) {
  itemList.value = row?.items ? [...row.items] : []; open(edit, row)
}
async function viewDetail(row: any) {
  const res = await request.get(`/production/boms/${row.id}`)
  const d = res.data?.header || res.data
  const { ElMessageBox } = await import('element-plus')
  ElMessageBox.alert(JSON.stringify(d, null, 2), 'BOM详情', { dangerouslyUseHTMLString: true })
}
function handleDelete(row: any) {
  const { ElMessageBox } = require('element-plus')
  ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' }).then(async () => { await request.delete(`/production/boms/${row.id}`); ElMessage.success('删除成功'); loadData() })
}
async function doSubmit() {
  formData.items = itemList.value
  await submit()
}
onMounted(loadData)
</script>
<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.productId" placeholder="产品ID" clearable style="width:150px"/>
      <el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button>
    </div>
    <div class="toolbar"><el-button type="primary" @click="handleOpen(false)">新增BOM</el-button></div>
    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70"/><el-table-column prop="bomCode" label="BOM编码"/><el-table-column prop="productId" label="产品ID"/><el-table-column prop="version" label="版本" width="80"/><el-table-column prop="quantity" label="基准数量"/><el-table-column prop="status" label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'">{{row.status===1?'启用':'禁用'}}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="260"><template #default="{row}"><el-button size="small" @click="viewDetail(row)">详情</el-button><el-button size="small" @click="handleOpen(true,row)">编辑</el-button><el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button></template></el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @current-change="handlePageChange" @size-change="handleSizeChange" style="margin-top:16px;justify-content:flex-end"/>
    <el-dialog :title="isEdit?'编辑BOM':'新增BOM'" v-model="visible" width="700px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-row :gutter="20"><el-col :span="12"><el-form-item label="产品ID" prop="productId" :rules="[{required:true}]"><el-input-number v-model="formData.productId"/></el-form-item></el-col><el-col :span="12"><el-form-item label="版本" prop="version"><el-input v-model="formData.version"/></el-form-item></el-col></el-row>
        <el-row :gutter="20"><el-col :span="12"><el-form-item label="基准数量"><el-input-number v-model="formData.quantity"/></el-form-item></el-col><el-col :span="12"><el-form-item label="状态"><el-switch v-model="formData.status" :active-value="1" :inactive-value="0"/></el-form-item></el-col></el-row>
        <h4>物料清单</h4><el-button size="small" type="primary" @click="addItem">添加物料</el-button>
        <el-table :data="itemList" border style="margin-top:8px">
          <el-table-column label="物料ID" width="120"><template #default="{row,$index}"><el-input-number v-model="itemList[$index].materialId" size="small"/></template></el-table-column>
          <el-table-column label="数量" width="120"><template #default="{row,$index}"><el-input-number v-model="itemList[$index].quantity" size="small"/></template></el-table-column>
          <el-table-column label="单位" width="100"><template #default="{row,$index}"><el-input v-model="itemList[$index].unit" size="small"/></template></el-table-column>
          <el-table-column label="损耗率%" width="100"><template #default="{row,$index}"><el-input-number v-model="itemList[$index].scrapRate" :min="0" :max="100" size="small"/></template></el-table-column>
          <el-table-column label="操作" width="80"><template #default="{row,$index}"><el-button size="small" type="danger" @click="itemList.splice($index,1)">删除</el-button></template></el-table-column>
        </el-table>
      </el-form>
      <template #footer><el-button @click="close">取消</el-button><el-button type="primary" :loading="fl" @click="doSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>