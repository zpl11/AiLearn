<template>
  <div class="operlog-container">
    <!-- 1. 顶部检索条件卡片 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" ref="queryFormRef" class="search-form">
        <el-form-item label="系统模块" prop="title">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入系统模块"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="操作人员" prop="operName">
          <el-input
            v-model="queryParams.operName"
            placeholder="请输入操作人员"
            clearable
            style="width: 180px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="业务类型" prop="businessType">
          <el-select
            v-model="queryParams.businessType"
            placeholder="操作类型"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="item in businessTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="操作状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="状态"
            clearable
            style="width: 130px"
          >
            <el-option label="正常" :value="0" />
            <el-option label="异常" :value="1" />
          </el-select>
        </el-form-item>

        <el-form-item label="操作时间">
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
            title="确认清空所有操作审计日志吗？清空后数据无法恢复！"
            confirm-button-text="确定清空"
            cancel-button-text="取消"
            confirm-button-type="danger"
            @confirm="handleClean"
          >
            <template #reference>
              <el-button type="warning" plain :icon="WarnTriangleFilled">
                清空全部日志
              </el-button>
            </template>
          </el-popconfirm>
        </div>
        <div class="toolbar-right">
          <el-tooltip content="刷新表格" placement="top">
            <el-button circle :icon="RefreshRight" @click="getList" />
          </el-tooltip>
        </div>
      </div>

      <!-- 日志列表表格 -->
      <el-table
        v-loading="loading"
        :data="logList"
        row-key="operId"
        border
        stripe
        @selection-change="handleSelectionChange"
        class="custom-table"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="日志编号" prop="operId" width="90" align="center" />
        <el-table-column label="系统模块" prop="title" min-width="120" show-overflow-tooltip />

        <el-table-column label="操作类型" prop="businessType" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getBusinessTypeTagType(row.businessType)" effect="light">
              {{ getBusinessTypeLabel(row.businessType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="请求方式" prop="requestMethod" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getRequestMethodTag(row.requestMethod)" effect="plain" size="small">
              {{ row.requestMethod || 'N/A' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作人员" prop="operName" width="120" align="center" />
        <el-table-column label="主机IP" prop="operIp" width="130" align="center" />

        <el-table-column label="操作状态" prop="status" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" effect="dark" size="small">
              {{ row.status === 0 ? '正常' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="消耗耗时" prop="costTime" width="100" align="center">
          <template #default="{ row }">
            <span :class="getCostTimeClass(row.costTime)">{{ row.costTime }} ms</span>
          </template>
        </el-table-column>

        <el-table-column label="操作时间" prop="operTime" width="170" align="center" />

        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="View" @click="handleDetail(row)">
              详情
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

    <!-- 3. 日志详情弹窗对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="操作审计日志详细信息"
      width="820px"
      append-to-body
      destroy-on-close
      class="detail-dialog"
    >
      <el-descriptions :column="2" border size="default">
        <el-descriptions-item label="操作模块">
          {{ currentDetail.title }}
        </el-descriptions-item>
        <el-descriptions-item label="登录信息">
          {{ currentDetail.operName }} / {{ currentDetail.operIp }}
        </el-descriptions-item>
        <el-descriptions-item label="请求方式">
          <el-tag size="small">{{ currentDetail.requestMethod }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="业务类型">
          <el-tag :type="getBusinessTypeTagType(currentDetail.businessType)" size="small">
            {{ getBusinessTypeLabel(currentDetail.businessType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求地址" :span="2">
          <code>{{ currentDetail.operUrl }}</code>
        </el-descriptions-item>
        <el-descriptions-item label="调用方法" :span="2">
          <code class="code-method">{{ currentDetail.method }}</code>
        </el-descriptions-item>
        <el-descriptions-item label="操作状态">
          <el-tag :type="currentDetail.status === 0 ? 'success' : 'danger'" size="small">
            {{ currentDetail.status === 0 ? '正常' : '异常' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="消耗耗时">
          <span :class="getCostTimeClass(currentDetail.costTime)">{{ currentDetail.costTime }} 毫秒</span>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">
          {{ currentDetail.operTime }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 请求参数代码框 -->
      <div class="json-box-section">
        <div class="box-title">
          <span>请求参数</span>
          <el-button link type="primary" size="small" @click="copyText(currentDetail.operParam)">
            复制入参
          </el-button>
        </div>
        <pre class="code-snippet">{{ formatJson(currentDetail.operParam) }}</pre>
      </div>

      <!-- 响应结果代码框 -->
      <div class="json-box-section" v-if="currentDetail.status === 0">
        <div class="box-title">
          <span>返回结果</span>
          <el-button link type="primary" size="small" @click="copyText(currentDetail.jsonResult)">
            复制出参
          </el-button>
        </div>
        <pre class="code-snippet">{{ formatJson(currentDetail.jsonResult) }}</pre>
      </div>

      <!-- 异常信息警告框 -->
      <div class="json-box-section error-section" v-if="currentDetail.status === 1">
        <div class="box-title text-danger">异常堆栈信息</div>
        <pre class="code-snippet error-snippet">{{ currentDetail.errorMsg || '无详细异常堆栈' }}</pre>
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
  getOperLogList,
  deleteOperLog,
  cleanOperLog,
  type SysOperLog,
  type OperLogQuery
} from '@/api/operLog'

// 业务类型选项字典
const businessTypeOptions = [
  { label: '其它', value: 0 },
  { label: '新增', value: 1 },
  { label: '修改', value: 2 },
  { label: '删除', value: 3 },
  { label: '授权', value: 4 },
  { label: '导出', value: 5 },
  { label: '导入', value: 6 },
  { label: '清空', value: 7 }
]

const queryFormRef = ref()
const loading = ref(false)
const logList = ref<SysOperLog[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const dateRange = ref<[string, string] | []>([])

// 查询参数
const queryParams = reactive<OperLogQuery>({
  pageNum: 1,
  pageSize: 10,
  title: '',
  operName: '',
  businessType: undefined,
  status: undefined
})

// 详情弹窗相关
const detailVisible = ref(false)
const currentDetail = ref<SysOperLog>({
  operId: 0,
  title: '',
  businessType: 0,
  method: '',
  requestMethod: '',
  operName: '',
  operUrl: '',
  operIp: '',
  operParam: '',
  jsonResult: '',
  status: 0,
  errorMsg: '',
  costTime: 0,
  operTime: ''
})

// 加载日志数据列表
const getList = async () => {
  loading.value = true
  try {
    const params: OperLogQuery = { ...queryParams }
    if (dateRange.value && dateRange.value.length === 2) {
      params.beginTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res: any = await getOperLogList(params)
    // 兼容后端直接返回 PageResult 或拦截器返回 res
    if (res && res.list !== undefined) {
      logList.value = res.list || []
      total.value = res.total || 0
    } else if (Array.isArray(res)) {
      logList.value = res
      total.value = res.length
    }
  } catch (error) {
    console.error('获取日志列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  dateRange.value = []
  if (queryFormRef.value) {
    queryFormRef.value.resetFields()
  }
  queryParams.beginTime = undefined
  queryParams.endTime = undefined
  handleQuery()
}

// 表格多选事件
const handleSelectionChange = (selection: SysOperLog[]) => {
  selectedIds.value = selection.map(item => item.operId)
}

// 详情点击
const handleDetail = (row: SysOperLog) => {
  currentDetail.value = { ...row }
  detailVisible.value = true
}

// 单条删除
const handleDelete = (row: SysOperLog) => {
  ElMessageBox.confirm(`是否确认删除日志编号为 "${row.operId}" 的记录?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteOperLog(row.operId)
    ElMessage.success('删除成功')
    getList()
  }).catch(() => {})
}

// 批量删除
const handleBatchDelete = () => {
  if (selectedIds.value.length === 0) return
  ElMessageBox.confirm(`是否确认删除选中的 ${selectedIds.value.length} 条操作日志?`, '批量删除警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteOperLog(selectedIds.value.join(','))
    ElMessage.success('批量删除成功')
    getList()
  }).catch(() => {})
}

// 清空全部日志
const handleClean = async () => {
  try {
    await cleanOperLog()
    ElMessage.success('清空日志成功')
    getList()
  } catch (e) {
    console.error(e)
  }
}

// 分页切换
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getList()
}

// 辅助样式方法
const getBusinessTypeLabel = (type: number) => {
  const target = businessTypeOptions.find(item => item.value === type)
  return target ? target.label : '其它'
}

const getBusinessTypeTagType = (type: number) => {
  switch (type) {
    case 1: return 'success'  // 新增
    case 2: return 'warning'  // 修改
    case 3: return 'danger'   // 删除
    case 4: return 'primary'  // 授权
    case 5: return 'info'     // 导出
    case 6: return 'warning'  // 导入
    case 7: return 'danger'   // 清空
    default: return 'info'
  }
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

const getCostTimeClass = (cost: number) => {
  if (cost < 500) return 'cost-fast'
  if (cost < 2000) return 'cost-mid'
  return 'cost-slow'
}

// JSON 格式化工具
const formatJson = (content?: string) => {
  if (!content) return '无'
  try {
    const obj = JSON.parse(content)
    return JSON.stringify(obj, null, 2)
  } catch {
    return content
  }
}

// 复制文本
const copyText = (text?: string) => {
  if (!text) {
    ElMessage.info('内容为空')
    return
  }
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.operlog-container {
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

  .cost-fast {
    color: #67c23a;
    font-weight: 500;
  }
  .cost-mid {
    color: #e6a23c;
    font-weight: 500;
  }
  .cost-slow {
    color: #f56c6c;
    font-weight: 600;
  }

  .code-method {
    font-family: Consolas, Monaco, monospace;
    font-size: 13px;
    color: #409eff;
    word-break: break-all;
  }

  .json-box-section {
    margin-top: 14px;

    .box-title {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 6px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      &.text-danger {
        color: #f56c6c;
      }
    }

    .code-snippet {
      margin: 0;
      padding: 12px;
      background-color: #f8f9fa;
      border: 1px solid #ebeef5;
      border-radius: 6px;
      max-height: 240px;
      overflow-y: auto;
      font-family: Consolas, Monaco, monospace;
      font-size: 12px;
      line-height: 1.5;
      color: #333333;
      white-space: pre-wrap;
      word-break: break-all;
    }

    &.error-section .error-snippet {
      background-color: #fef0f0;
      border-color: #fde2e2;
      color: #f56c6c;
    }
  }
}
</style>
