import request from '@/utils/request'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  userId: number
  username: string
  email: string
  token: string
  tokenType: string
  expiresIn: number
}

export interface UserInfo {
  id: number
  username: string
  email: string
  status: number
  lastLoginTime: string
  createTime: string
}

/**
 * 用户登录
 */
export function login(data: LoginRequest) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request({
    url: '/auth/current',
    method: 'get'
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
