<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getFlowNodeList,
  addFlowNode,
  updateFlowNode,
  deleteFlowNode,
  getAllPostList,
  type FlowNodeConfig,
  type PostOption
} from '@/api/FlowNodeConfig'

const visible = ref(false)
const loading = ref(false)
const currentFlowName = ref('')
const currentDefId = ref<number | undefined>(undefined)

const nodeList = ref<FlowNodeConfig[]>([])
const postOptions = ref<PostOption[]>([])

// 节点新增/编辑表单弹窗
const nodeDialogVisible = ref(false)
const isEditNode = ref(false)
const nodeFormRef = ref<FormInstance>()

const nodeFormData = reactive<FlowNodeConfig>({
  nodeId: undefined,
  defId: 0,
  nodeName: '',
  nodeOrder: 1,
  postId: null,
  approveType: 0
})

const nodeRules: FormRules = {
  nodeName: [
    { required: true, message: '请输入节点名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  nodeOrder: [{ required: true, message: '请配置执行序号', trigger: 'blur' }],
  postId: [{ required: true, message: '请选择负责审批的岗位', trigger: 'change' }],
  approveType: [{ required: true, message: '请选择审批方式', trigger: 'change' }]
}

// 供父页面调用的入口方法
const open = async (def: { defId: number; flowName: string }) => {
  currentDefId.value = def.defId
  currentFlowName.value = def.flowName
  visible.value = true

  await Promise.all([fetchNodeList(), fetchPosts()])
}

const fetchNodeList = async () => {
  if (!currentDefId.value) return
  loading.value = true
  try {
    const res = await getFlowNodeList(currentDefId.value)
    nodeList.value = res || []
  } finally {
    loading.value = false
  }
}

const fetchPosts = async () => {
  if (postOptions.value.length === 0) {
    try {
      const res = await getAllPostList()
      postOptions.value = res || []
    } catch (e) {}
  }
}

// 新增节点
const handleAddNode = () => {
  isEditNode.value = false
  nodeFormData.nodeId = undefined
  nodeFormData.defId = currentDefId.value!
  nodeFormData.nodeName = ''
  nodeFormData.postId = null
  nodeFormData.approveType = 0

  // 自动推荐序号：当前最大序号 + 1
  const maxOrder = nodeList.value.reduce((max, cur) => Math.max(max, cur.nodeOrder), 0)
  nodeFormData.nodeOrder = maxOrder + 1

  nodeDialogVisible.value = true
  nextTick(() => {
    nodeFormRef.value?.clearValidate()
  })
}

// 编辑节点
const handleEditNode = (row: FlowNodeConfig) => {
  isEditNode.value = true
  Object.assign(nodeFormData, row)
  nodeDialogVisible.value = true
  nextTick(() => {
    nodeFormRef.value?.clearValidate()
  })
}

// 提交节点表单
const submitNodeForm = async () => {
  if (!nodeFormRef.value) return
  await nodeFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEditNode.value) {
        await updateFlowNode(nodeFormData)
        ElMessage.success('节点修改成功')
      } else {
        await addFlowNode(nodeFormData)
        ElMessage.success('节点新增成功')
      }
      nodeDialogVisible.value = false
      fetchNodeList()
    } catch (e) {}
  })
}

// 删除节点
const handleDeleteNode = (row: FlowNodeConfig) => {
  ElMessageBox.confirm(`确认删除节点【${row.nodeName}】吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    if (!row.nodeId) return
    await deleteFlowNode(row.nodeId)
    ElMessage.success('删除成功')
    fetchNodeList()
  }).catch(() => {})
}

defineExpose({ open })
</script>

<template>
  <el-drawer
      v-model="visible"
      :title="`流程节点配置 - [${currentFlowName}]`"
      size="760px"
      destroy-on-close
  >
    <div class="drawer-header-toolbar">
      <el-button type="primary" @click="handleAddNode">添加审批节点</el-button>
      <span class="sub-tip">审批实例流转时，将按【执行序号】由小到大严格推进</span>
    </div>

    <!-- 流程节点链路表格 -->
    <el-table v-loading="loading" :data="nodeList" border stripe style="margin-top: 16px;">
      <el-table-column prop="nodeOrder" label="序号" width="70" align="center">
        <template #default="{ row }">
          <el-tag type="info" effect="dark" round>{{ row.nodeOrder }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="nodeName" label="节点名称" min-width="140" align="center" />
      <el-table-column prop="postName" label="审批岗位" min-width="140" align="center">
        <template #default="{ row }">
          <el-tag type="success">{{ row.postName || '未分配岗位' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="approveType" label="审批规则" width="130" align="center">
        <template #default="{ row }">
          <el-tag :type="row.approveType === 1 ? 'warning' : 'primary'">
            {{ row.approveType === 1 ? '会签 (全员同意)' : '或签 (一人同意)' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" align="center" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEditNode(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDeleteNode(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 节点增改的小弹窗 -->
    <el-dialog
        v-model="nodeDialogVisible"
        :title="isEditNode ? '编辑审批节点' : '添加审批节点'"
        width="460px"
        append-to-body
    >
      <el-form ref="nodeFormRef" :model="nodeFormData" :rules="nodeRules" label-width="95px">
        <el-form-item label="节点名称" prop="nodeName">
          <el-input v-model.trim="nodeFormData.nodeName" placeholder="例如：部门主管审批、财务审核" />
        </el-form-item>
        <el-form-item label="执行序号" prop="nodeOrder">
          <el-input-number v-model="nodeFormData.nodeOrder" :min="1" :max="99" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="审批岗位" prop="postId">
          <el-select v-model="nodeFormData.postId" placeholder="请选择审批岗位" style="width: 100%;">
            <el-option
                v-for="item in postOptions"
                :key="item.postId"
                :label="item.postName"
                :value="item.postId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审批规则" prop="approveType">
          <el-radio-group v-model="nodeFormData.approveType">
            <el-radio :label="0">或签（任一通过）</el-radio>
            <el-radio :label="1">会签（全员通过）</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="nodeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitNodeForm">确定</el-button>
      </template>
    </el-dialog>
  </el-drawer>
</template>

<style scoped>
.drawer-header-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}
.sub-tip {
  font-size: 13px;
  color: #909399;
}
</style>