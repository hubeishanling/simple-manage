import request from '@/utils/request'

export interface ScriptRsaKey {
  id?: string
  keyName: string
  requestPublicKey: string
  requestPrivateKey: string
  responsePublicKey: string
  responsePrivateKey: string
  status: number
  isDefault: number
  remark?: string
  createTime?: string
  updateTime?: string
}

export interface PageParams {
  current: number
  size: number
  keyName?: string
  status?: number
}

/**
 * 获取RSA密钥列表（分页）
 */
export function getScriptRsaKeyList(params: PageParams) {
  return request({
    url: '/script/rsaKey/list',
    method: 'get',
    params
  })
}

/**
 * 添加RSA密钥
 */
export function addScriptRsaKey(data: ScriptRsaKey) {
  return request({
    url: '/script/rsaKey/add',
    method: 'post',
    data
  })
}

/**
 * 更新RSA密钥
 */
export function updateScriptRsaKey(data: ScriptRsaKey) {
  return request({
    url: '/script/rsaKey/update',
    method: 'put',
    data
  })
}

/**
 * 删除RSA密钥
 */
export function deleteScriptRsaKey(id: string) {
  return request({
    url: `/script/rsaKey/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除RSA密钥
 */
export function batchDeleteScriptRsaKey(ids: string[]) {
  return request({
    url: '/script/rsaKey/batchDelete',
    method: 'delete',
    data: ids
  })
}

/**
 * 获取RSA密钥详情
 */
export function getScriptRsaKeyDetail(id: string) {
  return request({
    url: `/script/rsaKey/detail/${id}`,
    method: 'get'
  })
}

/**
 * 设置默认密钥
 */
export function setDefaultKey(id: string) {
  return request({
    url: `/script/rsaKey/setDefault/${id}`,
    method: 'put'
  })
}

/**
 * 生成RSA密钥对
 */
export function generateKeyPair() {
  return request({
    url: '/script/rsaKey/generateKeyPair',
    method: 'get'
  })
}

/**
 * 生成config.js配置文件
 */
export function generateConfig(gameId?: string) {
  return request({
    url: '/script/rsaKey/generateConfig',
    method: 'get',
    params: { gameId }
  })
}
