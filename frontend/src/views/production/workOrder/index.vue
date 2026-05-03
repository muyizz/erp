<script setup lang="ts">
import { onMounted } from 'vue'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/production/work-orders', { params: p }), { productId: null, status: null })
const { visible, loading: fl, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    await request({ method: d.isEdit ? 'put' : 'post', url: d.isEdit ? `/production/work-orders/${d.id}` : '/production/work-orders', data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功'); loadData()
  }, { productId: null, bomId: null, quantity: 1, plannedStart: '', plannedEnd: '', workshop: '', status: 1 })
async function handleAction(id: number, action: string) {
  await request.post(`/production/work-orders/${id}/${action}`)
  ElMessage.success('操作成功'); loadData()
}
const statusMap: Record<number,string> = {1:'草稿',2:'已下达',3:'进行中',4:'已完成',5:'已关闭'}
onMounted(loadData)
</script>
<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.productId" placeholder="产品ID" clearable style="width:150px"/>
      <el-select v-model="searchParams.status" placeholder="状态" clearable style="width:120px"><el-option label="草稿" :value="1"/><el-option label="已下达" :value="2"/><el-option label="进行中" :value="3"/><el-option label="已完成" :value="4"/></el-select>
      <el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button>
    </div>
    <div class="toolbar"><el-button type="primary" @click="open(false)">新增工单</el-button></div>
    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70"/><el-table-column prop="orderNo" label="工单号"/><el-table-column prop="productId" label="产品ID" width="100"/><el-table-column prop="quantity" label="计划数量"/><el-table-column prop="completedQty" label="完成数量"/><el-table-column prop="workshop" label="车间"/><el-table-column prop="status" label="状态" width="90"><template #default="{row}">{{statusMap[row.status]}}</template></el-table-column>
      <el-table-column label="操作" width="320" fixed="right"><template #default="{row}">
        <el-button v-if="row.status===1" size="small" type="success" @click="handleAction(row.id,'release')">下达</el-button>
        <el-button v-if="row.status===2" size="small" type="primary" @click="handleAction(row.id,'start')">开工</el-button>
        <el-button v-if="row.status===3" size="small" type="warning" @click="handleAction(row.id,'complete')">完工</el-button>
        <el-button size="small" @click="open(true,row)">编辑</el-button>
      </template></el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @current-change="handlePageChange" @size-change="handleSizeChange" style="margin-top:16px;justify-content:flex-end"/>
    <el-dialog :title="isEdit?'编辑工单':'新增工单'" v-model="visible" width="500px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="产品ID" prop="productId" :rules="[{required:true}]"><el-input-number v-model="formData.productId"/></el-form-item>
        <el-form-item label="BOM ID" prop="bomId"><el-input-number v-model="formData.bomId"/></el-form-item>
        <el-form-item label="计划数量" prop="quantity"><el-input-number v-model="formData.quantity" :min="1"/></el-form-item>
        <el-form-item label="计划开始"><el-date-picker v-model="formData.plannedStart" type="date" value-format="YYYY-MM-DD"/></el-form-item>
        <el-form-item label="计划结束"><el-date-picker v-model="formData.plannedEnd" type="date" value-format="YYYY-MM-DD"/></el-form-item>
        <el-form-item label="车间"><el-input v-model="formData.workshop"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="close">取消</el-button><el-button type="primary" :loading="fl" @click="submit">确定</el-button></template>
    </el-dialog>
  </div>
</template>