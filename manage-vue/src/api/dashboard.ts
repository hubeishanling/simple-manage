import request from '@/utils/request'

/**
 * 首页统计数据接口
 */

// 每日卡密趋势
export interface DailyCardTrend {
  date: string
  count: number
}

// 每日设备趋势
export interface DailyDeviceTrend {
  date: string
  count: number
}

// 每日收益趋势
export interface DailyRevenueTrend {
  date: string
  revenue: number
}

// 游戏卡密统计
export interface GameCardStats {
  gameId: string
  gameName: string
  cardCount: number
  activeCount: number
  inactiveCount: number
}

// 卡密状态统计
export interface CardStatusStats {
  normalCount: number
  disabledCount: number
  expiredCount: number
  expiringSoonCount: number
}

// 首页统计数据
export interface DashboardStats {
  totalRevenue: number
  todayRevenue: number
  totalCards: number
  todayCards: number
  totalDevices: number
  todayDevices: number
  cardTrend: DailyCardTrend[]
  deviceTrend: DailyDeviceTrend[]
  gameStats: GameCardStats[]
  cardStatusStats: CardStatusStats
  revenueTrend: DailyRevenueTrend[]
}

/**
 * 获取首页统计数据
 */
export function getDashboardStats() {
  return request<DashboardStats>({
    url: '/dashboard/stats',
    method: 'get'
  })
}
