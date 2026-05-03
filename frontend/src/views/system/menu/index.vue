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
    const res = await request.get('/system/menus/tree')
    treeData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const { visible, loading: formLoading, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const url = d.isEdit ? `/system/menus/${d.id}` : '/system/menus'
    await request({ method: d.isEdit ? 'put' : 'post', url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadTree()
  }, { parentId: 0, name: '', type: 2, path: '', component: '', permissionCode: '', icon: '', sortOrder: 0, visible: 1 })

function handleAdd(parentId = 0) {
  open(false)
  formData.parentId = parentId
}

function handleEdit(row: any) {
  open(true, row)
}

function handleDelete(row: any) {
  ElMessageBox.confirm('删除菜单会同时删除子菜单，确定继续？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/system/menus/${row.id}`)
    ElMessage.success('删除成功')
    loadTree()
  })
}

function getTypeTag(type: number) {
  const map: Record<number, { text: string; type: string }> = {
    1: { text: '目录', type: 'primary' },
    2: { text: '菜单', type: 'success' },
    3: { text: '按钮', type: 'warning' }
  }
  return map[type] || { text: '未知', type: 'info' }
}

function renderTree(data: any[]) {
  return data.map(m => ({
    id: m.id,
    label: `${m.name} (${getTypeTag(m.type).text})`,
    children: m.children?.length ? renderTree(m.children) : undefined
  }))
}

onMounted(() => loadTree())
</script>

<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd(0)">新增根菜单</el-button>
    </div>

    <el-table :data="treeData" v-loading="loading" border stripe row-key="id" default-expand-all
      :tree-props="{ children: 'children' }">
      <el-table-column prop="name" label="名称" min-width="200" />
      <el-table-column prop="type" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="getTypeTag(row.type).type as any" size="small">{{ getTypeTag(row.type).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由" />
      <el-table-column prop="component" label="组件路径" />
      <el-table-column prop="permissionCode" label="权限编码" />
      <el-table-column prop="icon" label="图标" width="80" />
      <el-table-column prop="sortOrder" label="排序" width="70" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleAdd(row.id)">添加子项</el-button>
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="isEdit ? '编辑菜单' : '新增菜单'" v-model="visible" width="520px">
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="类型" prop="type" :rules="[{ required: true }]">
          <el-radio-group v-model="formData.type">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
            <el-radio :value="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="名称" prop="name" :rules="[{ required: true }]">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item v-if="formData.type !== 3" label="路由路径" prop="path">
          <el-input v-model="formData.path" />
        </el-form-item>
        <el-form-item v-if="formData.type === 2" label="组件路径" prop="component">
          <el-input v-model="formData.component" />
        </el-form-item>
        <el-form-item v-if="formData.type === 3" label="权限编码" prop="permissionCode">
          <el-input v-model="formData.permissionCode" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="formData.icon" placeholder="例如：Setting" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="close">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
