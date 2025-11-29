import request from '@/utils/request'

export interface ScriptCard {
  id?: string
  cardNo: string
  expireDay: number
  price: number
  expireTime?: string
  deviceSize?: number
  status: number
  loginIp?: string
  loginDate?: string
  createTime?: string
  updateTime?: string
  createBy?: string
  updateBy?: string
  remark?: string
  gameIds?: string[]
}

export interface ScriptGame {
  id: string
  title: string
  remark?: string
  createTime?: string
  updateTime?: string
}

export interface PageParams {
  current: number
  size: number
  cardNo?: string
  status?: number
}

/**
 * 获取卡密列表
 */
export function getScriptCardList(params: PageParams) {
  return request({
    url: '/script/card/list',
    method: 'get',
    params
  })
}

/**
 * 添加卡密
 */
export function addScriptCard(data: ScriptCard) {
  return request({
    url: '/script/card/add',
    method: 'post',
    data
  })
}

/**
 * 更新卡密
 */
export function updateScriptCard(data: ScriptCard) {
  return request({
    url: '/script/card/update',
    method: 'put',
    data
  })
}

/**
 * 删除卡密
 */
export function deleteScriptCard(id: string) {
  return request({
    url: `/script/card/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除卡密
 */
export function batchDeleteScriptCard(ids: string[]) {
  return request({
    url: '/script/card/batchDelete',
    method: 'delete',
    data: ids
  })
}

/**
 * 获取卡密详情
 */
export function getScriptCardDetail(id: string) {
  return request({
    url: `/script/card/detail/${id}`,
    method: 'get'
  })
}

/**
 * 获取游戏列表（用于卡密关联游戏选择）
 */
export function getGameList() {
  return request({
    url: '/script/card/gameList',
    method: 'get'
  })
}
