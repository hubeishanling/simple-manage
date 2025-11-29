import request from '@/utils/request'

export interface User {
  id?: string
  username: string
  password?: string
  email?: string
  status: number
  lastLoginTime?: string
  lastLoginIp?: string
  createTime?: string
  updateTime?: string
  createBy?: string
  updateBy?: string
  remark?: string
}

export interface PageParams {
  current: number
  size: number
  username?: string
}

/**
 * 获取用户列表
 */
export function getUserList(params: PageParams) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

/**
 * 添加用户
 */
export function addUser(data: User) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}

/**
 * 更新用户
 */
export function updateUser(data: User) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id: string) {
  return request({
    url: `/user/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 获取用户详情
 */
export function getUserDetail(id: string) {
  return request({
    url: `/user/detail/${id}`,
    method: 'get'
  })
}
