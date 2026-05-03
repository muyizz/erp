<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/system/roles', { params: p }), { roleName: '' })

const { visible, loading: formLoading, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const url = d.isEdit ? `/system/roles/${d.id}` : '/system/roles'
    await request({ method: d.isEdit ? 'put' : 'post', url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadData()
  }, { roleName: '', roleCode: '', description: '', sortOrder: 0, status: 1 })

const menuVisible = ref(false)
const menuForm = reactive({ roleId: 0, menuIds: [] as number[] })
const menuTree = ref<any[]>([])

async function handleMenuAssign(row: any) {
  menuForm.roleId = row.id
  const res = await request.get(`/system/roles/${row.id}/menus`)
  menuForm.menuIds = res.data || []
  const mr = await request.get('/system/menus/tree')
  menuTree.value = mr.data || []
  menuVisible.value = true
}

async function handleMenuSubmit() {
  await request.put(`/system/roles/${menuForm.roleId}/menus`, { menuIds: menuForm.menuIds })
  ElMessage.success('菜单分配成功')
  menuVisible.value = false
}

function handleDelete(row: any) {
  ElMessageBox.confirm('确定删除该角色吗？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/system/roles/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.roleName" placeholder="角色名称" clearable style="width: 180px" />
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    <div class="toolbar">
      <el-button type="primary" @click="open(false)">新增角色</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roleName" label="角色名称" />
      <el-table-column prop="roleCode" label="角色编码" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="open(true, row)">编辑</el-button>
          <el-button size="small" type="warning" @click="handleMenuAssign(row)">分配菜单</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page" v-model:page-size="pageSize" :total="total"
      :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
      @current-change="handlePageChange" @size-change="handleSizeChange"
      style="margin-top: 16px; justify-content: flex-end"
    />

    <el-dialog :title="isEdit ? '编辑角色' : '新增角色'" v-model="visible" width="500px">
      <el-form ref="formRef" :model="formData" label-width="80px">
        <el-form-item label="角色名称" prop="roleName" :rules="[{ required: true }]">
          <el-input v-model="formData.roleName" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode" :rules="[{ required: true }]">
          <el-input v-model="formData.roleCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" />
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

    <el-dialog title="分配菜单" v-model="menuVisible" width="400px">
      <el-tree
        :data="menuTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="menuForm.menuIds"
        ref="menuTreeRef"
        default-expand-all
      />
      <template #footer>
        <el-button @click="menuVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMenuSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
