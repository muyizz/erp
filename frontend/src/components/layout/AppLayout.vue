<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { useRouter, useRoute } from 'vue-router'
import { computed, onMounted } from 'vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()

onMounted(async () => {
  if (localStorage.getItem('accessToken') && (!userStore.menus || userStore.menus.length === 0)) {
    try { await userStore.loadUserInfo() } catch (e) { /* ignore */ }
  }
})

const isCollapsed = computed(() => appStore.sidebarCollapsed)

function handleMenuSelect(index: string) {
  if (index && index.startsWith('/')) {
    router.push(index)
  }
}

const activeMenu = computed(() => route.path)
</script>

<template>
  <el-container class="app-container">
    <div class="sidebar-wrapper" :class="{ collapsed: isCollapsed }">
      <Sidebar
        :menus="userStore.menus"
        :collapsed="isCollapsed"
        :active="activeMenu"
        @select="handleMenuSelect"
      />
    </div>
    <el-container class="main-container">
      <el-header class="app-header">
        <Navbar />
      </el-header>
      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.app-container {
  height: 100vh;
  display: flex;
}
.sidebar-wrapper {
  width: 220px;
  min-width: 220px;
  background-color: #304156;
  transition: width 0.3s, min-width 0.3s;
}
.sidebar-wrapper.collapsed {
  width: 64px;
  min-width: 64px;
}
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.app-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 56px;
  min-height: 56px;
}
.app-main {
  background: #f5f7fa;
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}
</style>
