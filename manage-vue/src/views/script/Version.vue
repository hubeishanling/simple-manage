<template>
  <div class="version-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="游戏">
          <el-select v-model="searchForm.gameId" placeholder="请选择游戏" clearable style="width: 200px">
            <el-option
              v-for="game in gameList"
              :key="game.id"
              :label="game.title"
              :value="game.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文件类型">
          <el-select v-model="searchForm.type" placeholder="请选择文件类型" clearable style="width: 150px">
            <el-option label="APK插件" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="版本">
          <el-input v-model="searchForm.version" placeholder="请输入版本" clearable style="width: 120px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 版本列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>版本列表</span>
          <div>
            <el-button type="primary" :icon="Upload" @click="handleUpload">上传脚本</el-button>
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
        <el-table-column prop="gameId" label="游戏ID" width="180" />
        <el-table-column label="文件类型" width="100">
          <template #default="{ row }">
            <el-tag type="info">{{ row.type === 0 ? 'APK插件' : '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="版本" width="100">
          <template #default="{ row }">
            <el-tag type="success">v{{ row.version }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileUrl" label="文件地址" min-width="300" show-overflow-tooltip>
          <template #default="{ row }">
            <el-link type="primary" :href="row.fileUrl" target="_blank">
              {{ row.fileUrl }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
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

    <!-- 上传脚本对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传脚本"
      width="500px"
      @close="handleUploadDialogClose"
    >
      <el-form
        ref="uploadFormRef"
        :model="uploadForm"
        :rules="uploadRules"
        label-width="100px"
      >
        <el-form-item label="游戏" prop="gameId">
          <el-select v-model="uploadForm.gameId" placeholder="请选择游戏" style="width: 100%">
            <el-option
              v-for="game in gameList"
              :key="game.id"
              :label="game.title"
              :value="game.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文件类型">
          <el-input value="APK插件" disabled />
        </el-form-item>
        <el-form-item label="上传文件" prop="file" required>
          <el-upload
            ref="uploadRef"
            :limit="1"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            accept=".apk"
          >
            <el-button type="primary">选择APK文件</el-button>
            <template #tip>
              <div class="upload-tip">
                请选择.apk格式的插件文件（最大5MB）
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="uploadForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploadLoading" @click="handleSubmitUpload">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑版本对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑版本"
      width="500px"
      @close="handleEditDialogClose"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="100px"
      >
        <el-form-item label="游戏" prop="gameId">
          <el-select v-model="editForm.gameId" placeholder="请选择游戏" style="width: 100%" disabled>
            <el-option
              v-for="game in gameList"
              :key="game.id"
              :label="game.title"
              :value="game.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="当前文件">
          <el-link type="primary" :href="editForm.fileUrl" target="_blank">
            {{ editForm.fileUrl }}
          </el-link>
        </el-form-item>
        <el-form-item label="上传新文件">
          <el-upload
            ref="editUploadRef"
            :limit="1"
            :auto-upload="false"
            :on-change="handleEditFileChange"
            :on-remove="handleEditFileRemove"
            accept=".apk"
          >
            <el-button type="primary">选择APK文件</el-button>
            <template #tip>
              <div class="upload-tip">
                请选择.apk格式的插件文件（最大5MB，可选，不选则保持原文件）
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="editForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleSubmitEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadInstance } from 'element-plus'
import { Search, Refresh, Upload, Edit, Delete } from '@element-plus/icons-vue'
import {
  getScriptVersionList,
  uploadScriptFile,
  updateScriptFile,
  deleteScriptVersion,
  batchDeleteScriptVersion,
  type ScriptVersion,
  type PageParams
} from '@/api/scriptVersion'
import { getAllScriptGames } from '@/api/scriptGame'

const loading = ref(false)
const tableData = ref<ScriptVersion[]>([])
const selectedIds = ref<string[]>([])
const gameList = ref<any[]>([])

const searchForm = reactive({
  gameId: '',
  type: undefined as number | undefined,
  version: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const uploadDialogVisible = ref(false)
const uploadLoading = ref(false)
const uploadFormRef = ref<FormInstance>()
const uploadRef = ref<UploadInstance>()

const uploadForm = reactive({
  gameId: '',
  file: null as File | null,
  remark: ''
})

const uploadRules: FormRules = {
  gameId: [
    { required: true, message: '请选择游戏', trigger: 'change' }
  ]
}

const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref<FormInstance>()
const editUploadRef = ref<UploadInstance>()

const editForm = reactive<ScriptVersion & { file?: File | null }>({
  id: '',
  gameId: '',
  type: 0,
  fileUrl: '',
  remark: '',
  file: null
})

const editRules: FormRules = {
  gameId: [
    { required: true, message: '请选择游戏', trigger: 'change' }
  ]
}

const MAX_FILE_SIZE = 5 * 1024 * 1024 // 5MB

onMounted(() => {
  loadGameList()
  loadData()
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
      gameId: searchForm.gameId || undefined,
      type: searchForm.type,
      version: searchForm.version ? parseInt(searchForm.version) : undefined
    }
    const res: any = await getScriptVersionList(params)
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
  searchForm.gameId = ''
  searchForm.type = undefined
  searchForm.version = ''
  pagination.current = 1
  loadData()
}

const handleSizeChange = () => {
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

const handleSelectionChange = (selection: ScriptVersion[]) => {
  selectedIds.value = selection.map(item => item.id!).filter(id => id)
}

const handleUpload = () => {
  uploadDialogVisible.value = true
}

const handleFileChange = (file: any) => {
  const raw = file?.raw ?? file
  if (raw && raw.size > MAX_FILE_SIZE) {
    ElMessage.error('文件大小不能超过5MB')
    uploadRef.value?.clearFiles()
    uploadForm.file = null
    return
  }
  uploadForm.file = raw
}

const handleFileRemove = () => {
  uploadForm.file = null
}

const handleSubmitUpload = async () => {
  if (!uploadFormRef.value) return
  
  await uploadFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!uploadForm.file) {
        ElMessage.error('请选择上传文件')
        return
      }
      
      if (uploadForm.file.size > MAX_FILE_SIZE) {
        ElMessage.error('文件大小不能超过5MB')
        return
      }
      
      uploadLoading.value = true
      try {
        const formData = new FormData()
        formData.append('gameId', uploadForm.gameId)
        formData.append('file', uploadForm.file)
        if (uploadForm.remark) {
          formData.append('remark', uploadForm.remark)
        }
        
        const res: any = await uploadScriptFile(formData)
        if (res.code === 200) {
          ElMessage.success('上传成功')
          uploadDialogVisible.value = false
          loadData()
        }
      } catch (error) {
        console.error('上传失败：', error)
      } finally {
        uploadLoading.value = false
      }
    }
  })
}

const handleUploadDialogClose = () => {
  uploadFormRef.value?.resetFields()
  uploadRef.value?.clearFiles()
  uploadForm.file = null
  uploadForm.gameId = ''
  uploadForm.remark = ''
}

const handleEdit = (row: ScriptVersion) => {
  Object.assign(editForm, {
    ...row,
    file: null
  })
  editDialogVisible.value = true
}

const handleEditFileChange = (file: any) => {
  const raw = file?.raw ?? file
  if (raw && raw.size > MAX_FILE_SIZE) {
    ElMessage.error('文件大小不能超过5MB')
    editUploadRef.value?.clearFiles()
    editForm.file = null
    return
  }
  editForm.file = raw
}

const handleEditFileRemove = () => {
  editForm.file = null
}

const handleSubmitEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      editLoading.value = true
      try {
        if (editForm.file) {
          // 有文件，使用文件更新接口
          const formData = new FormData()
          formData.append('id', editForm.id!)
          formData.append('gameId', editForm.gameId)
          formData.append('file', editForm.file)
          if (editForm.remark) {
            formData.append('remark', editForm.remark)
          }
          
          const res: any = await updateScriptFile(formData)
          if (res.code === 200) {
            ElMessage.success('更新成功')
            editDialogVisible.value = false
            loadData()
          }
        } else {
          // 无文件，只更新备注等信息
          ElMessage.info('未选择新文件，仅更新备注信息')
          editDialogVisible.value = false
        }
      } catch (error) {
        console.error('更新失败：', error)
      } finally {
        editLoading.value = false
      }
    }
  })
}

const handleEditDialogClose = () => {
  editFormRef.value?.resetFields()
  editUploadRef.value?.clearFiles()
  editForm.file = null
}

const handleDelete = (row: ScriptVersion) => {
  ElMessageBox.confirm(`确定要删除版本 v${row.version} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await deleteScriptVersion(row.id!)
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个版本吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res: any = await batchDeleteScriptVersion(selectedIds.value)
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

const formatFileSize = (bytes: number | undefined): string => {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}
</script>

<style scoped>
.version-container {
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

.upload-tip {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>
