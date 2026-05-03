<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'
const data = ref<any[]>([])
const sDate = ref('2026-04-01'); const eDate = ref('2026-05-31')
async function load() {
  const res = await request.get('/reports/purchase/summary', { params: { startDate: sDate.value, endDate: eDate.value } })
  data.value = res.data || []
}
onMounted(load)
</script>
<template>
  <div><h3>采购报表</h3>
    <div class="search-bar">
      <el-date-picker v-model="sDate" type="date" value-format="YYYY-MM-DD" /> -
      <el-date-picker v-model="eDate" type="date" value-format="YYYY-MM-DD" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="data" border stripe>
      <el-table-column prop="supplier_name" label="供应商" />
      <el-table-column prop="order_count" label="订单数" />
      <el-table-column prop="total_amount" label="采购总额" />
    </el-table>
  </div>
</template>