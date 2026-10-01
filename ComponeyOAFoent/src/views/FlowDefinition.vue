<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getFlowDefinitionList,
  addFlowDefinition,
  updateFlowDefinition,
  deleteFlowDefinition,
  type FlowDefinition
} from '@/api/FlowDefinition'

// --- 1. 数据与状态管理 ---
const loading = ref(false)
const rawFlowList = ref<FlowDefinition[]>([]) // 原始列表缓存
const tableData = ref<FlowDefinition[]>([])    // 表格展示数据

// 搜索条件
const searchQuery = reactive({
  flowCode: '',
  flowName: '',
  status: ''
})

// 从后端拉取流程定义列表
const fetchFlowData = async () => {
  loading.value = true
  try {
    const list = await getFlowDefinitionList()
    rawFlowList.value = list || []
    filterTableData()
  } catch (error) {
    // 错误已被 request.ts 统一拦截提示
  } finally {
    loading.value = false
  }
}

// 内存复合过滤
const filterTableData = () => {
  tableData.value = rawFlowList.value.filter((item) => {
    const matchCode = !searchQuery.flowCode || item.flowCode.toLowerCase().includes(searchQuery.flowCode.trim().toLowerCase())
    const matchName = !searchQuery.flowName || item.flowName.includes(searchQuery.flowName.trim())
    const matchStatus = searchQuery.status === '' || String(item.status) === searchQuery.status

    return matchCode && matchName && matchStatus
  })
}

const handleSearch = () => {
  filterTableData()
}

const handleReset = () => {
  searchQuery.flowCode = ''
  searchQuery.flowName = ''
  searchQuery.status = ''
  filterTableData()
}

// --- 2. 增删改模态框逻辑 ---
const dialogVisible = ref(false)
const dialogTitle = ref('')
const flowFormRef = ref<FormInstance>()
const isEdit = ref(false)

const flowFormData = reactive<FlowDefinition>({
  defId: undefined,
  flowCode: '',
  flowName: '',
  status: 0,
  remark: ''
})

// 表单校验规则
const formRules: FormRules = {
  flowName: [
    { required: true, message: '请输入流程名称', trigger: 'blur' },
    { whitespace: true, message: '流程名称不能为全空格', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  flowCode: [
    { required: true, message: '请输入流程编码', trigger: 'blur' },
    { whitespace: true, message: '流程编码不能为全空格', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_-]+$/,
      message: '编码只能由英文字母、数字、下划线或中划线组成',
      trigger: 'blur'
    }
  ]
}

// 打开新增弹窗
const handleAdd = () => {
  dialogTitle.value = '新增流程定义'
  isEdit.value = false

  flowFormData.defId = undefined
  flowFormData.flowCode = ''
  flowFormData.flowName = ''
  flowFormData.status = 0
  flowFormData.remark = ''

  dialogVisible.value = true
  nextTick(() => {
    flowFormRef.value?.clearValidate()
  })
}

// 打开修改弹窗
const handleEdit = (row: FlowDefinition) => {
  dialogTitle.value = '修改流程定义'
  isEdit.value = true

  Object.assign(flowFormData, row)
  dialogVisible.value = true
  nextTick(() => {
    flowFormRef.value?.clearValidate()
  })
}

// 提交保存
const submitForm = async () => {
  if (!flowFormRef.value) return
  await flowFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value && typeof flowFormData.defId === 'number') {
        await updateFlowDefinition(flowFormData)
        ElMessage.success('修改成功')
      } else {
        await addFlowDefinition(flowFormData)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      fetchFlowData()
    } catch (error) {
      // 错误已被统一提示
    }
  })
}

// 逻辑删除流程定义
const handleDelete = (row: FlowDefinition) => {
  ElMessageBox.confirm(`确认删除流程【${row.flowName}】(${row.flowCode}) 吗？`, '安全警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    if (!row.defId) return
    await deleteFlowDefinition(row.defId)
    ElMessage.success('删除成功')
    fetchFlowData()
  }).catch(() => {})
}

// 节点配置弹窗预留入口
const handleConfigNodes = (row: FlowDefinition) => {
  ElMessage.info(`进入【${row.flowName}】的节点流转配置（下一步即将对接 flow_node_config）`)
}

onMounted(() => {
  fetchFlowData()
})
</script>

<template>
  <div class="flow-container">
    <!-- 1. 顶部检索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchQuery" class="search-form">
        <el-form-item label="流程编码">
          <el-input
              v-model.trim="searchQuery.flowCode"
              placeholder="请输入流程编码"
              clearable
              @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="流程名称">
          <el-input
              v-model.trim="searchQuery.flowName"
              placeholder="请输入流程名称"
              clearable
              @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="流程状态">
          <el-select
              v-model="searchQuery.status"
              placeholder="状态"
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
        <el-button type="primary" @click="handleAdd">新增流程定义</el-button>
      </div>

      <el-table
          v-loading="loading"
          :data="tableData"
          row-key="defId"
          border
          style="width: 100%"
      >
        <el-table-column prop="defId" label="流程ID" width="90" align="center" />
        <el-table-column prop="flowCode" label="流程唯一编码" min-width="150" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.flowCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="flowName" label="流程名称" min-width="160" align="center" />
        <el-table-column prop="remark" label="说明描述" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" align="center" />
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="warning" @click="handleConfigNodes(row)">配置节点</el-button>
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
        width="520px"
        destroy-on-close
    >
      <el-form
          ref="flowFormRef"
          :model="flowFormData"
          :rules="formRules"
          label-width="100px"
      >
        <el-form-item label="流程名称" prop="flowName">
          <el-input v-model.trim="flowFormData.flowName" placeholder="例如：员工请假审批流、采购报销流程" />
        </el-form-item>
        <el-form-item label="流程编码" prop="flowCode">
          <el-input
              v-model.trim="flowFormData.flowCode"
              placeholder="例如：LEAVE_APPLY、EXPENSE"
              :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="流程状态" prop="status">
          <el-radio-group v-model="flowFormData.status">
            <el-radio :label="0">正常启用</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="说明描述" prop="remark">
          <el-input
              v-model="flowFormData.remark"
              type="textarea"
              :rows="3"
              placeholder="请输入流程的用途和流转注意事项"
          />
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
.flow-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 16px;
  height: calc(100vh - 32px);
  box-sizing: border-box;

  .search-card {
    :deep(.el-card__body) {
      padding-bottom: 2px;
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