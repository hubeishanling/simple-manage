import request from '@/utils/request'

export interface ScriptVersion {
  id?: string
  gameId: string
  fileUrl?: string
  type: number
  version?: number
  fileMd5?: string
  fileSize?: number
  remark?: string
  createTime?: string
  updateTime?: string
}

export interface PageParams {
  current: number
  size: number
  gameId?: string
  type?: number
  version?: number
}

/**
 * 获取版本列表（分页）
 */
export function getScriptVersionList(params: PageParams) {
  return request({
    url: '/script/version/list',
    method: 'get',
    params
  })
}

/**
 * 上传脚本文件
 */
export function uploadScriptFile(formData: FormData) {
  return request({
    url: '/script/version/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新脚本文件（带文件）
 */
export function updateScriptFile(formData: FormData) {
  return request({
    url: '/script/version/updateFile',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新版本信息（不更新文件）
 */
export function updateScriptVersion(data: ScriptVersion) {
  return request({
    url: '/script/version/update',
    method: 'put',
    data
  })
}

/**
 * 删除版本
 */
export function deleteScriptVersion(id: string) {
  return request({
    url: `/script/version/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除版本
 */
export function batchDeleteScriptVersion(ids: string[]) {
  return request({
    url: '/script/version/batchDelete',
    method: 'delete',
    data: ids
  })
}

/**
 * 获取版本详情
 */
export function getScriptVersionDetail(id: string) {
  return request({
    url: `/script/version/detail/${id}`,
    method: 'get'
  })
}

/**
 * 获取版本历史
 */
export function getVersionHistory(gameId: string) {
  return request({
    url: `/script/version/history/${gameId}`,
    method: 'get'
  })
}
