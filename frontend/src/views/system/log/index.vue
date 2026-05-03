<script setup lang="ts">
import { onMounted } from 'vue'
import { useTable } from '@/composables/useTable'
import request from '@/api/request'
import { ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/system/logs', { params: p }), { username: '', module: '', dateRange: [] as string[] })

function handleViewDetail(row: any) {
  ElMessageBox.alert(
    `<div style="max-height:400px;overflow:auto">
      <p><strong>请求方式：</strong>${row.requestMethod}</p>
      <p><strong>请求URL：</strong>${row.requestUrl}</p>
      <p><strong>IP地址：</strong>${row.ip || '-'}</p>
      <p><strong>耗时：</strong>${row.costTime ? row.costTime + 'ms' : '-'}</p>
      <p><strong>请求参数：</strong></p>
      <pre>${JSON.stringify(row.requestParams || {}, null, 2)}</pre>
      <p><strong>返回结果：</strong></p>
      <pre>${JSON.stringify(row.responseResult || {}, null, 2)}</pre>
    </div>`,
    '操作日志详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '关闭' }
  )
}

function getStatusTag(status: number) {
  if (status === 1) return { text: '成功', type: 'success' as const }
  return { text: '失败', type: 'danger' as const }
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.username" placeholder="操作人" clearable style="width: 150px" />
      <el-input v-model="searchParams.module" placeholder="模块" clearable style="width: 150px" />
      <el-date-picker
        v-model="searchParams.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 260px"
      />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="操作人" width="100" />
      <el-table-column prop="module" label="模块" width="120" />
      <el-table-column prop="operation" label="操作" min-width="150" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP地址" width="140" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status).type">{{ getStatusTag(row.status).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="操作时间" width="170" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleViewDetail(row)">详情</el-button>
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
