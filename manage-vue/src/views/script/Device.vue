<template>
  <div class="device-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="卡密">
          <el-input v-model="searchForm.cardNo" placeholder="请输入卡密" clearable />
        </el-form-item>
        <el-form-item label="设备ID">
          <el-input v-model="searchForm.deviceAndroidId" placeholder="请输入设备Android ID" clearable />
        </el-form-item>
        <el-form-item label="厂商品牌">
          <el-input v-model="searchForm.deviceBrand" placeholder="请输入厂商品牌" clearable />
        </el-form-item>
        <el-form-item label="设备型号">
          <el-input v-model="searchForm.deviceModel" placeholder="请输入设备型号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 设备列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>设备绑定列表</span>
          <div>
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增绑定</el-button>
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
        <el-table-column prop="cardNo" label="卡密" width="120" />
        <el-table-column prop="deviceAndroidId" label="设备Android ID" width="150" show-overflow-tooltip />
        <el-table-column label="屏幕分辨率" width="120">
          <template #default="{ row }">
            <span v-if="row.deviceWidth && row.deviceHeight">{{ row.deviceWidth }}x{{ row.deviceHeight }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="deviceBrand" label="厂商品牌" width="100" />
        <el-table-column prop="deviceName" label="设备名称" width="120" show-overflow-tooltip />
        <el-table-column prop="deviceModel" label="设备型号" width="120" show-overflow-tooltip />
        <el-table-column prop="deviceSdkInt" label="API版本" width="80" />
        <el-table-column prop="deviceImei" label="IMEI" width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="绑定时间" width="180" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">详情</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">解绑</el-button>
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

    <!-- 查看设备详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="设备详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="卡密">{{ viewData.cardNo }}</el-descriptions-item>
        <el-descriptions-item label="设备Android ID">{{ viewData.deviceAndroidId }}</el-descriptions-item>
        <el-descriptions-item label="屏幕宽度">{{ viewData.deviceWidth || '-' }}px</el-descriptions-item>
        <el-descriptions-item label="屏幕高度">{{ viewData.deviceHeight || '-' }}px</el-descriptions-item>
        <el-descriptions-item label="厂商品牌">{{ viewData.deviceBrand || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ viewData.deviceName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备型号">{{ viewData.deviceModel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="API版本">{{ viewData.deviceSdkInt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备IMEI">{{ viewData.deviceImei || '-' }}</el-descriptions-item>
        <el-descriptions-item label="主板型号">{{ viewData.deviceBroad || '-' }}</el-descriptions-item>
        <el-descriptions-item label="版本号">{{ viewData.deviceBuildId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 新增绑定对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="新增设备绑定"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
      >
        <el-form-item label="卡密" prop="cardNo">
          <el-input v-model="formData.cardNo" placeholder="请输入卡密" />
        </el-form-item>
        <el-form-item label="设备Android ID" prop="deviceAndroidId">
          <el-input v-model="formData.deviceAndroidId" placeholder="请输入设备Android ID" />
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
import { Search, Refresh, Plus, Delete, View } from '@element-plus/icons-vue'
import {
  getScriptCardDeviceList,
  addScriptCardDevice,
  deleteScriptCardDevice,
  batchDeleteScriptCardDevice,
  type ScriptCardDevice,
  type PageParams
} from '@/api/scriptCardDevice'

const loading = ref(false)
const tableData = ref<ScriptCardDevice[]>([])
const selectedIds = ref<string[]>([])

const searchForm = reactive({
  cardNo: '',
  deviceAndroidId: '',
  deviceBrand: '',
  deviceModel: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const submitLoading = ref(false)

const viewData = ref<ScriptCardDevice>({
  cardNo: '',
  deviceAndroidId: ''
})

const formRef = ref<FormInstance>()
const formData = reactive<ScriptCardDevice>({
  cardNo: '',
  deviceAndroidId: ''
})

const formRules: FormRules = {
  cardNo: [
    { required: true, message: '请输入卡密', trigger: 'blur' }
  ],
  deviceAndroidId: [
    { required: true, message: '请输入设备Android ID', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const params: PageParams = {
      current: pagination.current,
      size: pagination.size,
      cardNo: searchForm.cardNo || undefined,
      deviceAndroidId: searchForm.deviceAndroidId || undefined,
      deviceBrand: searchForm.deviceBrand || undefined,
      deviceModel: searchForm.deviceModel || undefined
    }
    const res: any = await getScriptCardDeviceList(params)
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
  searchForm.deviceAndroidId = ''
  searchForm.deviceBrand = ''
  searchForm.deviceModel = ''
  pagination.current = 1
  loadData()
}

const handleView = async (row: ScriptCardDevice) => {
  try {
    const res: any = await getScriptCardDeviceDetail(row.id!)
    if (res.code === 200) {
      viewData.value = res.data
      viewDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取详情失败：', error)
  }
}

const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

const handleSelectionChange = (selection: ScriptCardDevice[]) => {
  selectedIds.value = selection.map(item => item.id!).filter(id => id)
}

const handleAdd = () => {
  dialogVisible.value = true
}

const handleDelete = (row: ScriptCardDevice) => {
  ElMessageBox.confirm(`确定要解绑设备 "${row.deviceAndroidId}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await deleteScriptCardDevice(row.id!)
      if (res.code === 200) {
        ElMessage.success('解绑成功')
        loadData()
      }
    } catch (error) {
      console.error('解绑失败：', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要解绑选中的 ${selectedIds.value.length} 个设备吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await batchDeleteScriptCardDevice(selectedIds.value)
      if (res.code === 200) {
        ElMessage.success('批量解绑成功')
        selectedIds.value = []
        loadData()
      }
    } catch (error) {
      console.error('批量解绑失败：', error)
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
        const res: any = await addScriptCardDevice(formData)
        
        if (res.code === 200) {
          ElMessage.success('绑定成功')
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
    deviceAndroidId: ''
  })
}
</script>

<style scoped>
.device-container {
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
