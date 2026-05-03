<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'
const balance = ref<any[]>([]); const ar = ref<any[]>([]); const ap = ref<any[]>([])
async function load() {
  balance.value = (await request.get('/reports/finance/trial-balance')).data || []
  ar.value = (await request.get('/reports/finance/ar-aging')).data || []
  ap.value = (await request.get('/reports/finance/ap-aging')).data || []
}
onMounted(load)
</script>
<template>
  <div><h3>财务报表</h3>
    <el-tabs>
      <el-tab-pane label="试算平衡">
        <el-table :data="balance" border stripe><el-table-column prop="account_code" label="科目编码"/><el-table-column prop="account_name" label="科目名称"/><el-table-column prop="balance" label="余额"/></el-table>
      </el-tab-pane>
      <el-tab-pane label="应收账龄">
        <el-table :data="ar" border stripe><el-table-column prop="customer_name" label="客户"/><el-table-column prop="amount" label="金额"/><el-table-column prop="due_date" label="到期日"/></el-table>
      </el-tab-pane>
      <el-tab-pane label="应付账龄">
        <el-table :data="ap" border stripe><el-table-column prop="supplier_name" label="供应商"/><el-table-column prop="amount" label="金额"/><el-table-column prop="due_date" label="到期日"/></el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>