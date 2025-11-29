import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getCurrentUser, logout } from '@/api/auth'
import type { LoginRequest, UserInfo } from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  // 登录
  const doLogin = async (loginData: LoginRequest) => {
    try {
      const res: any = await login(loginData)
      if (res.code === 200 && res.data) {
        token.value = res.data.token
        localStorage.setItem('token', res.data.token)
        
        // 保存用户基本信息
        const info = {
          id: res.data.userId,
          username: res.data.username,
          email: res.data.email
        }
        userInfo.value = info as UserInfo
        localStorage.setItem('userInfo', JSON.stringify(info))
        
        ElMessage.success('登录成功')
        return true
      }
      return false
    } catch (error) {
      console.error('登录失败：', error)
      return false
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const res: any = await getCurrentUser()
      if (res.code === 200 && res.data) {
        userInfo.value = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
      }
    } catch (error) {
      console.error('获取用户信息失败：', error)
    }
  }

  // 退出登录
  const doLogout = async () => {
    try {
      await logout()
    } catch (error) {
      console.error('退出登录失败：', error)
    } finally {
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      ElMessage.success('已退出登录')
    }
  }

  // 初始化用户信息
  const initUserInfo = () => {
    const storedInfo = localStorage.getItem('userInfo')
    if (storedInfo) {
      try {
        userInfo.value = JSON.parse(storedInfo)
      } catch (error) {
        console.error('解析用户信息失败：', error)
      }
    }
  }

  return {
    token,
    userInfo,
    doLogin,
    getUserInfo,
    doLogout,
    initUserInfo
  }
})
