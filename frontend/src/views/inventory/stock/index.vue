<script setup lang="ts">
import { onMounted } from 'vue'
import { useTable } from '@/composables/useTable'
import request from '@/api/request'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/inventory/stocks', { params: p }), { materialCode: '', materialName: '', warehouseId: undefined })

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.materialCode" placeholder="物料编码" clearable style="width: 150px" />
      <el-input v-model="searchParams.materialName" placeholder="物料名称" clearable style="width: 150px" />
      <el-input v-model="searchParams.warehouseId" placeholder="仓库ID" clearable style="width: 120px" />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="materialCode" label="物料编码" width="130" />
      <el-table-column prop="materialName" label="物料名称" min-width="140" />
      <el-table-column prop="spec" label="规格" width="120" />
      <el-table-column prop="unit" label="单位" width="70" />
      <el-table-column prop="warehouseName" label="仓库" width="120" />
      <el-table-column prop="quantity" label="库存数量" width="100" align="right" />
      <el-table-column prop="availableQty" label="可用数量" width="100" align="right" />
      <el-table-column prop="lockedQty" label="锁定数量" width="100" align="right" />
      <el-table-column prop="minStock" label="最低库存" width="100" align="right" />
      <el-table-column prop="maxStock" label="最高库存" width="100" align="right" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.quantity <= row.minStock" type="danger">低库存</el-tag>
          <el-tag v-else-if="row.quantity >= row.maxStock" type="warning">超储</el-tag>
          <el-tag v-else type="success">正常</el-tag>
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
