<template>
  <div class="card-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="卡密">
          <el-input v-model="searchForm.cardNo" placeholder="请输入卡密" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 卡密列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>卡密列表</span>
          <div>
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增卡密</el-button>
            <el-button
              type="danger"
              :icon="Delete"
              :disabled="selectedIds.length === 0"
              @click="handleBatchDelete"
            >
              批量删除
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="tableData"
        v-loading="loading"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="cardNo" label="卡密" width="200" />
        <el-table-column prop="expireDay" label="过期天数" width="100" />
        <el-table-column prop="price" label="价格（元）" width="120">
          <template #default="{ row }">
            ¥{{ row.price ? row.price.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="deviceSize" label="可绑定设备数" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间" width="180">
          <template #default="{ row }">
            {{ row.expireTime || '未绑定' }}
          </template>
        </el-table-column>
        <el-table-column prop="loginIp" label="最后登录IP" width="150" />
        <el-table-column prop="loginDate" label="最后登录时间" width="180" />
        <el-table-column label="关联游戏" width="150">
          <template #default="{ row }">
            <el-tag 
              v-for="game in (row.cardGames || [])"
              :key="game.gameId" 
              size="small" 
              class="mr-1"
            >
              {{ game.gameTitle }}
            </el-tag>
            <span v-if="!row.cardGames || row.cardGames.length === 0">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip min-width="150" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="卡密" prop="cardNo">
          <div style="display: flex; gap: 10px;">
            <el-input 
              v-model="formData.cardNo" 
              placeholder="请输入卡密" 
              :disabled="isEdit"
              style="flex: 1;"
            />
            <el-button 
              v-if="!isEdit" 
              type="primary" 
              @click="generateCardNo"
            >
              随机生成
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="过期天数" prop="expireDay">
          <el-input-number
            v-model="formData.expireDay"
            :min="1"
            :max="3650"
            placeholder="请输入过期天数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="formData.price"
            :min="0"
            :precision="2"
            placeholder="请输入价格"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="过期时间" prop="expireTime">
          <el-date-picker
            v-model="formData.expireTime"
            type="datetime"
            placeholder="请选择过期时间（可选）"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="可绑定设备数" prop="deviceSize">
          <el-input-number
            v-model="formData.deviceSize"
            :min="1"
            :max="100"
            placeholder="请输入可绑定设备数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="关联游戏" prop="gameIds">
          <el-select 
            v-model="formData.gameIds" 
            multiple 
            placeholder="请选择关联的游戏" 
            style="width: 100%"
          >
            <el-option
              v-for="game in gameList"
              :key="game.id"
              :label="game.title"
              :value="game.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getScriptCardList,
  getScriptCardDetail,
  addScriptCard,
  updateScriptCard,
  deleteScriptCard,
  batchDeleteScriptCard,
  getGameList,
  type ScriptCard,
  type ScriptGame,
  type PageParams
} from '@/api/scriptCard'

const loading = ref(false)
const tableData = ref<ScriptCard[]>([])
const selectedIds = ref<string[]>([])
const gameList = ref<ScriptGame[]>([])

const searchForm = reactive({
  cardNo: '',
  status: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增卡密')
const isEdit = ref(false)
const submitLoading = ref(false)

const formRef = ref<FormInstance>()
const formData = reactive<ScriptCard>({
  cardNo: '',
  expireDay: 30,
  price: 0,
  expireTime: undefined,
  deviceSize: 1,
  status: 1,
  remark: '',
  gameIds: []
})

const formRules: FormRules = {
  cardNo: [
    { required: true, message: '请输入卡密', trigger: 'blur' }
  ],
  expireDay: [
    { required: true, message: '请输入过期天数', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  gameIds: [
    { required: true, message: '请至少选择一个关联游戏', trigger: 'change' }
  ]
}

onMounted(() => {
  loadData()
  loadGameList()
})

/** 加载游戏列表 */
const loadGameList = async () => {
  try {
    const res: any = await getGameList()
    if (res.code === 200) {
      gameList.value = res.data
    }
  } catch (error) {
    console.error('加载游戏列表失败：', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params: PageParams = {
      current: pagination.current,
      size: pagination.size,
      cardNo: searchForm.cardNo || undefined,
      status: searchForm.status
    }
    const res: any = await getScriptCardList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载数据失败：', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.cardNo = ''
  searchForm.status = undefined
  pagination.current = 1
  loadData()
}

const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

const handleSelectionChange = (selection: ScriptCard[]) => {
  selectedIds.value = selection.map(item => item.id!).filter(id => id)
}

const handleAdd = () => {
  dialogTitle.value = '新增卡密'
  isEdit.value = false
  dialogVisible.value = true
}

const handleEdit = async (row: ScriptCard) => {
  dialogTitle.value = '编辑卡密'
  isEdit.value = true
  
  // 调用后端接口获取完整数据（包括关联的游戏）
  try {
    const res: any = await getScriptCardDetail(row.id!)
    if (res.code === 200) {
      Object.assign(formData, res.data)
      // 设置关联的游戏ID列表
      if (res.data.cardGames && res.data.cardGames.length > 0) {
        formData.gameIds = res.data.cardGames.map((item: any) => item.gameId)
      } else {
        formData.gameIds = []
      }
    }
  } catch (error) {
    console.error('获取卡密详情失败：', error)
    ElMessage.error('获取卡密详情失败')
    return
  }
  
  dialogVisible.value = true
}

const handleDelete = (row: ScriptCard) => {
  ElMessageBox.confirm(`确定要删除卡密 "${row.cardNo}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await deleteScriptCard(row.id!)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      console.error('删除失败：', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个卡密吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await batchDeleteScriptCard(selectedIds.value)
      if (res.code === 200) {
        ElMessage.success('批量删除成功')
        selectedIds.value = []
        loadData()
      }
    } catch (error) {
      console.error('批量删除失败：', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const res: any = isEdit.value 
          ? await updateScriptCard(formData)
          : await addScriptCard(formData)
        
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          loadData()
        }
      } catch (error) {
        console.error('提交失败：', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    cardNo: '',
    expireDay: 30,
    price: 0,
    expireTime: undefined,
    deviceSize: 1,
    status: 1,
    remark: '',
    gameIds: []
  })
}

/** 随机生成卡密 */
const generateCardNo = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
  const length = Math.floor(Math.random() * 3) + 6 // 6-8位
  let cardNo = ''
  for (let i = 0; i < length; i++) {
    cardNo += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  formData.cardNo = cardNo
}
</script>

<style scoped>
.card-container {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>
