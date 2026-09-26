<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getPostList,
  addPost,
  updatePost,
  deletePost,
  type SysPost
} from '@/api/post'

// --- 1. 数据与状态管理 ---
const loading = ref(false)
const rawPostList = ref<SysPost[]>([]) // 原始列表缓存
const tableData = ref<SysPost[]>([])    // 表格展示数据

// 搜索条件
const searchQuery = reactive({
  postCode: '',
  postName: '',
  status: ''
})

// 从后端拉取数据
const fetchPostData = async () => {
  loading.value = true
  try {
    const list = await getPostList()
    rawPostList.value = list
    filterTableData()
  } catch (error) {
    // 错误已被 request.ts 拦截器捕获
  } finally {
    loading.value = false
  }
}

// 前端内存复合过滤（兼顾速度与体验）
const filterTableData = () => {
  tableData.value = rawPostList.value.filter((item) => {
    // 岗位编码匹配
    const matchCode = !searchQuery.postCode || item.postCode.toLowerCase().includes(searchQuery.postCode.trim().toLowerCase())
    // 岗位名称匹配
    const matchName = !searchQuery.postName || item.postName.includes(searchQuery.postName.trim())
    // 状态过滤
    const matchStatus = searchQuery.status === '' || String(item.status) === searchQuery.status

    return matchCode && matchName && matchStatus
  })
}

const handleSearch = () => {
  filterTableData()
}

const handleReset = () => {
  searchQuery.postCode = ''
  searchQuery.postName = ''
  searchQuery.status = ''
  filterTableData()
}

// --- 2. 增删改模态框逻辑 ---
const dialogVisible = ref(false)
const dialogTitle = ref('')
const postFormRef = ref<FormInstance>()
const isEdit = ref(false)

const postFormData = reactive<SysPost>({
  postId: undefined,
  postCode: '',
  postName: '',
  postSort: 0,
  status: 0
})

// 校验规则：保证 postCode 的业务格式规范
const formRules: FormRules = {
  postName: [
    { required: true, message: '请输入岗位名称', trigger: 'blur' },
    { whitespace: true, message: '岗位名称不能为全空格', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  postCode: [
    { required: true, message: '请输入岗位编码', trigger: 'blur' },
    { whitespace: true, message: '岗位编码不能为全空格', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_-]+$/,
      message: '编码只能由英文字母、数字、下划线或中划线组成',
      trigger: 'blur'
    }
  ],
  postSort: [{ required: true, message: '请输入排序值', trigger: 'blur' }]
}

// 打开新增弹窗
const handleAdd = () => {
  dialogTitle.value = '新增岗位'
  isEdit.value = false

  postFormData.postId = undefined
  postFormData.postCode = ''
  postFormData.postName = ''
  postFormData.postSort = 0
  postFormData.status = 0

  dialogVisible.value = true
  nextTick(() => {
    postFormRef.value?.clearValidate()
  })
}

// 打开修改弹窗
const handleEdit = (row: SysPost) => {
  dialogTitle.value = '修改岗位'
  isEdit.value = true

  Object.assign(postFormData, row)
  dialogVisible.value = true
  nextTick(() => {
    postFormRef.value?.clearValidate()
  })
}

// 提交表单保存
const submitForm = async () => {
  if (!postFormRef.value) return
  await postFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value && typeof postFormData.postId === 'number') {
        await updatePost(postFormData)
        ElMessage.success('修改成功')
      } else {
        await addPost(postFormData)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      fetchPostData()
    } catch (error) {
      // 错误已被统一提示
    }
  })
}

// 逻辑删除岗位
const handleDelete = (row: SysPost) => {
  ElMessageBox.confirm(`确认删除岗位【${row.postName}】(${row.postCode}) 吗？`, '安全警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    if (!row.postId) return
    await deletePost(row.postId)
    ElMessage.success('删除成功')
    fetchPostData()
  }).catch(() => {})
}

onMounted(() => {
  fetchPostData()
})
</script>

<template>
  <div class="post-container">
    <!-- 1. 顶部检索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchQuery" class="search-form">
        <el-form-item label="岗位编码">
          <el-input
              v-model.trim="searchQuery.postCode"
              placeholder="请输入岗位编码"
              clearable
              @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="岗位名称">
          <el-input
              v-model.trim="searchQuery.postName"
              placeholder="请输入岗位名称"
              clearable
              @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
              v-model="searchQuery.status"
              placeholder="岗位状态"
              clearable
              style="width: 120px"
          >
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 主格数据与工具栏 -->
    <el-card class="table-card" shadow="never">
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增岗位</el-button>
      </div>

      <el-table
          v-loading="loading"
          :data="tableData"
          row-key="postId"
          border
          style="width: 100%"
      >
        <el-table-column prop="postId" label="岗位编号" width="90" align="center" />
        <el-table-column prop="postCode" label="岗位编码" min-width="150" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.postCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="postName" label="岗位名称" min-width="160" align="center" />
        <el-table-column prop="postSort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" align="center" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 3. 新增/修改 模态对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="500px"
        destroy-on-close
    >
      <el-form
          ref="postFormRef"
          :model="postFormData"
          :rules="formRules"
          label-width="90px"
      >
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model.trim="postFormData.postName" placeholder="例如：董事长、技术总监" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <!-- 修改时禁用编码，防止破坏下游业务契约 -->
          <el-input
              v-model.trim="postFormData.postCode"
              placeholder="例如：ceo、tech_lead"
              :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="显示排序" prop="postSort">
          <el-input-number
              v-model="postFormData.postSort"
              :min="0"
              :max="9999"
              controls-position="right"
          />
        </el-form-item>
        <el-form-item label="岗位状态" prop="status">
          <el-radio-group v-model="postFormData.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.post-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  height: calc(100vh - 32px);
  box-sizing: border-box;

  .search-card {
    :deep(.el-card__body) {
      padding-bottom: 2px; // 压缩搜索栏底部留白
    }
  }

  .table-card {
    flex: 1;
    display: flex;
    flex-direction: column;

    .toolbar {
      margin-bottom: 12px;
    }
  }
}
</style>