<script setup lang="ts">
import { ref, reactive, watch, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type TreeInstance } from 'element-plus'
import {
  getDeptList,
  addDept,
  updateDept,
  deleteDept,
  type SysDept
} from '@/api/dept'

// --- 1. 类型定义 ---
interface DeptNode {
  id: number
  label: string
  children?: DeptNode[]
}

// 原始全量部门数据（内存缓存，支撑前端过滤）
const rawDeptList = ref<SysDept[]>([])

// --- 2. 左侧部门树逻辑 ---
const treeFilterText = ref('')
const deptTreeRef = ref<TreeInstance>()
const currentDeptId = ref<number | null>(null)
const deptTreeData = ref<DeptNode[]>([])

// 算法：将扁平列表在内存中递归组装为 Tree 结构
const buildTree = (list: SysDept[], parentId: number = 0): DeptNode[] => {
  return list
      .filter((item) => (item.parentId ?? 0) === parentId)
      .map((item) => {
        const node: DeptNode = {
          id: item.deptId!,
          label: item.deptName,
        }
        const children = buildTree(list, item.deptId!)
        if (children.length > 0) {
          node.children = children
        }
        return node
      })
}

// 树节点关键字过滤
watch(treeFilterText, (val) => {
  deptTreeRef.value?.filter(val)
})

const filterNode = (value: string, data: DeptNode) => {
  if (!value) return true
  return data.label.includes(value)
}

// 点击左侧树节点联动过滤表格
const handleNodeClick = (data: DeptNode) => {
  currentDeptId.value = data.id
  searchQuery.deptName = ''
  filterTableData()
}

// --- 3. 右侧搜索与表格数据 ---
const loading = ref(false)
const tableData = ref<SysDept[]>([])
const searchQuery = reactive({
  deptName: '',
  status: '',
})

// 从后端获取全量数据并刷新视图
const fetchDeptData = async () => {
  loading.value = true
  try {
    const list = await getDeptList()
    rawDeptList.value = list
    // 组装左侧树
    deptTreeData.value = buildTree(list, 0)
    // 刷新右侧表格
    filterTableData()
  } catch (error) {
    // 错误已被 request.ts 统一拦截提示
  } finally {
    loading.value = false
  }
}

// 本地组合条件过滤表格
const filterTableData = () => {
  tableData.value = rawDeptList.value.filter((item) => {
    // 树节点过滤：展示自身及直接子项
    const matchTree =
        currentDeptId.value === null ||
        item.parentId === currentDeptId.value ||
        item.deptId === currentDeptId.value

    // 部门名称搜索
    const matchName = !searchQuery.deptName || item.deptName.includes(searchQuery.deptName)

    // 状态过滤
    const matchStatus = searchQuery.status === '' || String(item.status) === searchQuery.status

    return matchTree && matchName && matchStatus
  })
}

const handleSearch = () => {
  filterTableData()
}

const handleReset = () => {
  searchQuery.deptName = ''
  searchQuery.status = ''
  currentDeptId.value = null
  filterTableData()
}

// --- 4. 增删改弹窗与表单逻辑 ---
const dialogVisible = ref(false)
const dialogTitle = ref('')
const deptFormRef = ref<FormInstance>()

const deptFormData = reactive<SysDept>({
  deptId: undefined,
  parentId: 0,
  deptName: '',
  orderNum: 0,
  leaderId: undefined,
  status: 0,
})

// 表单校验：增加 whitespace 拦截纯空格
const formRules: FormRules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { whitespace: true, message: '部门名称不能为全空格', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  orderNum: [{ required: true, message: '请输入显示排序', trigger: 'blur' }],
}

// 打开新增（顶级或子级）
const handleAdd = (row?: SysDept) => {
  dialogTitle.value = row ? `在 [${row.deptName}] 下新增子部门` : '新增顶级部门'
  // 核心清空：确保新增时 deptId 绝对为 undefined
  deptFormData.deptId = undefined
  deptFormData.parentId = row?.deptId ?? 0
  deptFormData.deptName = ''
  deptFormData.orderNum = 0
  deptFormData.leaderId = undefined
  deptFormData.status = 0

  dialogVisible.value = true
  nextTick(() => {
    deptFormRef.value?.clearValidate()
  })
}

// 打开修改
const handleEdit = (row: SysDept) => {
  dialogTitle.value = '修改部门'
  Object.assign(deptFormData, row)
  dialogVisible.value = true
  nextTick(() => {
    deptFormRef.value?.clearValidate()
  })
}

// 提交表单保存
const submitForm = async () => {
  if (!deptFormRef.value) return
  await deptFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      // 核心判断：明确判断 deptId 为大于 0 的数值才走更新，否则统一走新增
      if (typeof deptFormData.deptId === 'number' && deptFormData.deptId > 0) {
        await updateDept(deptFormData)
        ElMessage.success('修改成功')
      } else {
        await addDept(deptFormData)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      fetchDeptData() // 刷新全量数据
    } catch (error) {
      // 错误已被 request.ts 统一拦截提示
    }
  })
}

// 删除部门
const handleDelete = (row: SysDept) => {
  ElMessageBox.confirm(`确认删除部门【${row.deptName}】吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    if (!row.deptId) return
    await deleteDept(row.deptId)
    ElMessage.success('删除成功')
    fetchDeptData()
  }).catch(() => {})
}

// 页面加载触发查询
onMounted(() => {
  fetchDeptData()
})
</script>

<template>
  <div class="dept-layout">
    <!-- 左侧：部门组织树卡片 -->
    <el-card class="tree-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">组织架构</span>
          <el-button link type="primary" @click="handleReset">重置选中</el-button>
        </div>
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
                v-model.trim="searchQuery.deptName"
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
            row-key="deptId"
            border
            style="width: 100%"
        >
          <el-table-column prop="deptName" label="部门名称" min-width="160" />
          <el-table-column prop="orderNum" label="排序" width="80" align="center" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'danger'">
                {{ row.status === 0 ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="170" align="center" />
          <el-table-column label="操作" width="220" align="center" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleAdd(row)">新增子项</el-button>
              <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 5. 增/改 模态对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="deptFormRef" :model="deptFormData" :rules="formRules" label-width="90px">
        <el-form-item label="部门名称" prop="deptName">
          <!-- 核心：添加 .trim 修饰符 -->
          <el-input v-model.trim="deptFormData.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="显示排序" prop="orderNum">
          <el-input-number v-model="deptFormData.orderNum" :min="0" :max="999" controls-position="right" />
        </el-form-item>
        <el-form-item label="部门状态" prop="status">
          <el-radio-group v-model="deptFormData.status">
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

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

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