import request from '@/utils/request'

export interface ScriptGame {
  id?: string
  title: string
  remark?: string
  createTime?: string
  updateTime?: string
  createBy?: string
  updateBy?: string
}

export interface PageParams {
  current: number
  size: number
  title?: string
}

/**
 * 获取游戏列表（分页）
 */
export function getScriptGameList(params: PageParams) {
  return request({
    url: '/script/game/list',
    method: 'get',
    params
  })
}

/**
 * 获取所有游戏列表（不分页）
 */
export function getAllScriptGames() {
  return request({
    url: '/script/game/all',
    method: 'get'
  })
}

/**
 * 添加游戏
 */
export function addScriptGame(data: ScriptGame) {
  return request({
    url: '/script/game/add',
    method: 'post',
    data
  })
}

/**
 * 更新游戏
 */
export function updateScriptGame(data: ScriptGame) {
  return request({
    url: '/script/game/update',
    method: 'put',
    data
  })
}

/**
 * 删除游戏
 */
export function deleteScriptGame(id: string) {
  return request({
    url: `/script/game/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除游戏
 */
export function batchDeleteScriptGame(ids: string[]) {
  return request({
    url: '/script/game/batchDelete',
    method: 'delete',
    data: ids
  })
}

/**
 * 获取游戏详情
 */
export function getScriptGameDetail(id: string) {
  return request({
    url: `/script/game/detail/${id}`,
    method: 'get'
  })
}
