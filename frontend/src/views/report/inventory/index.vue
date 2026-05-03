<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/api/request'
const stock = ref<any[]>([]); const turnover = ref<any[]>([])
async function load() {
  stock.value = (await request.get('/reports/inventory/snapshot')).data || []
  turnover.value = (await request.get('/reports/inventory/turnover')).data || []
}
onMounted(load)
</script>
<template>
  <div><h3>库存报表</h3>
    <el-tabs>
      <el-tab-pane label="库存快照">
        <el-table :data="stock" border stripe><el-table-column prop="material_name" label="物料"/><el-table-column prop="warehouse_name" label="仓库"/><el-table-column prop="quantity" label="库存数量"/></el-table>
      </el-tab-pane>
      <el-tab-pane label="库存周转">
        <el-table :data="turnover" border stripe><el-table-column prop="material_name" label="物料"/><el-table-column prop="current_stock" label="当前库存"/><el-table-column prop="safety_stock" label="安全库存"/></el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>