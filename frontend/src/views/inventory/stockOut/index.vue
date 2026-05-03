<script setup lang="ts">
import { onMounted } from 'vue'
import { useTable } from '@/composables/useTable'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/inventory/stock-outs', { params: p }), { warehouseId: null, docType: null })

const docTypeMap: Record<number, string> = { 1: '销售出库', 2: '采购退货', 3: '盘亏出库', 4: '生产领料', 5: '调拨出库' }

function handleConfirm(row: any) {
  ElMessageBox.confirm('确认后库存将减少，确定继续？', '提示', { type: 'warning' }).then(async () => {
    await request.post(`/inventory/stock-outs/${row.id}/confirm`)
    ElMessage.success('确认成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-select v-model="searchParams.warehouseId" placeholder="仓库" clearable style="width: 140px">
        <el-option label="原材料仓" :value="1" />
        <el-option label="成品仓" :value="2" />
        <el-option label="辅料仓" :value="3" />
      </el-select>
      <el-select v-model="searchParams.docType" placeholder="单据类型" clearable style="width: 130px">
        <el-option v-for="(v,k) in docTypeMap" :key="k" :label="v" :value="Number(k)" />
      </el-select>
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="docNo" label="单据号" width="160" />
      <el-table-column prop="docType" label="单据类型" width="100">
        <template #default="{ row }">{{ docTypeMap[row.docType] || row.docType }}</template>
      </el-table-column>
      <el-table-column prop="warehouseId" label="仓库ID" width="80" />
      <el-table-column prop="outDate" label="出库日期" width="110" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="warning" @click="handleConfirm(row)">确认出库</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page" v-model:page-size="pageSize" :total="total"
      :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next"
      @current-change="handlePageChange" @size-change="handleSizeChange"
      style="margin-top: 16px; justify-content: flex-end"
    />
  </div>
</template>
