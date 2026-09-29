<script setup lang="ts">
import { ref, reactive, watch, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type TreeInstance } from 'element-plus'
import { getMenuList, addMenu, updateMenu, deleteMenu, type SysMenu } from '@/api/menu'

// --- 1. 类型定义 ---
interface MenuTreeNode {
  id: number
  label: string
  children?: MenuTreeNode[]
}

const rawMenuList = ref<SysMenu[]>([])

// --- 2. 左侧菜单树结构 ---
const treeFilterText = ref('')
const menuTreeRef = ref<TreeInstance>()
const currentMenuId = ref<number | null>(null)
const menuTreeData = ref<MenuTreeNode[]>([])

const buildTree = (list: SysMenu[], parentId: number = 0): MenuTreeNode[] => {
  return list
      .filter((item) => (item.parentId ?? 0) === parentId)
      .map((item) => {
        const node: MenuTreeNode = {
          id: item.menuId!,
          label: item.menuName,
        }
        const children = buildTree(list, item.menuId!)
        if (children.length > 0) {
          node.children = children
        }
        return node
      })
}

watch(treeFilterText, (val) => {
  menuTreeRef.value?.filter(val)
})

const filterNode = (value: string, data: MenuTreeNode) => {
  if (!value) return true
  return data.label.includes(value)
}

const handleNodeClick = (data: MenuTreeNode) => {
  currentMenuId.value = data.id
  searchQuery.menuName = ''
  filterTableData()
}

// --- 3. 搜索与表格逻辑 ---
const loading = ref(false)
const tableData = ref<SysMenu[]>([])
const searchQuery = reactive({
  menuName: '',
  status: '',
})

const fetchMenuData = async () => {
  loading.value = true
  try {
    const list = await getMenuList()
    rawMenuList.value = list
    menuTreeData.value = buildTree(list, 0)
    filterTableData()
  } finally {
    loading.value = false
  }
}

const filterTableData = () => {
  tableData.value = rawMenuList.value.filter((item) => {
    const matchTree =
        currentMenuId.value === null ||
        item.parentId === currentMenuId.value ||
        item.menuId === currentMenuId.value

    const matchName = !searchQuery.menuName || item.menuName.includes(searchQuery.menuName)
    const matchStatus = searchQuery.status === '' || String(item.status) === searchQuery.status

    return matchTree && matchName && matchStatus
  })
}

const handleSearch = () => filterTableData()

const handleReset = () => {
  searchQuery.menuName = ''
  searchQuery.status = ''
  currentMenuId.value = null
  filterTableData()
}

// --- 4. 增删改表单逻辑 ---
const dialogVisible = ref(false)
const dialogTitle = ref('')
const menuFormRef = ref<FormInstance>()

const menuFormData = reactive<SysMenu>({
  menuId: undefined,
  parentId: 0,
  menuName: '',
  orderNum: 0,
  path: '',
  component: '',
  menuType: 'M',
  perms: '',
  status: 0,
})

const formRules: FormRules = {
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' },
    { whitespace: true, message: '菜单名称不能为全空格', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  orderNum: [{ required: true, message: '请输入显示排序', trigger: 'blur' }],
}

const handleAdd = (row?: SysMenu) => {
  dialogTitle.value = row ? `在 [${row.menuName}] 下新增子项` : '新增顶级目录'
  menuFormData.menuId = undefined
  menuFormData.parentId = row?.menuId ?? 0
  menuFormData.menuName = ''
  menuFormData.orderNum = 0
  menuFormData.path = ''
  menuFormData.component = ''
  menuFormData.menuType = row?.menuType === 'M' ? 'C' : (row?.menuType === 'C' ? 'F' : 'M')
  menuFormData.perms = ''
  menuFormData.status = 0

  dialogVisible.value = true
  nextTick(() => menuFormRef.value?.clearValidate())
}

const handleEdit = (row: SysMenu) => {
  dialogTitle.value = '修改菜单/权限'
  Object.assign(menuFormData, row)
  dialogVisible.value = true
  nextTick(() => menuFormRef.value?.clearValidate())
}

const submitForm = async () => {
  if (!menuFormRef.value) return
  await menuFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (typeof menuFormData.menuId === 'number' && menuFormData.menuId > 0) {
      await updateMenu(menuFormData)
      ElMessage.success('修改成功')
    } else {
      await addMenu(menuFormData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchMenuData()
  })
}

const handleDelete = (row: SysMenu) => {
  ElMessageBox.confirm(`确认删除菜单【${row.menuName}】吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    if (!row.menuId) return
    await deleteMenu(row.menuId)
    ElMessage.success('删除成功')
    fetchMenuData()
  }).catch(() => {})
}

onMounted(() => {
  fetchMenuData()
})
</script>

<template>
  <div class="menu-layout">
    <!-- 左侧菜单树 -->
    <el-card class="tree-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">菜单层级</span>
          <el-button link type="primary" @click="handleReset">重置选中</el-button>
        </div>
      </template>
      <el-input
          v-model="treeFilterText"
          placeholder="输入菜单名称过滤"
          clearable
          class="tree-search-input"
      />
      <div class="tree-wrapper">
        <el-tree
            ref="menuTreeRef"
            :data="menuTreeData"
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

    <!-- 右侧表格区 -->
    <div class="main-content">
      <el-card class="search-card" shadow="never">
        <el-form :inline="true" :model="searchQuery" class="search-form">
          <el-form-item label="菜单名称">
            <el-input
                v-model.trim="searchQuery.menuName"
                placeholder="请输入菜单名称"
                clearable
                @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchQuery.status" placeholder="状态" clearable style="width: 120px">
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

      <el-card class="table-card" shadow="never">
        <div class="toolbar">
          <el-button type="primary" @click="handleAdd()">新增顶级目录</el-button>
        </div>

        <el-table v-loading="loading" :data="tableData" row-key="menuId" border style="width: 100%">
          <el-table-column prop="menuName" label="菜单名称" min-width="150" />
          <el-table-column prop="menuType" label="类型" width="90" align="center">
            <template #default="{ row }">
              <el-tag v-if="row.menuType === 'M'" type="warning">目录</el-tag>
              <el-tag v-else-if="row.menuType === 'C'" type="success">菜单</el-tag>
              <el-tag v-else type="info">按钮</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="path" label="路由地址" min-width="140" />
          <el-table-column prop="component" label="组件路径" min-width="160" />
          <el-table-column prop="perms" label="权限标识" min-width="150" />
          <el-table-column prop="orderNum" label="排序" width="70" align="center" />
          <el-table-column prop="status" label="状态" width="85" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'danger'">
                {{ row.status === 0 ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
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

    <!-- 增/改 对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="menuFormRef" :model="menuFormData" :rules="formRules" label-width="100px">
        <el-form-item label="菜单类型">
          <el-radio-group v-model="menuFormData.menuType">
            <el-radio label="M">目录</el-radio>
            <el-radio label="C">菜单</el-radio>
            <el-radio label="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model.trim="menuFormData.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="显示排序" prop="orderNum">
          <el-input-number v-model="menuFormData.orderNum" :min="0" :max="999" controls-position="right" />
        </el-form-item>
        <el-form-item v-if="menuFormData.menuType !== 'F'" label="路由地址">
          <el-input v-model.trim="menuFormData.path" placeholder="例如: role 或 /system/role" />
        </el-form-item>
        <el-form-item v-if="menuFormData.menuType === 'C'" label="组件路径">
          <el-input v-model.trim="menuFormData.component" placeholder="例如: system/role/index" />
        </el-form-item>
        <el-form-item v-if="menuFormData.menuType !== 'M'" label="权限标识">
          <el-input v-model.trim="menuFormData.perms" placeholder="例如: system:role:add" />
        </el-form-item>
        <el-form-item label="菜单状态" prop="status">
          <el-radio-group v-model="menuFormData.status">
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
.menu-layout {
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
    min-width: 0;

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
}
</style>