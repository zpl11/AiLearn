<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { TreeInstance } from 'element-plus'

// --- 类型定义 ---
interface DeptNode {
  id: number
  label: string
  children?: DeptNode[]
}

interface DeptItem {
  id: number
  deptName: string
  orderNum: number
  status: '0' | '1' // 0: 正常, 1: 停用
  createTime: string
  parentId: number
}

// --- 1. 左侧部门树逻辑 ---
const treeFilterText = ref('')
const deptTreeRef = ref<TreeInstance>()
const currentDeptId = ref<number | null>(null)

// 模拟树形数据
const deptTreeData = ref<DeptNode[]>([
  {
    id: 100,
    label: '某某集团总公司',
    children: [
      {
        id: 101,
        label: '研发中心',
        children: [
          { id: 103, label: '后端研发部' },
          { id: 104, label: '前端研发部' },
          { id: 105, label: '测试运维部' },
        ],
      },
      {
        id: 102,
        label: '市场运营中心',
        children: [
          { id: 106, label: '国内业务部' },
          { id: 107, label: '海外业务部' },
        ],
      },
    ],
  },
])

// 树节点过滤检索
watch(treeFilterText, (val) => {
  deptTreeRef.value?.filter(val)
})

const filterNode = (value: string, data: DeptNode) => {
  if (!value) return true
  return data.label.includes(value)
}

// 点击树节点，联动右侧表格
const handleNodeClick = (data: DeptNode) => {
  currentDeptId.value = data.id
  searchQuery.deptName = '' // 切换节点时可选择重置特定搜索词
  fetchDeptList()
}

// --- 2. 右侧查询表单逻辑 ---
const searchQuery = reactive({
  deptName: '',
  status: '',
})

const handleSearch = () => {
  fetchDeptList()
}

const handleReset = () => {
  searchQuery.deptName = ''
  searchQuery.status = ''
  currentDeptId.value = null
  fetchDeptList()
}

// --- 3. 右侧表格与操作逻辑 ---
const loading = ref(false)
const tableData = ref<DeptItem[]>([
  { id: 101, deptName: '研发中心', orderNum: 1, status: '0', createTime: '2026-01-10 10:00:00', parentId: 100 },
  { id: 102, deptName: '市场运营中心', orderNum: 2, status: '0', createTime: '2026-01-11 11:20:00', parentId: 100 },
  { id: 103, deptName: '后端研发部', orderNum: 1, status: '0', createTime: '2026-02-01 09:30:00', parentId: 101 },
  { id: 104, deptName: '前端研发部', orderNum: 2, status: '0', createTime: '2026-02-01 09:30:00', parentId: 101 },
  { id: 105, deptName: '测试运维部', orderNum: 3, status: '1', createTime: '2026-02-05 14:10:00', parentId: 101 },
  { id: 106, deptName: '国内业务部', orderNum: 1, status: '0', createTime: '2026-03-01 16:00:00', parentId: 102 },
  { id: 107, deptName: '海外业务部', orderNum: 2, status: '0', createTime: '2026-03-02 17:00:00', parentId: 102 },
])

// 模拟请求接口刷新数据
const fetchDeptList = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('数据已刷新')
  }, 200)
}

// 增删改查事件触发
const handleAdd = (row?: DeptItem) => {
  ElMessage.info(`触发新增部门逻辑${row ? `，挂载在 [${row.deptName}] 下` : ''}`)
}

const handleEdit = (row: DeptItem) => {
  ElMessage.info(`修改部门：${row.deptName}`)
}

const handleDelete = (row: DeptItem) => {
  ElMessageBox.confirm(`确认删除部门【${row.deptName}】吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    ElMessage.success('删除成功')
  }).catch(() => {})
}
</script>

<template>
  <div class="dept-layout">
    <!-- 左侧：部门组织树卡片 -->
    <el-card class="tree-card" shadow="never">
      <template #header>
        <span class="card-title">组织架构</span>
      </template>
      <el-input
          v-model="treeFilterText"
          placeholder="输入部门关键字过滤"
          clearable
          class="tree-search-input"
      />
      <div class="tree-wrapper">
        <el-tree
            ref="deptTreeRef"
            :data="deptTreeData"
            node-key="id"
            default-expand-all
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            @node-click="handleNodeClick"
        >
          <template #default="{ node }">
            <span class="tree-node-label">{{ node.label }}</span>
          </template>
        </el-tree>
      </div>
    </el-card>

    <!-- 右侧：搜索表单与表格卡片 -->
    <div class="main-content">
      <!-- 搜索栏 -->
      <el-card class="search-card" shadow="never">
        <el-form :inline="true" :model="searchQuery" class="search-form">
          <el-form-item label="部门名称">
            <el-input
                v-model="searchQuery.deptName"
                placeholder="请输入部门名称"
                clearable
                @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
                v-model="searchQuery.status"
                placeholder="部门状态"
                clearable
                style="width: 130px"
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

      <!-- 列表与操作区 -->
      <el-card class="table-card" shadow="never">
        <div class="toolbar">
          <el-button type="primary" @click="handleAdd()">新增顶级部门</el-button>
        </div>

        <el-table
            v-loading="loading"
            :data="tableData"
            row-key="id"
            border
            style="width: 100%"
        >
          <el-table-column prop="deptName" label="部门名称" min-width="160" />
          <el-table-column prop="orderNum" label="排序" width="80" align="center" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === '0' ? 'success' : 'danger'">
                {{ row.status === '0' ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="170" align="center" />
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleAdd(row)">新增子项</el-button>
              <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<style scoped lang="scss">
.dept-layout {
  display: flex;
  gap: 16px;
  padding: 16px;
  height: calc(100vh - 32px);
  box-sizing: border-box;

  .tree-card {
    width: 280px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;

    .card-title {
      font-weight: 600;
    }

    .tree-search-input {
      margin-bottom: 12px;
    }

    .tree-wrapper {
      overflow-y: auto;
      flex: 1;
    }

    .tree-node-label {
      font-size: 14px;
    }
  }

  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 16px;
    min-width: 0; // 防止 flex 容器被子表格撑开

    .search-card {
      :deep(.el-card__body) {
        padding-bottom: 2px; // 压缩表单底部空隙
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
}
</style>