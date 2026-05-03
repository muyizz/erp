<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const categoryTree = ref<any[]>([])
const treeLoading = ref(false)
const selectedCategory = ref<any>(null)

async function loadCategoryTree() {
  treeLoading.value = true
  try {
    const res = await request.get('/base/materials/categories/tree')
    categoryTree.value = res.data || []
  } finally {
    treeLoading.value = false
  }
}

function handleCategoryClick(node: any) {
  selectedCategory.value = node
  searchParams.categoryId = node.id
  handleSearch()
}

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/base/materials', { params: p }), { materialCode: '', name: '', categoryId: undefined })

const { visible, loading: formLoading, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const url = d.isEdit ? `/base/materials/${d.id}` : '/base/materials'
    await request({ method: d.isEdit ? 'put' : 'post', url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadData()
  }, { materialCode: '', name: '', categoryId: undefined, categoryName: '', spec: '', unit: '', unitPrice: undefined, minStock: undefined, maxStock: undefined, remark: '', status: 1 })

function handleAdd() {
  open(false)
  if (selectedCategory.value) {
    formData.categoryId = selectedCategory.value.id
  }
}

function handleDelete(row: any) {
  ElMessageBox.confirm('确定删除该物料吗？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/base/materials/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => {
  loadCategoryTree()
  loadData()
})
</script>

<template>
  <div class="material-layout">
    <div class="material-left">
      <el-card header="物料分类" shadow="never">
        <el-tree
          :data="categoryTree"
          node-key="id"
          :props="{ label: 'name', children: 'children' }"
          highlight-current
          default-expand-all
          @node-click="handleCategoryClick"
          v-loading="treeLoading"
        />
      </el-card>
    </div>
    <div class="material-right">
      <div class="search-bar">
        <el-input v-model="searchParams.materialCode" placeholder="物料编码" clearable style="width: 150px" />
        <el-input v-model="searchParams.materialName" placeholder="物料名称" clearable style="width: 150px" />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增物料</el-button>
      </div>

      <el-table :data="data" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="materialCode" label="物料编码" width="130" />
        <el-table-column prop="materialName" label="物料名称" min-width="140" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="spec" label="规格" width="120" />
        <el-table-column prop="unit" label="单位" width="70" />
        <el-table-column prop="unitPrice" label="单价" width="100" align="right" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
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
    </div>
  </div>

  <el-dialog :title="isEdit ? '编辑物料' : '新增物料'" v-model="visible" width="580px" @close="close">
    <el-form ref="formRef" :model="formData" label-width="80px">
      <el-form-item label="物料编码" prop="materialCode" :rules="[{ required: true, message: '请输入编码' }]">
        <el-input v-model="formData.materialCode" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="物料名称" prop="materialName" :rules="[{ required: true, message: '请输入名称' }]">
        <el-input v-model="formData.materialName" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId" :rules="[{ required: true, message: '请选择分类' }]">
        <el-tree-select
          v-model="formData.categoryId"
          :data="categoryTree"
          :props="{ label: 'name', children: 'children', value: 'id' }"
          check-strictly
          placeholder="请选择分类"
          filterable
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="规格" prop="spec">
        <el-input v-model="formData.spec" />
      </el-form-item>
      <el-form-item label="单位" prop="unit">
        <el-input v-model="formData.unit" />
      </el-form-item>
      <el-form-item label="单价" prop="unitPrice">
        <el-input-number v-model="formData.unitPrice" :min="0" :precision="2" style="width: 100%" />
      </el-form-item>
      <el-form-item label="最低库存" prop="minStock">
        <el-input-number v-model="formData.minStock" :min="0" style="width: 100%" />
      </el-form-item>
      <el-form-item label="最高库存" prop="maxStock">
        <el-input-number v-model="formData.maxStock" :min="0" style="width: 100%" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="formData.remark" type="textarea" :rows="2" />
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

<style scoped>
.material-layout { display: flex; gap: 16px; }
.material-left { width: 240px; flex-shrink: 0; }
.material-right { flex: 1; min-width: 0; }
</style>
