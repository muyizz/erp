<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('type')

// --- Dict Type ---
const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/system/dicts/types', { params: p }), { dictName: '' })

const { visible, loading: formLoading, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const url = d.isEdit ? `/system/dicts/types/${d.id}` : '/system/dicts/types'
    await request({ method: d.isEdit ? 'put' : 'post', url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadData()
  }, { dictName: '', dictCode: '', status: 1 })

function handleDelete(row: any) {
  ElMessageBox.confirm('删除字典类型会同时删除其所有字典项，确定继续？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/system/dicts/types/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
    if (selectedType.value?.id === row.id) {
      selectedType.value = null
    }
  })
}

// --- Dict Items ---
const selectedType = ref<any>(null)
const itemsLoading = ref(false)
const itemsData = ref<any[]>([])

const itemsTotal = ref(0)
const itemsPage = ref(1)
const itemsPageSize = ref(20)

const { visible: itemVisible, loading: itemFormLoading, isEdit: itemIsEdit, formRef: itemFormRef, formData: itemFormData, open: openItem, close: closeItem, submit: submitItem } =
  useForm(async (d) => {
    const url = d.isEdit
      ? `/system/dicts/types/${d.dictTypeId}/items/${d.id}`
      : `/system/dicts/types/${selectedType.value.id}/items`
    await request({ method: d.isEdit ? 'put' : 'post', url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadItems()
  }, { dictTypeId: 0, label: '', value: '', sortOrder: 0, status: 1 })

async function selectType(row: any) {
  selectedType.value = row
  activeTab.value = 'item'
  itemsPage.value = 1
  await loadItems()
}

async function loadItems() {
  if (!selectedType.value) return
  itemsLoading.value = true
  try {
    const res = await request.get(`/system/dicts/types/${selectedType.value.id}/items`, {
      params: { page: itemsPage.value, pageSize: itemsPageSize.value }
    })
    itemsData.value = res.data?.records || []
    itemsTotal.value = res.data?.total || 0
  } finally {
    itemsLoading.value = false
  }
}

function handleAddItem() {
  openItem(false)
  itemFormData.dictTypeId = selectedType.value.id
}

function handleEditItem(row: any) {
  openItem(true, row)
}

function handleDeleteItem(row: any) {
  ElMessageBox.confirm('确定删除该字典项吗？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/system/dicts/types/${selectedType.value.id}/items/${row.id}`)
    ElMessage.success('删除成功')
    loadItems()
  })
}

function handleItemsPageChange(p: number) {
  itemsPage.value = p
  loadItems()
}

function handleItemsSizeChange(s: number) {
  itemsPageSize.value = s
  itemsPage.value = 1
  loadItems()
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="字典类型" name="type" />
      <el-tab-pane label="字典项" name="item" :disabled="!selectedType" />
    </el-tabs>

    <!-- Dict Type Tab -->
    <template v-if="activeTab === 'type'">
      <div class="search-bar">
        <el-input v-model="searchParams.dictName" placeholder="字典名称" clearable style="width: 180px" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <div class="toolbar">
        <el-button type="primary" @click="open(false)">新增字典类型</el-button>
      </div>

      <el-table :data="data" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="dictName" label="字典名称" />
        <el-table-column prop="dictCode" label="字典编码" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="selectType(row)">字典项</el-button>
            <el-button size="small" @click="open(true, row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page" v-model:page-size="pageSize" :total="total"
        :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next"
        @current-change="handlePageChange" @size-change="handleSizeChange"
        style="margin-top: 16px; justify-content: flex-end"
      />

      <el-dialog :title="isEdit ? '编辑字典类型' : '新增字典类型'" v-model="visible" width="500px" @close="close">
        <el-form ref="formRef" :model="formData" label-width="80px">
          <el-form-item label="字典名称" prop="dictName" :rules="[{ required: true, message: '请输入字典名称' }]">
            <el-input v-model="formData.dictName" />
          </el-form-item>
          <el-form-item label="字典编码" prop="dictCode" :rules="[{ required: true, message: '请输入字典编码' }]">
            <el-input v-model="formData.dictCode" :disabled="isEdit" />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="close">取消</el-button>
          <el-button type="primary" :loading="formLoading" @click="submit">确定</el-button>
        </template>
      </el-dialog>
    </template>

    <!-- Dict Items Tab -->
    <template v-if="activeTab === 'item' && selectedType">
      <div style="margin-bottom: 12px; font-size: 14px; color: #606266">
        当前字典类型：<el-tag type="primary" disable-transitions>{{ selectedType.dictName }}</el-tag>
        <el-button size="small" style="margin-left: 12px" @click="activeTab = 'type'">返回类型列表</el-button>
      </div>
      <div class="toolbar">
        <el-button type="primary" @click="handleAddItem">新增字典项</el-button>
      </div>

      <el-table :data="itemsData" v-loading="itemsLoading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="label" label="标签" />
        <el-table-column prop="value" label="值" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleEditItem(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="itemsPage" v-model:page-size="itemsPageSize" :total="itemsTotal"
        :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next"
        @current-change="handleItemsPageChange" @size-change="handleItemsSizeChange"
        style="margin-top: 16px; justify-content: flex-end"
      />

      <el-dialog :title="itemIsEdit ? '编辑字典项' : '新增字典项'" v-model="itemVisible" width="500px" @close="closeItem">
        <el-form ref="itemFormRef" :model="itemFormData" label-width="80px">
          <el-form-item label="标签" prop="label" :rules="[{ required: true, message: '请输入标签' }]">
            <el-input v-model="itemFormData.label" />
          </el-form-item>
          <el-form-item label="值" prop="value" :rules="[{ required: true, message: '请输入值' }]">
            <el-input v-model="itemFormData.value" />
          </el-form-item>
          <el-form-item label="排序" prop="sortOrder">
            <el-input-number v-model="itemFormData.sortOrder" :min="0" />
          </el-form-item>
          <el-form-item label="状态">
            <el-switch v-model="itemFormData.status" :active-value="1" :inactive-value="0" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="closeItem">取消</el-button>
          <el-button type="primary" :loading="itemFormLoading" @click="submitItem">确定</el-button>
        </template>
      </el-dialog>
    </template>
  </div>
</template>
