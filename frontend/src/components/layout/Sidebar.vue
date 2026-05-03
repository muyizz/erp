<script setup lang="ts">
import { computed } from 'vue'

interface MenuItem {
  id: number
  parentId: number
  name: string
  path?: string
  icon?: string
  type: number
  children?: MenuItem[]
}

const props = defineProps<{
  menus: MenuItem[]
  collapsed: boolean
  active: string
}>()

const emit = defineEmits<{ select: [index: string] }>()

const menuTree = computed(() => {
  return props.menus
    .filter(m => m.type === 1)
    .map(m => ({
      ...m,
      children: m.children?.filter(c => c.type === 2 && c.path)
    }))
})
</script>

<template>
  <div class="sidebar-inner">
    <div class="logo">
      <span v-if="!collapsed" class="logo-full">ERP 系统</span>
      <span v-else class="logo-mini">ERP</span>
    </div>

    <el-menu
      :default-active="active"
      :collapse="collapsed"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
      @select="emit('select', $event)"
    >
      <template v-for="menu in menuTree" :key="menu.id">
        <el-sub-menu
          v-if="menu.children && menu.children.length > 0"
          :index="menu.path || String(menu.id)"
        >
          <template #title>
            <span>{{ menu.name }}</span>
          </template>
          <el-menu-item
            v-for="child in menu.children"
            :key="child.id"
            :index="child.path"
          >
            {{ child.name }}
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item
          v-else
          :index="menu.path || String(menu.id)"
        >
          {{ menu.name }}
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<style scoped>
.sidebar-inner {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: #fff;
}
.logo-full {
  font-size: 18px;
  font-weight: 600;
}
.logo-mini {
  font-size: 16px;
  font-weight: 700;
}
.el-menu {
  border-right: none !important;
  flex: 1;
  overflow-y: auto;
}
</style>
