<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import {
  getFlowInstanceList,
  getFlowInstanceMyInitiate,
  deleteFlowInstance,
  type FlowInstanceVO,
  getAvailableFlowDefs,
  startFlowInstance,
  type StartInstanceDTO
} from '@/api/FlowInstance'

// --- 1.状态与数据 ---
const loading = ref(false)
const rawInstanceList = ref<FlowInstanceVO[]>([])
const tableData = ref<FlowInstanceVO[]>([])

// 搜索条件
const searchQuery = reactive({
  flowCode: '',
  status: '',
  initiatorId: ''
})

// 页面模式：all=全部实例(管理员), my=我发起的(普通用户)
const pageMode = ref<'all' | 'my'>('all')

// 获取列表数据
const fetchInstanceData = async () => {
  loading.value = true
  try {
    let list: FlowInstanceVO[] = []
    if(pageMode.value === 'my'){
      // 实际项目initiatorId从登录store拿，这里api参数预留[cite: 15]
      list = await getFlowInstanceMyInitiate()
    }else{
      list = await getFlowInstanceList()
    }
    rawInstanceList.value = list || []
    filterTableData()
  } catch (error) {
    // request.ts统一拦截错误提示[cite: 15]
  } finally {
    loading.value = false
  }
}

// 内存过滤
const filterTableData = () => {
  tableData.value = rawInstanceList.value.filter(item=>{
    const matchCode = !searchQuery.flowCode || item.flowCode?.toLowerCase().includes(searchQuery.flowCode.trim().toLowerCase())
    const matchStatus = searchQuery.status === '' || String(item.status) === searchQuery.status
    return matchCode && matchStatus
  })
}

const handleSearch = ()=>{
  filterTableData()
}

const handleReset = ()=>{
  searchQuery.flowCode = ''
  searchQuery.status = ''
  filterTableData()
}

// 切换查看模式：全部 / 我发起的
const changeMode = (mode:'all'|'my')=>{
  pageMode.value = mode
  fetchInstanceData()
}

// 删除实例（逻辑删除）
const handleDelete = (row:FlowInstanceVO)=>{
  ElMessageBox.confirm(`确认要逻辑删除该审批实例【${row.title}】吗？删除后不再展示。`,'警告',{
    type:'warning'
  }).then(async ()=>{
    if(!row.instanceId) return
    await deleteFlowInstance(row.instanceId)
    ElMessage.success('删除成功')
    fetchInstanceData()
  }).catch(()=>{})
}


// ==================== 2. 查看详情逻辑 ====================

const detailDialogVisible = ref(false)
const currentInstanceTitle = ref('')
const parsedDetailData = ref<Record<string, any>>({}) // 用来存放解析后的 JSON 表单数据

const handleViewDetail = (row: FlowInstanceVO) => {
  currentInstanceTitle.value = row.title || '审批详情'

  // 尝试解析 JSON 格式的 formData
  try {
    parsedDetailData.value = row.formData ? JSON.parse(row.formData) : {}
  } catch (e) {
    // 如果不是合法的 JSON，就直接作为一个整体字符串放进去展示
    parsedDetailData.value = { '原始数据': row.formData }
  }

  detailDialogVisible.value = true
}


// ==================== 3. 发起审批逻辑 ====================

const applyDrawerVisible = ref(false)
const applyFormRef = ref<FormInstance>()
const availableFlows = ref<{ defId: number; flowName: string; flowCode: string }[]>([])

// 发起审批的表单数据 (模拟组装 DTO)
const applyFormData = reactive({
  defId: undefined as number | undefined,
  title: '',
  // 模拟 JSON 结构，实际业务应依据 defId 动态渲染对应表单并组装
  formDataJson: '{\n  "申请理由": "请假",\n  "天数": 1\n}'
})

// 校验规则
const applyRules = {
  defId: [{ required: true, message: '请选择审批流程', trigger: 'change' }],
  title: [{ required: true, message: '请输入审批标题', trigger: 'blur' }],
  formDataJson: [{ required: true, message: '表单数据不能为空', trigger: 'blur' }]
}

// 打开“发起申请”抽屉
const handleApply = async () => {
  applyDrawerVisible.value = true

  // 拉取下拉框可用的流程定义（接口已脱壳，直接拿数组）
  try {
    const res = await getAvailableFlowDefs()
    availableFlows.value = res || []
  } catch (error) {
    // 错误处理由 request.ts 接管
  }
}

// 提交发起申请
const submitApply = async () => {
  if (!applyFormRef.value) return
  await applyFormRef.value.validate(async (valid) => {
    if (!valid) return

    // 校验前端填写的 JSON 格式是否正确
    try {
      JSON.parse(applyFormData.formDataJson)
    } catch (e) {
      ElMessage.error('表单数据必须是合法的 JSON 格式')
      return
    }

    const dto: StartInstanceDTO = {
      defId: applyFormData.defId!,
      title: applyFormData.title,
      formData: applyFormData.formDataJson
    }

    try {
      await startFlowInstance(dto)
      ElMessage.success('审批发起成功！')
      applyDrawerVisible.value = false

      // 提交成功后，默认切换到“我发起的”列表进行刷新查看
      pageMode.value = 'my'
      fetchInstanceData()
    } catch (error) {
      // 错误已拦截
    }
  })
}

onMounted(()=>{
  fetchInstanceData()
})
</script>

<template>
  <div class="flow-instance-container">
    <!-- 顶部检索 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchQuery" class="search-form">
        <el-form-item label="流程编码">
          <el-input
              v-model.trim="searchQuery.flowCode"
              placeholder="流程编码"
              clearable
              @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="流程状态">
          <el-select
              v-model="searchQuery.status"
              placeholder="状态筛选"
              clearable
              style="width:140px"
          >
            <el-option label="处理中" value="0"/>
            <el-option label="已通过" value="1"/>
            <el-option label="已驳回" value="2"/>
            <el-option label="已撤销" value="3"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button :type="pageMode==='all'?'primary':'default'" @click="changeMode('all')">全部审批实例</el-button>
          <el-button :type="pageMode==='my'?'primary':'default'" @click="changeMode('my')">我发起的</el-button>
        </div>
        <div class="toolbar-right">
          <!-- ✅ 发起审批 按钮 -->
          <el-button type="success" @click="handleApply">发起审批</el-button>
        </div>
      </div>

      <el-table
          v-loading="loading"
          :data="tableData"
          border
          style="width:100%"
      >
        <el-table-column prop="instanceId" label="实例ID" width="90" align="center"/>
        <el-table-column prop="flowCode" label="流程编码" min-width="140" align="center">
          <template #default="{row}">
            <el-tag type="info" effect="plain">{{row.flowCode}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="flowName" label="流程模板名称" min-width="140" align="center"/>
        <el-table-column prop="title" label="审批标题" min-width="180" show-overflow-tooltip/>
        <el-table-column prop="initiatorNickName" label="发起人" width="120" align="center"/>
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{row}">
            <el-tag
                :type="row.status===0?'warning':row.status===1?'success':row.status===2?'danger':'info'"
            >
              {{
                row.status===0?'处理中':
                    row.status===1?'已通过':
                        row.status===2?'已驳回':'已撤销'
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentOrder" label="当前节点序号" width="130" align="center"/>
        <el-table-column prop="createTime" label="发起时间" min-width="170" align="center"/>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{row}">
            <el-button link type="primary" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- ==================== 发起审批 抽屉 ==================== -->
    <el-drawer
        v-model="applyDrawerVisible"
        title="发起审批申请"
        size="500px"
        destroy-on-close
    >
      <div class="drawer-content">
        <el-form ref="applyFormRef" :model="applyFormData" :rules="applyRules" label-width="90px">

          <el-form-item label="审批流程" prop="defId">
            <el-select v-model="applyFormData.defId" placeholder="请选择要发起的流程" style="width: 100%;">
              <el-option
                  v-for="item in availableFlows"
                  :key="item.defId"
                  :label="item.flowName"
                  :value="item.defId"
              >
                <span style="float: left">{{ item.flowName }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.flowCode }}</span>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="审批标题" prop="title">
            <el-input v-model="applyFormData.title" placeholder="如：张三提交的请假申请" />
          </el-form-item>

          <!-- 模拟业务表单内容的录入 -->
          <el-form-item label="表单数据" prop="formDataJson">
            <template #label>
              表单数据<br/>
              <span style="font-size: 12px; color: #909399;">(JSON格式)</span>
            </template>
            <el-input
                v-model="applyFormData.formDataJson"
                type="textarea"
                :rows="8"
                placeholder='请输入标准的JSON字符串，如：{"天数": 3, "事由": "事假"}'
            />
          </el-form-item>

        </el-form>
      </div>

      <template #footer>
        <div style="flex: auto">
          <el-button @click="applyDrawerVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApply">提交申请</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- ✅ ==================== 查看详情 弹窗 ==================== -->
    <el-dialog
        v-model="detailDialogVisible"
        :title="currentInstanceTitle"
        width="600px"
        destroy-on-close
    >
      <div class="detail-snapshot">
        <el-descriptions title="申请表单内容快照" :column="2" border>
          <!-- 动态遍历解析出来的 JSON 属性并展示 -->
          <el-descriptions-item
              v-for="(val, key) in parsedDetailData"
              :key="key"
              :label="key"
          >
            {{ val }}
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="Object.keys(parsedDetailData).length === 0" style="color: #909399; text-align: center; margin-top: 20px;">
          暂无表单快照数据
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="detailDialogVisible = false">关 闭</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped lang="scss">
.flow-instance-container{
  display:flex;
  flex-direction:column;
  gap:16px;
  padding:16px;
  height: calc(100vh - 32px);
  box-sizing:border-box;

  .search-card{
    :deep(.el-card__body){
      padding-bottom:4px;
    }
  }
  .table-card{
    flex:1;
    display:flex;
    flex-direction:column;
    .toolbar{
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom:12px;
    }
  }
}

.drawer-content {
  padding: 20px;
}

/* 详情面板样式 */
.detail-snapshot {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 4px;
  max-height: 350px;
  overflow-y: auto;
}
</style>