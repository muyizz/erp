<script setup lang="ts">
import { onMounted } from 'vue'
import { useTable } from '@/composables/useTable'
import { useForm } from '@/composables/useForm'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const { loading, data, total, page, pageSize, searchParams, loadData, handlePageChange, handleSizeChange, handleSearch, handleReset } =
  useTable((p: any) => request.get('/system/users', { params: p }), { username: '', status: null })

const { visible, loading: formLoading, isEdit, formRef, formData, open, close, submit } =
  useForm(async (d) => {
    const url = d.isEdit ? `/system/users/${d.id}` : '/system/users'
    const method = d.isEdit ? 'put' : 'post'
    await request({ method, url, data: d })
    ElMessage.success(d.isEdit ? '修改成功' : '新增成功')
    loadData()
  }, { username: '', realName: '', email: '', phone: '', password: '', status: 1 })

const roleVisible = ref(false)
const roleForm = reactive({ userId: 0, roleIds: [] as number[] })
const allRoles = ref<any[]>([])

async function handleRoleAssign(row: any) {
  roleForm.userId = row.id
  const res = await request.get(`/system/users/${row.id}/roles`)
  roleForm.roleIds = res.data || []
  const rr = await request.get('/system/roles', { params: { page: 1, pageSize: 999 } })
  allRoles.value = rr.data?.records || []
  roleVisible.value = true
}

async function handleRoleSubmit() {
  await request.put(`/system/users/${roleForm.userId}/roles`, { roleIds: roleForm.roleIds })
  ElMessage.success('角色分配成功')
  roleVisible.value = false
}

function handleDelete(row: any) {
  ElMessageBox.confirm('确定删除该用户吗？', '提示', { type: 'warning' }).then(async () => {
    await request.delete(`/system/users/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => loadData())
</script>

<template>
  <div>
    <div class="search-bar">
      <el-input v-model="searchParams.username" placeholder="用户名" clearable style="width: 180px" />
      <el-select v-model="searchParams.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="启用" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="handleSearch">查询</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>
    <div class="toolbar">
      <el-button type="primary" @click="open(false)">新增用户</el-button>
    </div>

    <el-table :data="data" v-loading="loading" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="realName" label="姓名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="open(true, row)">编辑</el-button>
          <el-button size="small" type="warning" @click="handleRoleAssign(row)">分配角色</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
      style="margin-top: 16px; justify-content: flex-end"
    />

    <!-- User Form Dialog -->
    <el-dialog :title="isEdit ? '编辑用户' : '新增用户'" v-model="visible" width="500px" @close="close">
      <el-form ref="formRef" :model="formData" label-width="80px">
        <el-form-item label="用户名" prop="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <el-input v-model="formData.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" />
        </el-form-item>
        <el-form-item label="密码" prop="password" :rules="isEdit ? [] : [{ required: true, message: '请输入密码' }]">
          <el-input v-model="formData.password" type="password" :placeholder="isEdit ? '留空不修改' : '请输入密码'" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="close">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <!-- Role Assignment Dialog -->
    <el-dialog title="分配角色" v-model="roleVisible" width="400px">
      <el-checkbox-group v-model="roleForm.roleIds">
        <el-checkbox v-for="r in allRoles" :key="r.id" :label="r.id" :value="r.id">{{ r.roleName }}</el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRoleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
