<template>
  <div class="dashboard-container" v-loading="loading">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card revenue">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="48"><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总收益</div>
              <div class="stat-value">¥{{ formatCurrency(stats.totalRevenue) }}</div>
              <div class="stat-trend">
                <span class="trend-label">今日新增：</span>
                <span class="trend-value">¥{{ formatCurrency(stats.todayRevenue) }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card cards">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="48"><Postcard /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总卡密数</div>
              <div class="stat-value">{{ stats.totalCards || 0 }}</div>
              <div class="stat-trend">
                <span class="trend-label">今日新增：</span>
                <span class="trend-value">+{{ stats.todayCards || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card devices">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="48"><Iphone /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总设备数</div>
              <div class="stat-value">{{ stats.totalDevices || 0 }}</div>
              <div class="stat-trend">
                <span class="trend-label">今日新增：</span>
                <span class="trend-value">+{{ stats.todayDevices || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card status">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="48"><DataAnalysis /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">卡密状态</div>
              <div class="stat-value">{{ stats.cardStatusStats?.normalCount || 0 }}</div>
              <div class="stat-trend">
                <span class="trend-label">正常</span>
                <span class="trend-label" style="margin-left: 10px;">停用：{{ stats.cardStatusStats?.disabledCount || 0 }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 收益趋势图表 -->
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">近30天收益趋势</span>
            </div>
          </template>
          <div ref="revenueChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>

      <!-- 卡密新增趋势图表 -->
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">近7天卡密新增趋势</span>
            </div>
          </template>
          <div ref="cardTrendChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 游戏统计和设备趋势 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 游戏卡密统计图表 -->
      <el-col :xs="24" :sm="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">游戏卡密统计</span>
            </div>
          </template>
          <div ref="gameStatsChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>

      <!-- 设备趋势和卡密状态 -->
      <el-col :xs="24" :sm="24" :lg="12">
        <!-- 设备绑定趋势图表 -->
        <el-card shadow="hover" style="margin-bottom: 20px;">
          <template #header>
            <div class="card-header">
              <span class="card-title">近7天设备绑定趋势</span>
            </div>
          </template>
          <div ref="deviceTrendChartRef" style="height: 150px"></div>
        </el-card>

        <!-- 卡密状态分布图表 -->
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">卡密状态分布</span>
            </div>
          </template>
          <div ref="cardStatusChartRef" style="height: 170px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getDashboardStats, type DashboardStats } from '@/api/dashboard'
import { ElMessage } from 'element-plus'
import { Coin, Postcard, Iphone, DataAnalysis } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const loading = ref(false)
const stats = ref<DashboardStats>({
  totalRevenue: 0,
  todayRevenue: 0,
  totalCards: 0,
  todayCards: 0,
  totalDevices: 0,
  todayDevices: 0,
  cardTrend: [],
  deviceTrend: [],
  gameStats: [],
  cardStatusStats: {
    normalCount: 0,
    disabledCount: 0,
    expiredCount: 0,
    expiringSoonCount: 0
  },
  revenueTrend: []
})

// 图表引用
const revenueChartRef = ref()
const cardTrendChartRef = ref()
const deviceTrendChartRef = ref()
const gameStatsChartRef = ref()
const cardStatusChartRef = ref()

// 图表实例
let revenueChart: echarts.ECharts | null = null
let cardTrendChart: echarts.ECharts | null = null
let deviceTrendChart: echarts.ECharts | null = null
let gameStatsChart: echarts.ECharts | null = null
let cardStatusChart: echarts.ECharts | null = null

// 加载统计数据
const loadDashboardStats = async () => {
  loading.value = true
  try {
    const res = await getDashboardStats()
    stats.value = res.data
    
    // 延迟初始化图表，确保DOM已渲染
    nextTick(() => {
      initCharts()
    })
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化所有图表
const initCharts = () => {
  initRevenueChart()
  initCardTrendChart()
  initDeviceTrendChart()
  initGameStatsChart()
  initCardStatusChart()
}

// 初始化收益趋势图表
const initRevenueChart = () => {
  if (!revenueChartRef.value) return
  
  if (revenueChart) {
    revenueChart.dispose()
  }
  
  revenueChart = echarts.init(revenueChartRef.value)
  
  const dates = stats.value.revenueTrend?.map(item => item.date) || []
  const revenues = stats.value.revenueTrend?.map(item => {
    const val = typeof item.revenue === 'string' ? parseFloat(item.revenue) : item.revenue
    return isNaN(val) ? 0 : val
  }) || []
  
  revenueChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        return `${params[0].name}<br/>收益: ¥${params[0].value.toFixed(2)}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '收益',
        type: 'line',
        smooth: true,
        data: revenues,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(255, 193, 7, 0.5)' },
            { offset: 1, color: 'rgba(255, 193, 7, 0.1)' }
          ])
        },
        lineStyle: {
          color: '#FFC107'
        },
        itemStyle: {
          color: '#FFC107'
        }
      }
    ]
  })
}

// 初始化卡密趋势图表
const initCardTrendChart = () => {
  if (!cardTrendChartRef.value) return
  
  if (cardTrendChart) {
    cardTrendChart.dispose()
  }
  
  cardTrendChart = echarts.init(cardTrendChartRef.value)
  
  const dates = stats.value.cardTrend?.map(item => item.date) || []
  const counts = stats.value.cardTrend?.map(item => item.count) || []
  
  cardTrendChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增卡密',
        type: 'bar',
        data: counts,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#4CAF50' },
            { offset: 1, color: '#81C784' }
          ])
        }
      }
    ]
  })
}

// 初始化设备趋势图表
const initDeviceTrendChart = () => {
  if (!deviceTrendChartRef.value) return
  
  if (deviceTrendChart) {
    deviceTrendChart.dispose()
  }
  
  deviceTrendChart = echarts.init(deviceTrendChartRef.value)
  
  const dates = stats.value.deviceTrend?.map(item => item.date) || []
  const counts = stats.value.deviceTrend?.map(item => item.count) || []
  
  deviceTrendChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增设备',
        type: 'line',
        smooth: true,
        data: counts,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(33, 150, 243, 0.5)' },
            { offset: 1, color: 'rgba(33, 150, 243, 0.1)' }
          ])
        },
        lineStyle: {
          color: '#2196F3'
        },
        itemStyle: {
          color: '#2196F3'
        }
      }
    ]
  })
}

// 初始化游戏统计图表
const initGameStatsChart = () => {
  if (!gameStatsChartRef.value) return
  
  if (gameStatsChart) {
    gameStatsChart.dispose()
  }
  
  gameStatsChart = echarts.init(gameStatsChartRef.value)
  
  const games = stats.value.gameStats?.map(item => item.gameName) || []
  const totalCounts = stats.value.gameStats?.map(item => item.cardCount) || []
  const activeCounts = stats.value.gameStats?.map(item => item.activeCount) || []
  const inactiveCounts = stats.value.gameStats?.map(item => item.inactiveCount) || []
  
  gameStatsChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['总卡密', '已激活', '未激活']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: games
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '总卡密',
        type: 'bar',
        data: totalCounts,
        itemStyle: { color: '#409EFF' }
      },
      {
        name: '已激活',
        type: 'bar',
        data: activeCounts,
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '未激活',
        type: 'bar',
        data: inactiveCounts,
        itemStyle: { color: '#E6A23C' }
      }
    ]
  })
}

// 初始化卡密状态图表
const initCardStatusChart = () => {
  if (!cardStatusChartRef.value) return
  
  if (cardStatusChart) {
    cardStatusChart.dispose()
  }
  
  cardStatusChart = echarts.init(cardStatusChartRef.value)
  
  const statsData = stats.value.cardStatusStats
  
  cardStatusChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: '0%',
      left: 'center'
    },
    series: [
      {
        name: '卡密状态',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {c}'
        },
        data: [
          { value: statsData?.normalCount || 0, name: '正常', itemStyle: { color: '#67C23A' } },
          { value: statsData?.disabledCount || 0, name: '停用', itemStyle: { color: '#909399' } },
          { value: statsData?.expiredCount || 0, name: '已过期', itemStyle: { color: '#F56C6C' } },
          { value: statsData?.expiringSoonCount || 0, name: '即将过期', itemStyle: { color: '#E6A23C' } }
        ]
      }
    ]
  })
}

// 监听窗口大小变化
const handleResize = () => {
  revenueChart?.resize()
  cardTrendChart?.resize()
  deviceTrendChart?.resize()
  gameStatsChart?.resize()
  cardStatusChart?.resize()
}

// 格式化货币
const formatCurrency = (value: any) => {
  if (value === null || value === undefined) return '0.00'
  const num = typeof value === 'string' ? parseFloat(value) : value
  if (isNaN(num)) return '0.00'
  return num.toFixed(2)
}

onMounted(() => {
  loadDashboardStats()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  revenueChart?.dispose()
  cardTrendChart?.dispose()
  deviceTrendChart?.dispose()
  gameStatsChart?.dispose()
  cardStatusChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 120px);
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 48px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-trend {
  font-size: 12px;
  color: #67C23A;
}

.trend-label {
  color: #909399;
}

.trend-value {
  color: #67C23A;
  font-weight: 500;
}

.stat-card.revenue .stat-icon {
  background: linear-gradient(135deg, #FFC107 0%, #FFD54F 100%);
  color: #fff;
}

.stat-card.revenue .stat-value {
  color: #FFC107;
}

.stat-card.cards .stat-icon {
  background: linear-gradient(135deg, #4CAF50 0%, #81C784 100%);
  color: #fff;
}

.stat-card.cards .stat-value {
  color: #4CAF50;
}

.stat-card.devices .stat-icon {
  background: linear-gradient(135deg, #2196F3 0%, #64B5F6 100%);
  color: #fff;
}

.stat-card.devices .stat-value {
  color: #2196F3;
}

.stat-card.status .stat-icon {
  background: linear-gradient(135deg, #9C27B0 0%, #BA68C8 100%);
  color: #fff;
}

.stat-card.status .stat-value {
  color: #9C27B0;
}

.chart-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

:deep(.el-card) {
  border-radius: 8px;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #EBEEF5;
}

:deep(.el-card__body) {
  padding: 20px;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
  }
  
  .stat-icon {
    display: none;
  }
}
</style>
