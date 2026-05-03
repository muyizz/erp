<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

function handleLogout() {
  userStore.logout()
  router.replace('/login')
}
</script>

<template>
  <div class="navbar">
    <div class="navbar-left">
      <el-button
        text
        @click="appStore.toggleSidebar"
        style="font-size: 18px; width: 36px; height: 36px"
      >
        <span v-if="appStore.sidebarCollapsed">&#9776;</span>
        <span v-else>&#10005;</span>
      </el-button>
      <el-breadcrumb separator="/" style="margin-left: 12px">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="navbar-right">
      <el-dropdown trigger="click">
        <span class="user-info">
          {{ userStore.realName || userStore.username }}
          <el-icon style="margin-left: 4px"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped>
.navbar {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.navbar-left {
  display: flex;
  align-items: center;
}
.navbar-right {
  display: flex;
  align-items: center;
}
.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  color: #303133;
}
</style>
