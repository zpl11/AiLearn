<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { getMyTodoList, getMyDoneList, processTask, type FlowTaskVO, type ProcessTaskDTO } from '@/api/FlowTask'

// --- 1. 状态切换与列表数据 ---
const activeTab = ref('todo') // 当前激活的 Tab: 'todo' 待办, 'done' 已办
const loading = ref(false)
const tableData = ref<FlowTaskVO[]>([])

// 搜索条件 (针对已办状态的二次过滤)
const doneStatusFilter = ref<number | undefined>(undefined)

// 拉取列表数据 (根据 activeTab 区分)
const fetchTaskList = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'todo') {
      const res: any = await getMyTodoList()
      tableData.value = Array.isArray(res) ? res : (res?.data || [])
    } else {
      const res: any = await getMyDoneList(doneStatusFilter.value)
      tableData.value = Array.isArray(res) ? res : (res?.data || [])
    }
  } catch (error) {
    // request.ts 统一处理报错
  } finally {
    loading.value = false
  }
}

// Tab 切换钩子
const handleTabChange = () => {
  doneStatusFilter.value = undefined // 切换时重置状态搜索
  fetchTaskList()
}


// --- 2. 审批处理模态框逻辑 ---
const processDialogVisible = ref(false)
const processFormRef = ref<FormInstance>()
const currentTask = ref<FlowTaskVO | null>(null) // 当前正在处理的任务

// 审批参数表单对应 DTO[cite: 1]
const processFormData = reactive<ProcessTaskDTO>({
  taskId: '',
  status: 1, // 默认 1:同意
  comment: ''
})

// 解析用来展示的业务表单（模拟将 JSON 字符串转回对象展示）
const parsedFormData = ref<any>({})

// 点击“审批”按钮打开弹窗
const handleProcess = (row: FlowTaskVO) => {
  currentTask.value = row
  processFormData.taskId = row.taskId
  processFormData.status = 1
  processFormData.comment = ''

  // 尝试解析业务表单 JSON
  try {
    parsedFormData.value = row.formData ? JSON.parse(row.formData) : {}
  } catch (e) {
    parsedFormData.value = {}
  }

  processDialogVisible.value = true
}

// 提交审批结果
const submitProcess = async (targetStatus: number) => {
  if (!processFormData.taskId) return

  // 校验如果是驳回，必须填写意见
  if (targetStatus === 2 && !processFormData.comment.trim()) {
    ElMessage.warning('驳回操作必须填写审批意见')
    return
  }

  processFormData.status = targetStatus

  try {
    // 调用 PUT 请求提交给后端[cite: 1]
    await processTask(processFormData)
    ElMessage.success(targetStatus === 1 ? '审批已同意' : '审批已驳回')
    processDialogVisible.value = false
    fetchTaskList() // 重新刷新列表
  } catch (error) {
    // 报错处理
  }
}


// 点击“查看详情”(已办列表中使用)
const handleView = (row: FlowTaskVO) => {
  ElMessageBox.alert(`审批意见：${row.comment || '无'}`, '审批详情 (只读)', {
    confirmButtonText: '关闭'
  })
}

onMounted(() => {
  fetchTaskList()
})
</script>

<template>
  <div class="flow-task-container">
    <el-card shadow="never" class="main-card">

      <!-- 1. 顶部 Tab 切换 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="custom-tabs">
        <el-tab-pane label="我的待办" name="todo" />
        <el-tab-pane label="我的已办" name="done" />
      </el-tabs>

      <!-- 2. 已办列表专属状态过滤栏 -->
      <div v-if="activeTab === 'done'" class="filter-bar">
        <span class="label">流转状态：</span>
        <el-radio-group v-model="doneStatusFilter" @change="fetchTaskList">
          <el-radio-button :label="undefined">全部</el-radio-button>
          <el-radio-button :label="1">已同意</el-radio-button>
          <el-radio-button :label="2">已驳回</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 3. 数据表格 -->
      <el-table
          v-loading="loading"
          :data="tableData"
          border
          style="width: 100%; margin-top: 16px;"
      >
        <el-table-column prop="title" label="审批标题" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="title-text">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nodeName" label="当前节点" width="150" align="center">
          <template #default="{ row }">
            <el-tag type="primary">{{ row.nodeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="接收时间" width="180" align="center" />

        <!-- 已办特有列 -->
        <template v-if="activeTab === 'done'">
          <el-table-column prop="taskStatus" label="处理结果" width="100" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.taskStatus === 1" type="success">已同意</el-tag>
              <el-tag v-else-if="row.taskStatus === 2" type="danger">已驳回</el-tag>
              <el-tag v-else-if="row.taskStatus === 3" type="info">已作废</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="finishTime" label="处理时间" width="180" align="center" />
        </template>

        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="activeTab === 'todo'" type="primary" link @click="handleProcess(row)">
              处理审批
            </el-button>
            <el-button v-else type="info" link @click="handleView(row)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 4. 审批处理模态框 -->
    <el-dialog
        v-model="processDialogVisible"
        title="审批处理"
        width="600px"
        destroy-on-close
    >
      <!-- 展示业务表单快照 (根据实际业务动态渲染) -->
      <div class="form-snapshot">
        <el-descriptions title="申请表单详情" :column="2" border>
          <el-descriptions-item v-for="(val, key) in parsedFormData" :key="key" :label="key">
            {{ val }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <el-divider />

      <!-- 审批意见填写 -->
      <el-form :model="processFormData" label-width="80px">
        <el-form-item label="审批意见">
          <el-input
              v-model="processFormData.comment"
              type="textarea"
              :rows="3"
              placeholder="请输入审批意见（驳回必填）"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="processDialogVisible = false">取 消</el-button>
          <el-button type="danger" @click="submitProcess(2)">驳 回</el-button>
          <el-button type="success" @click="submitProcess(1)">同 意</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped lang="scss">
.flow-task-container {
  /* 严格使用 border-box 避免 padding/border 撑破容器[cite: 1] */
  box-sizing: border-box;
  padding: 16px;
  height: calc(100vh - 32px);
  display: flex;
  flex-direction: column;

  .main-card {
    flex: 1;
    display: flex;
    flex-direction: column;

    :deep(.el-card__body) {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden; /* 防止内部高度溢出 */
    }
  }

  .custom-tabs {
    margin-bottom: -10px;
  }

  .filter-bar {
    margin-top: 10px;
    display: flex;
    align-items: center;
    .label {
      font-size: 14px;
      color: #606266;
      margin-right: 12px;
    }
  }

  .title-text {
    font-weight: 500;
    color: #303133;
  }

  .form-snapshot {
    background: #f8f9fa;
    padding: 16px;
    border-radius: 4px;
    max-height: 250px;
    overflow-y: auto;
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
  }
}
</style>