import request from '@/utils/request'

export interface ScriptCardDevice {
  id?: string
  cardNo: string
  deviceAndroidId: string
  deviceWidth?: number
  deviceHeight?: number
  deviceBrand?: string
  deviceName?: string
  deviceModel?: string
  deviceSdkInt?: string
  deviceImei?: string
  deviceBroad?: string
  deviceBuildId?: string
  remark?: string
  createTime?: string
  updateTime?: string
  createBy?: string
  updateBy?: string
}

export interface PageParams {
  current: number
  size: number
  cardNo?: string
  deviceAndroidId?: string
  deviceBrand?: string
  deviceModel?: string
}

/**
 * 获取设备列表
 */
export function getScriptCardDeviceList(params: PageParams) {
  return request({
    url: '/script/cardDevice/list',
    method: 'get',
    params
  })
}

/**
 * 添加设备绑定
 */
export function addScriptCardDevice(data: ScriptCardDevice) {
  return request({
    url: '/script/cardDevice/add',
    method: 'post',
    data
  })
}

/**
 * 删除设备绑定
 */
export function deleteScriptCardDevice(id: string) {
  return request({
    url: `/script/cardDevice/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除设备绑定
 */
export function batchDeleteScriptCardDevice(ids: string[]) {
  return request({
    url: '/script/cardDevice/batchDelete',
    method: 'delete',
    data: ids
  })
}

/**
 * 获取设备详情
 */
export function getScriptCardDeviceDetail(id: string) {
  return request({
    url: `/script/cardDevice/detail/${id}`,
    method: 'get'
  })
}

/**
 * 统计卡密已绑定的设备数量
 */
export function countBoundDevices(cardNo: string) {
  return request({
    url: `/script/cardDevice/count/${cardNo}`,
    method: 'get'
  })
}
