import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/api/request'

interface MenuItem {
  id: number
  parentId: number
  name: string
  type: number
  path?: string
  component?: string
  permissionCode?: string
  icon?: string
  sortOrder: number
  children?: MenuItem[]
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('accessToken') || '')
  const userId = ref<number>(0)
  const username = ref('')
  const realName = ref('')
  const avatar = ref('')
  const permissions = ref<string[]>([])
  const menus = ref<MenuItem[]>([])

  async function login(data: { username: string; password: string }) {
    const res = await request.post('/auth/login', data)
    const d = res.data
    token.value = d.accessToken
    userId.value = d.userId
    username.value = d.username
    realName.value = d.realName
    permissions.value = d.permissions || []

    localStorage.setItem('accessToken', d.accessToken)
    localStorage.setItem('refreshToken', d.refreshToken)

    await loadUserInfo()
  }

  async function loadUserInfo() {
    const res = await request.get('/auth/userinfo')
    const d = res.data
    userId.value = d.userId
    username.value = d.username
    realName.value = d.realName
    avatar.value = d.avatar
    permissions.value = d.permissions || []
    menus.value = d.menus || []
  }

  function logout() {
    token.value = ''
    permissions.value = []
    menus.value = []
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  function hasPermission(code: string): boolean {
    return permissions.value.includes(code)
  }

  return { token, userId, username, realName, avatar, permissions, menus, login, loadUserInfo, logout, hasPermission }
})
