<script setup lang="ts">
import { ref } from 'vue'
import request from '@/api/request'
const demandItems = ref([{ productId: null, quantity: 0 }])
const result = ref<any[]>([])
const loading = ref(false)
function addDemand() { demandItems.value.push({ productId: null, quantity: 0 }) }
function removeDemand(i: number) { demandItems.value.splice(i, 1) }
async function calculate() {
  loading.value = true
  try {
    const res = await request.post('/production/mrp', { demands: demandItems.value })
    result.value = res.data || []
  } finally { loading.value = false }
}
</script>
<template>
  <div><h3>MRP 物料需求计划</h3>
    <el-card>
      <template #header>需求输入</template>
      <el-table :data="demandItems" border><el-table-column label="产品ID"><template #default="{row,$index}"><el-input-number v-model="demandItems[$index].productId" size="small"/></template></el-table-column><el-table-column label="需求数量"><template #default="{row,$index}"><el-input-number v-model="demandItems[$index].quantity" :min="0" size="small"/></template></el-table-column><el-table-column label="操作" width="80"><template #default="{row,$index}"><el-button size="small" type="danger" @click="removeDemand($index)">删除</el-button></template></el-table-column></el-table>
      <el-button type="primary" style="margin-top:12px" @click="addDemand">添加需求</el-button>
      <el-button type="success" style="margin-top:12px;margin-left:8px" @click="calculate" :loading="loading">计算MRP</el-button>
    </el-card>
    <el-card style="margin-top:16px" v-if="result.length">
      <template #header>计算结果 - 物料需求</template>
      <el-table :data="result" border stripe><el-table-column prop="materialId" label="物料ID"/><el-table-column prop="materialName" label="物料名称"/><el-table-column prop="requiredQty" label="需求数量"/><el-table-column prop="currentStock" label="当前库存"/><el-table-column prop="shortage" label="短缺数量"/></el-table>
    </el-card>
  </div>
</template>