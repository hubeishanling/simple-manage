<template>
  <div class="home-container">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ userStore.userInfo?.username }}！</h2>
          <p>今天是 {{ currentDate }}</p>
        </div>
        <el-icon class="welcome-icon" :size="80">
          <TrophyBase />
        </el-icon>
      </div>
    </el-card>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card stat-card-blue">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">用户总数</div>
              <div class="stat-value">1,234</div>
            </div>
            <el-icon class="stat-icon" :size="48">
              <User />
            </el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card stat-card-green">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">今日访问</div>
              <div class="stat-value">567</div>
            </div>
            <el-icon class="stat-icon" :size="48">
              <View />
            </el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card stat-card-orange">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">消息通知</div>
              <div class="stat-value">89</div>
            </div>
            <el-icon class="stat-icon" :size="48">
              <Bell />
            </el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card stat-card-purple">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">待办事项</div>
              <div class="stat-value">12</div>
            </div>
            <el-icon class="stat-icon" :size="48">
              <Document />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 用户信息卡片 -->
    <el-row :gutter="20" class="info-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><User /></el-icon>
                用户信息
              </span>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户ID">
              {{ userStore.userInfo?.id }}
            </el-descriptions-item>
            <el-descriptions-item label="用户名">
              {{ userStore.userInfo?.username }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ userStore.userInfo?.email || '未设置' }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="userStore.userInfo?.status === 1 ? 'success' : 'danger'">
                {{ userStore.userInfo?.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="最后登录">
              {{ formatDateTime(userStore.userInfo?.lastLoginTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ formatDateTime(userStore.userInfo?.createTime) }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><InfoFilled /></el-icon>
                系统信息
              </span>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统名称">
              简单管理系统
            </el-descriptions-item>
            <el-descriptions-item label="系统版本">
              v1.0.0
            </el-descriptions-item>
            <el-descriptions-item label="后端框架">
              Spring Boot 3.2.0
            </el-descriptions-item>
            <el-descriptions-item label="前端框架">
              Vue 3 + Element Plus
            </el-descriptions-item>
            <el-descriptions-item label="认证方式">
              JWT Token
            </el-descriptions-item>
            <el-descriptions-item label="开发者">
              shanling
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { TrophyBase, User, View, Bell, Document, InfoFilled } from '@element-plus/icons-vue'

const userStore = useUserStore()

const currentDate = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const weekDay = weekDays[now.getDay()]
  return `${year}年${month}月${day}日 ${weekDay}`
})

const formatDateTime = (dateTimeStr: string | undefined) => {
  if (!dateTimeStr) return '暂无'
  const date = new Date(dateTimeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
</script>

<style scoped>
.home-container {
  max-width: 1400px;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.welcome-icon {
  color: rgba(255, 255, 255, 0.3);
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  font-weight: 600;
  color: #303133;
}

.stat-icon {
  opacity: 0.3;
}

.stat-card-blue .stat-icon {
  color: #409eff;
}

.stat-card-green .stat-icon {
  color: #67c23a;
}

.stat-card-orange .stat-icon {
  color: #e6a23c;
}

.stat-card-purple .stat-icon {
  color: #9c27b0;
}

.info-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

:deep(.el-card__body) {
  padding: 20px;
}

:deep(.el-descriptions__label) {
  width: 120px;
}

@media (max-width: 768px) {
  .welcome-icon {
    display: none;
  }
  
  .stat-icon {
    display: none;
  }
}
</style>
