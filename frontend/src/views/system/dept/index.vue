<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const treeData = ref<any[]>([])
const loading = ref(false)

async function loadTree() {
  loading.value = true
  try {
    const res = await request.get('/system/depts/tree')
    treeData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const { visible, loading: formLoading, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const url = d.isEdit ? `/system/depts/${d.id}` : '/system/depts'
    await request({ method: d.isEdit ? 'put' : 'post', url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadTree()
  }, { parentId: 0, name: '', leader: '', phone: '', email: '', sortOrder: 0, status: 1 })

function handleAdd(parentId = 0) {
  open(false)
  formData.parentId = parentId
}

function handleEdit(row: any) {
  open(true, row)
}

function handleDelete(row: any) {
  ElMessageBox.confirm('删除部门会同时删除子部门，确定继续？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/system/depts/${row.id}`)
    ElMessage.success('删除成功')
    loadTree()
  })
}

onMounted(() => loadTree())
</script>

<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd(0)">新增根部门</el-button>
    </div>

    <el-table :data="treeData" v-loading="loading" border stripe row-key="id" default-expand-all
      :tree-props="{ children: 'children' }">
      <el-table-column prop="name" label="部门名称" min-width="200" />
      <el-table-column prop="leader" label="负责人" width="120" />
      <el-table-column prop="phone" label="联系电话" width="140" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="sortOrder" label="排序" width="70" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleAdd(row.id)">添加子部门</el-button>
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="isEdit ? '编辑部门' : '新增部门'" v-model="visible" width="500px" @close="close">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="部门名称" prop="name" :rules="[{ required: true, message: '请输入部门名称' }]">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="formData.leader" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" />
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
  </div>
</template>
