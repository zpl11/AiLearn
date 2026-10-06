<template>
  <div class="errorlog-container">
    <!-- 1. 顶部检索条件卡片 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryFormRef" class="search-form">
        <el-form-item label="异常名称" prop="errorName">
          <el-input
            v-model="queryParams.errorName"
            placeholder="如: NullPointerException"
            clearable
            style="width: 220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="请求URL" prop="requestUri">
          <el-input
            v-model="queryParams.requestUri"
            placeholder="如: /system/user/add"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="操作人员" prop="operName">
          <el-input
            v-model="queryParams.operName"
            placeholder="请输入操作人员账号"
            clearable
            style="width: 170px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="发生时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 340px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 2. 数据表格与操作工具栏 -->
    <el-card class="table-card" shadow="never">
      <div class="toolbar-wrapper">
        <div class="toolbar-left">
          <el-button
            type="danger"
            plain
            :icon="Delete"
            :disabled="selectedIds.length === 0"
            @click="handleBatchDelete"
          >
            批量删除 {{ selectedIds.length ? `(${selectedIds.length})` : '' }}
          </el-button>
          <el-popconfirm
            title="确认清空所有异常监控日志吗？此操作不可逆！"
            confirm-button-text="确定清空"
            cancel-button-text="取消"
            confirm-button-type="danger"
            @confirm="handleClean"
          >
            <template #reference>
              <el-button type="warning" plain :icon="WarnTriangleFilled">
                清空全部异常
              </el-button>
            </template>
          </el-popconfirm>
        </div>
        <div class="toolbar-right">
          <el-tooltip content="刷新列表" placement="top">
            <el-button circle :icon="RefreshRight" @click="getList" />
          </el-tooltip>
        </div>
      </div>

      <!-- 异常监控日志表格 -->
      <el-table
        v-loading="loading"
        :data="errorList"
        row-key="errorId"
        border
        stripe
        @selection-change="handleSelectionChange"
        class="custom-table"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="编号" prop="errorId" width="80" align="center" />

        <el-table-column label="请求方式" prop="requestMethod" width="95" align="center">
          <template #default="{ row }">
            <el-tag :type="getRequestMethodTag(row.requestMethod)" effect="plain" size="small">
              {{ row.requestMethod || 'N/A' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="请求URL" prop="requestUri" min-width="170" show-overflow-tooltip>
          <template #default="{ row }">
            <code class="uri-tag">{{ row.requestUri }}</code>
          </template>
        </el-table-column>

        <el-table-column label="异常名称" prop="errorName" min-width="190" show-overflow-tooltip>
          <template #default="{ row }">
            <el-tag type="danger" effect="light" class="error-name-tag">
              {{ formatErrorName(row.errorName) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="错误摘要" prop="errorMessage" min-width="220" show-overflow-tooltip />

        <el-table-column label="操作人员" prop="operName" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small" effect="plain">{{ row.operName || '未登录' }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="发生时间" prop="createTime" width="170" align="center" />

        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleDetail(row)">
              堆栈详情
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页栏 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 3. 异常堆栈详情弹窗对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="系统异常监控与堆栈详情"
      width="860px"
      append-to-body
      destroy-on-close
      class="detail-dialog"
    >
      <el-descriptions :column="2" border size="default">
        <el-descriptions-item label="日志编号">
          {{ currentDetail.errorId }}
        </el-descriptions-item>
        <el-descriptions-item label="发生时间">
          {{ currentDetail.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="请求方式">
          <el-tag size="small">{{ currentDetail.requestMethod }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作人员">
          <el-tag type="info" size="small">{{ currentDetail.operName || '未登录' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">
          <code>{{ currentDetail.requestUri }}</code>
        </el-descriptions-item>
        <el-descriptions-item label="异常类名" :span="2">
          <span class="text-danger font-semibold">{{ currentDetail.errorName }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="异常消息" :span="2">
          <div class="msg-box">{{ currentDetail.errorMessage || '无详细错误摘要' }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 完整错误堆栈代码框 -->
      <div class="stack-box-section">
        <div class="box-title">
          <span>完整错误堆栈 (Stack Trace)</span>
          <el-button link type="primary" size="small" @click="copyStack(currentDetail.errorStack)">
            复制异常堆栈
          </el-button>
        </div>
        <pre class="terminal-stack">{{ currentDetail.errorStack || '无堆栈信息' }}</pre>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Delete,
  WarnTriangleFilled,
  RefreshRight,
  View
} from '@element-plus/icons-vue'
import {
  getErrorLogList,
  deleteErrorLog,
  cleanErrorLog,
  type SysErrorLog,
  type ErrorLogQuery
} from '@/api/errorLog'

const queryFormRef = ref()
const loading = ref(false)
const errorList = ref<SysErrorLog[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const dateRange = ref<[string, string] | []>([])

// 查询参数
const queryParams = reactive<ErrorLogQuery>({
  pageNum: 1,
  pageSize: 10,
  errorName: '',
  requestUri: '',
  operName: ''
})

// 详情弹窗
const detailVisible = ref(false)
const currentDetail = ref<SysErrorLog>({
  errorId: 0,
  requestUri: '',
  requestMethod: '',
  errorName: '',
  errorMessage: '',
  errorStack: '',
  operName: '',
  createTime: ''
})

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const params: ErrorLogQuery = { ...queryParams }
    if (dateRange.value && dateRange.value.length === 2) {
      params.beginTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res: any = await getErrorLogList(params)
    if (res && res.list !== undefined) {
      errorList.value = res.list || []
      total.value = res.total || 0
    } else if (Array.isArray(res)) {
      errorList.value = res
      total.value = res.length
    }
  } catch (error) {
    console.error('获取异常日志失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  dateRange.value = []
  if (queryFormRef.value) {
    queryFormRef.value.resetFields()
  }
  queryParams.beginTime = undefined
  queryParams.endTime = undefined
  handleQuery()
}

// 表格多选
const handleSelectionChange = (selection: SysErrorLog[]) => {
  selectedIds.value = selection.map(item => item.errorId)
}

// 查看详情
const handleDetail = (row: SysErrorLog) => {
  currentDetail.value = { ...row }
  detailVisible.value = true
}

// 单条删除
const handleDelete = (row: SysErrorLog) => {
  ElMessageBox.confirm(`是否确认删除编号为 "${row.errorId}" 的异常监控记录?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteErrorLog(row.errorId)
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) return
  ElMessageBox.confirm(`是否确认删除选中的 ${selectedIds.value.length} 条异常日志?`, '批量删除警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteErrorLog(selectedIds.value.join(','))
    ElMessage.success('批量删除成功')
    getList()
  }).catch(() => {})
}

// 清空全部
const handleClean = async () => {
  try {
    await cleanErrorLog()
    ElMessage.success('已清空全部系统异常日志')
    getList()
  } catch (e) {
    console.error(e)
  }
}

// 分页变化
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getList()
}

// 辅助格式化
const formatErrorName = (name: string) => {
  if (!name) return 'UnknownException'
  const parts = name.split('.')
  return parts[parts.length - 1] || name
}

const getRequestMethodTag = (method: string) => {
  switch ((method || '').toUpperCase()) {
    case 'GET': return 'info'
    case 'POST': return 'success'
    case 'PUT': return 'warning'
    case 'DELETE': return 'danger'
    default: return ''
  }
}

// 复制堆栈
const copyStack = (stack?: string) => {
  if (!stack) {
    ElMessage.info('暂无堆栈信息')
    return
  }
  navigator.clipboard.writeText(stack).then(() => {
    ElMessage.success('异常堆栈已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.errorlog-container {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;

  .search-card {
    border-radius: 8px;
    background-color: #ffffff;
    :deep(.el-card__body) {
      padding: 18px 20px 2px 20px;
    }
  }

  .table-card {
    border-radius: 8px;
    background-color: #ffffff;

    .toolbar-wrapper {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
    }

    .custom-table {
      width: 100%;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      margin-top: 16px;
    }
  }

  .uri-tag {
    font-family: Consolas, Monaco, monospace;
    font-size: 13px;
    color: #409eff;
  }

  .error-name-tag {
    font-weight: 600;
  }

  .text-danger {
    color: #f56c6c;
  }

  .font-semibold {
    font-weight: 600;
  }

  .msg-box {
    color: #e6a23c;
    word-break: break-all;
    font-family: Consolas, Monaco, monospace;
    font-size: 13px;
  }

  .stack-box-section {
    margin-top: 14px;

    .box-title {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 6px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .terminal-stack {
      margin: 0;
      padding: 14px;
      background-color: #1e1e1e;
      border: 1px solid #333333;
      border-radius: 6px;
      max-height: 280px;
      overflow-y: auto;
      font-family: Consolas, Monaco, monospace;
      font-size: 12px;
      line-height: 1.6;
      color: #f87171;
      white-space: pre-wrap;
      word-break: break-all;
    }
  }
}
</style>
