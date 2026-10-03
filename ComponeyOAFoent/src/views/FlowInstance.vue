<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFlowInstanceList, getFlowInstanceMyInitiate, getFlowInstanceInfo, deleteFlowInstance, type FlowInstanceVO } from '@/api/FlowInstance'

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
      // 实际项目initiatorId从登录store拿，这里api参数预留
      list = await getFlowInstanceMyInitiate()
    }else{
      list = await getFlowInstanceList()
    }
    rawInstanceList.value = list || []
    filterTableData()
  } catch (error) {
    // request.ts统一拦截错误提示
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

// 查看详情弹窗/抽屉（这里预留，你后续可以做详情Drawer）
const handleViewDetail = (row:FlowInstanceVO)=>{
  console.log('查看实例详情', row)
  // 后续：打开详情抽屉，展示formData表单快照、审批流转记录
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
        <el-button :type="pageMode==='all'?'primary':'default'" @click="changeMode('all')">全部审批实例</el-button>
        <el-button :type="pageMode==='my'?'primary':'default'" @click="changeMode('my')">我发起的</el-button>
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
      margin-bottom:12px;
    }
  }
}
</style>
