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
    const res = await request.get('/finance/accounts/tree')
    treeData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const { visible, loading: formLoading, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const url = d.isEdit ? `/finance/accounts/${d.id}` : '/finance/accounts'
    await request({ method: d.isEdit ? 'put' : 'post', url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadTree()
  }, { parentId: 0, code: '', name: '', accountType: 1, balanceDirection: 1, status: 1 })

function handleAdd(parentId = 0) {
  open(false)
  formData.parentId = parentId
}

function handleEdit(row: any) {
  open(true, row)
}

function handleDelete(row: any) {
  ElMessageBox.confirm('删除科目会同时删除下级科目，确定继续？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/finance/accounts/${row.id}`)
    ElMessage.success('删除成功')
    loadTree()
  })
}

function getTypeTag(type: number) {
  const map: Record<number, { text: string; type: string }> = {
    1: { text: '资产', type: 'primary' },
    2: { text: '负债', type: 'warning' },
    3: { text: '权益', type: 'success' },
    4: { text: '收入', type: 'info' },
    5: { text: '费用', type: 'danger' }
  }
  return map[type] || { text: '其他', type: 'info' }
}

onMounted(() => loadTree())
</script>

<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd(0)">新增一级科目</el-button>
    </div>

    <el-table :data="treeData" v-loading="loading" border stripe row-key="id" default-expand-all
      :tree-props="{ children: 'children' }">
      <el-table-column prop="code" label="科目编码" width="140" />
      <el-table-column prop="name" label="科目名称" min-width="200" />
      <el-table-column prop="accountType" label="科目类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getTypeTag(row.accountType).type as any" size="small">{{ getTypeTag(row.accountType).text }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="balanceDirection" label="余额方向" width="90">
        <template #default="{ row }">
          <el-tag :type="row.balanceDirection === 1 ? 'success' : 'danger'" size="small">
            {{ row.balanceDirection === 1 ? '借方' : '贷方' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleAdd(row.id)">添加下级</el-button>
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="isEdit ? '编辑科目' : '新增科目'" v-model="visible" width="500px" @close="close">
      <el-form ref="formRef" :model="formData" label-width="80px">
        <el-form-item label="科目编码" prop="code" :rules="[{ required: true, message: '请输入编码' }]">
          <el-input v-model="formData.code" />
        </el-form-item>
        <el-form-item label="科目名称" prop="name" :rules="[{ required: true, message: '请输入名称' }]">
          <el-input v-model="formData.name" />
        </el-form-item>
        <el-form-item label="科目类型" prop="accountType" :rules="[{ required: true }]">
          <el-select v-model="formData.accountType" style="width: 100%">
            <el-option label="资产" :value="1" />
            <el-option label="负债" :value="2" />
            <el-option label="权益" :value="3" />
            <el-option label="收入" :value="4" />
            <el-option label="费用" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="余额方向" prop="balanceDirection">
          <el-radio-group v-model="formData.balanceDirection">
            <el-radio :value="1">借方</el-radio>
            <el-radio :value="0">贷方</el-radio>
          </el-radio-group>
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
