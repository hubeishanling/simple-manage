import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/Index.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/User.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: '/script/card',
        name: 'ScriptCard',
        component: () => import('@/views/script/Card.vue'),
        meta: { title: '卡密管理' }
      },
      {
        path: '/script/device',
        name: 'ScriptDevice',
        component: () => import('@/views/script/Device.vue'),
        meta: { title: '设备管理' }
      },
      {
        path: '/script/game',
        name: 'ScriptGame',
        component: () => import('@/views/script/Game.vue'),
        meta: { title: '游戏管理' }
      },
      {
        path: '/script/version',
        name: 'ScriptVersion',
        component: () => import('@/views/script/Version.vue'),
        meta: { title: '版本控制' }
      },
      {
        path: '/script/rsakey',
        name: 'ScriptRsaKey',
        component: () => import('@/views/script/RsaKey.vue'),
        meta: { title: 'RSA密钥' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 设置页面标题
  document.title = (to.meta.title as string) || '简单管理系统'
  
  // 如果访问登录页且已登录，跳转到首页
  if (to.path === '/login' && token) {
    next('/home')
    return
  }
  
  // 如果访问其他页面且未登录，跳转到登录页
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }
  
  next()
})

export default router
