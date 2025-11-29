<template>
  <div class="rsakey-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="密钥名称">
          <el-input v-model="searchForm.keyName" placeholder="请输入密钥名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="停用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- RSA密钥列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>RSA密钥列表</span>
          <div>
            <el-button type="primary" :icon="Plus" @click="handleAdd">新增密钥</el-button>
            <el-button type="success" :icon="Document" @click="handleGenerateConfig">生成 config.js</el-button>
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
        <el-table-column prop="id" label="ID" width="180" show-overflow-tooltip />
        <el-table-column prop="keyName" label="密钥名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否默认" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isDefault === 1 ? 'success' : 'info'">
              {{ row.isDefault === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleView(row)">查看</el-button>
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.isDefault !== 1" link type="success" :icon="Select" @click="handleSetDefault(row)">设为默认</el-button>
            <el-button v-if="row.isDefault !== 1" link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑/查看对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="140px"
        :disabled="isView"
      >
        <el-form-item label="密钥名称" prop="keyName">
          <el-input v-model="formData.keyName" placeholder="请输入密钥名称" />
        </el-form-item>

        <el-divider content-position="left">请求加密密钥对（前端→后端）</el-divider>
        <el-form-item label="请求加密公钥" prop="requestPublicKey">
          <el-input
            v-model="formData.requestPublicKey"
            type="textarea"
            :rows="3"
            placeholder="前端使用此公钥加密请求数据"
          />
        </el-form-item>
        <el-form-item label="请求解密私钥" prop="requestPrivateKey">
          <el-input
            v-model="formData.requestPrivateKey"
            type="textarea"
            :rows="3"
            placeholder="后端使用此私钥解密请求数据"
            show-password
          />
        </el-form-item>

        <el-divider content-position="left">响应加密密钥对（后端→前端）</el-divider>
        <el-form-item label="响应加密公钥" prop="responsePublicKey">
          <el-input
            v-model="formData.responsePublicKey"
            type="textarea"
            :rows="3"
            placeholder="后端使用此公钥加密响应数据"
          />
        </el-form-item>
        <el-form-item label="响应解密私钥" prop="responsePrivateKey">
          <el-input
            v-model="formData.responsePrivateKey"
            type="textarea"
            :rows="3"
            placeholder="前端使用此私钥解密响应数据"
            show-password
          />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否默认">
          <el-radio-group v-model="formData.isDefault">
            <el-radio :value="1">是</el-radio>
            <el-radio :value="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div v-if="!isView">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
          <el-button type="success" @click="handleGenerateRequestKey">生成请求密钥对</el-button>
          <el-button type="success" @click="handleGenerateResponseKey">生成响应密钥对</el-button>
        </div>
        <div v-else>
          <el-button @click="dialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- config.js 预览对话框 -->
    <el-dialog
      v-model="configDialogVisible"
      title="预览 config.js"
      width="900px"
      @close="handleConfigDialogClose"
    >
      <div class="config-preview">
        <div class="game-selector">
          <el-form :inline="true">
            <el-form-item label="选择游戏">
              <el-select
                v-model="selectedGameId"
                placeholder="请选择游戏（可选）"
                clearable
                style="width: 300px"
                @change="handleGameChange"
              >
                <el-option
                  v-for="game in gameList"
                  :key="game.id"
                  :label="game.title"
                  :value="game.id"
                />
              </el-select>
            </el-form-item>
          </el-form>
          <el-text type="info" size="small">
            选择游戏后，配置文件中的 GAME_ID 将被替换为对应的游戏ID
          </el-text>
        </div>

        <div class="code-editor">
          <el-input
            v-model="configContent"
            type="textarea"
            :rows="20"
            placeholder="配置内容将在这里显示..."
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" :icon="Download" @click="handleDownloadConfig">下载文件</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, View, Select, Document, Download } from '@element-plus/icons-vue'
import {
  getScriptRsaKeyList,
  addScriptRsaKey,
  updateScriptRsaKey,
  deleteScriptRsaKey,
  batchDeleteScriptRsaKey,
  setDefaultKey,
  generateKeyPair,
  generateConfig,
  type ScriptRsaKey,
  type PageParams
} from '@/api/scriptRsaKey'
import { getAllScriptGames } from '@/api/scriptGame'

const loading = ref(false)
const tableData = ref<ScriptRsaKey[]>([])
const selectedIds = ref<string[]>([])
const gameList = ref<any[]>([])

const searchForm = reactive({
  keyName: '',
  status: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增RSA密钥')
const isEdit = ref(false)
const isView = ref(false)
const submitLoading = ref(false)

const formRef = ref<FormInstance>()
const formData = reactive<ScriptRsaKey>({
  keyName: '',
  requestPublicKey: '',
  requestPrivateKey: '',
  responsePublicKey: '',
  responsePrivateKey: '',
  status: 0,
  isDefault: 0,
  remark: ''
})

const formRules: FormRules = {
  keyName: [
    { required: true, message: '请输入密钥名称', trigger: 'blur' }
  ],
  requestPublicKey: [
    { required: true, message: '请求加密公钥不能为空', trigger: 'blur' }
  ],
  requestPrivateKey: [
    { required: true, message: '请求解密私钥不能为空', trigger: 'blur' }
  ],
  responsePublicKey: [
    { required: true, message: '响应加密公钥不能为空', trigger: 'blur' }
  ],
  responsePrivateKey: [
    { required: true, message: '响应解密私钥不能为空', trigger: 'blur' }
  ]
}

const configDialogVisible = ref(false)
const configContent = ref('')
const selectedGameId = ref<string>('')

onMounted(() => {
  loadData()
  loadGameList()
})

const loadGameList = async () => {
  try {
    const res: any = await getAllScriptGames()
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
      keyName: searchForm.keyName || undefined,
      status: searchForm.status
    }
    const res: any = await getScriptRsaKeyList(params)
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
  searchForm.keyName = ''
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

const handleSelectionChange = (selection: ScriptRsaKey[]) => {
  selectedIds.value = selection.map(item => item.id!).filter(id => id)
}

const handleAdd = () => {
  dialogTitle.value = '新增RSA密钥'
  isEdit.value = false
  isView.value = false
  dialogVisible.value = true
}

const handleView = (row: ScriptRsaKey) => {
  dialogTitle.value = '查看RSA密钥'
  isView.value = true
  isEdit.value = false
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleEdit = (row: ScriptRsaKey) => {
  dialogTitle.value = '编辑RSA密钥'
  isEdit.value = true
  isView.value = false
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row: ScriptRsaKey) => {
  ElMessageBox.confirm(`确定要删除密钥 "${row.keyName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await deleteScriptRsaKey(row.id!)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    } catch (error) {
      console.error('删除失败：', error)
    }
  }).catch(() => {})
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个密钥吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await batchDeleteScriptRsaKey(selectedIds.value)
      if (res.code === 200) {
        ElMessage.success('批量删除成功')
        selectedIds.value = []
        loadData()
      }
    } catch (error) {
      console.error('批量删除失败：', error)
    }
  }).catch(() => {})
}

const handleSetDefault = (row: ScriptRsaKey) => {
  ElMessageBox.confirm(`确定将密钥 "${row.keyName}" 设置为默认密钥吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      const res: any = await setDefaultKey(row.id!)
      if (res.code === 200) {
        ElMessage.success('设置成功')
        loadData()
      }
    } catch (error) {
      console.error('设置失败：', error)
    }
  }).catch(() => {})
}

const handleGenerateRequestKey = async () => {
  try {
    const res: any = await generateKeyPair()
    if (res.code === 200) {
      formData.requestPublicKey = res.data.publicKey
      formData.requestPrivateKey = res.data.privateKey
      ElMessage.success('请求密钥对生成成功')
    }
  } catch (error) {
    console.error('生成密钥对失败：', error)
  }
}

const handleGenerateResponseKey = async () => {
  try {
    const res: any = await generateKeyPair()
    if (res.code === 200) {
      formData.responsePublicKey = res.data.publicKey
      formData.responsePrivateKey = res.data.privateKey
      ElMessage.success('响应密钥对生成成功')
    }
  } catch (error) {
    console.error('生成密钥对失败：', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const res: any = isEdit.value 
          ? await updateScriptRsaKey(formData)
          : await addScriptRsaKey(formData)
        
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
    keyName: '',
    requestPublicKey: '',
    requestPrivateKey: '',
    responsePublicKey: '',
    responsePrivateKey: '',
    status: 0,
    isDefault: 0,
    remark: ''
  })
}

const handleGenerateConfig = async () => {
  try {
    selectedGameId.value = ''
    const res: any = await generateConfig()
    if (res.code === 200) {
      configContent.value = res.data.content
      configDialogVisible.value = true
    }
  } catch (error) {
    console.error('生成配置失败：', error)
  }
}

const handleGameChange = async () => {
  try {
    const res: any = await generateConfig(selectedGameId.value)
    if (res.code === 200) {
      configContent.value = res.data.content
    }
  } catch (error) {
    console.error('生成配置失败：', error)
  }
}

const handleDownloadConfig = () => {
  const blob = new Blob([configContent.value], { type: 'application/javascript;charset=utf-8' })
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = 'config.js'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
  ElMessage.success('文件下载成功')
}

const handleConfigDialogClose = () => {
  configContent.value = ''
  selectedGameId.value = ''
}
</script>

<style scoped>
.rsakey-container {
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

.config-preview {
  .game-selector {
    padding: 16px;
    background-color: #f9fafb;
    border-radius: 4px;
    margin-bottom: 16px;
    border: 1px solid #e5e7eb;
  }

  .code-editor {
    :deep(textarea) {
      font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
      font-size: 14px;
      line-height: 1.6;
    }
  }
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>
