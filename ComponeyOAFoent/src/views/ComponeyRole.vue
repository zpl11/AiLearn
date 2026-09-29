<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type TreeInstance } from 'element-plus'
import {
  getRoleList,
  addRole,
  updateRole,
  deleteRole,
  changeRoleStatus,
  getRoleMenuIds,
  assignRoleMenus,
  type SysRole
} from '@/api/role'
import { getMenuList, type SysMenu } from '@/api/menu'

interface MenuTreeNode {
  id: number
  label: string
  children?: MenuTreeNode[]
}

const loading = ref(false)
const roleList = ref<SysRole[]>([])
const selectedRoleIds = ref<number[]>([])

const searchQuery = reactive({
  roleName: '',
  status: '',
})

// 获取角色表格数据
const fetchRoleData = async () => {
  loading.value = true
  try {
    roleList.value = await getRoleList({
      roleName: searchQuery.roleName || undefined,
      status: searchQuery.status !== '' ? searchQuery.status : undefined
    })
  } finally {
    loading.value = false
  }
}

const handleSearch = () => fetchRoleData()
const handleReset = () => {
  searchQuery.roleName = ''
  searchQuery.status = ''
  fetchRoleData()
}

const handleSelectionChange = (selection: SysRole[]) => {
  selectedRoleIds.value = selection.map((item) => item.roleId!)
}

// 快速修改角色状态
const handleStatusChange = async (row: SysRole) => {
  try {
    await changeRoleStatus(row.roleId!, row.status)
    ElMessage.success(`角色【${row.roleName}】状态已更新`)
  } catch (error) {
    row.status = row.status === 0 ? 1 : 0 // 回滚开关状态
  }
}

// --- 角色增删改表单 ---
const dialogVisible = ref(false)
const dialogTitle = ref('')
const roleFormRef = ref<FormInstance>()

const roleFormData = reactive<SysRole>({
  roleId: undefined,
  roleName: '',
  roleCode: '',
  roleSort: 0,
  status: 0,
  remark: '',
})

const formRules: FormRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { whitespace: true, message: '角色名称不能为全空格', trigger: 'blur' },
  ],
  roleCode: [
    { required: true, message: '请输入权限字符', trigger: 'blur' },
    { whitespace: true, message: '权限字符不能为全空格', trigger: 'blur' },
  ],
  roleSort: [{ required: true, message: '请输入显示排序', trigger: 'blur' }],
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  roleFormData.roleId = undefined
  roleFormData.roleName = ''
  roleFormData.roleCode = ''
  roleFormData.roleSort = 0
  roleFormData.status = 0
  roleFormData.remark = ''
  dialogVisible.value = true
  nextTick(() => roleFormRef.value?.clearValidate())
}

const handleEdit = (row: SysRole) => {
  dialogTitle.value = '修改角色'
  Object.assign(roleFormData, row)
  dialogVisible.value = true
  nextTick(() => roleFormRef.value?.clearValidate())
}

const submitForm = async () => {
  if (!roleFormRef.value) return
  await roleFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (typeof roleFormData.roleId === 'number' && roleFormData.roleId > 0) {
      await updateRole(roleFormData)
      ElMessage.success('修改成功')
    } else {
      await addRole(roleFormData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchRoleData()
  })
}

const handleDelete = (row?: SysRole) => {
  const ids = row?.roleId ? [row.roleId] : selectedRoleIds.value
  if (!ids.length) return

  ElMessageBox.confirm(`确认删除已选中的角色数据吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await deleteRole(ids)
    ElMessage.success('删除成功')
    fetchRoleData()
  }).catch(() => {})
}

// --- 核心：角色授权弹窗逻辑 ---
const authDialogVisible = ref(false)
const currentAuthRoleId = ref<number>()
const menuTreeRef = ref<TreeInstance>()
const menuOptions = ref<MenuTreeNode[]>([])

const buildMenuTree = (list: SysMenu[], parentId: number = 0): MenuTreeNode[] => {
  return list
      .filter((item) => (item.parentId ?? 0) === parentId)
      .map((item) => {
        const node: MenuTreeNode = {
          id: item.menuId!,
          label: item.menuName,
        }
        const children = buildMenuTree(list, item.menuId!)
        if (children.length > 0) {
          node.children = children
        }
        return node
      })
}

// 打开授权抽屉/弹窗
const handleAssignMenus = async (row: SysRole) => {
  currentAuthRoleId.value = row.roleId
  authDialogVisible.value = true

  // 1. 获取全量菜单并构造成树
  const allMenus = await getMenuList()
  menuOptions.value = buildMenuTree(allMenus, 0)

  // 2. 获取该角色已有权限并回显打钩
  const checkedKeys = await getRoleMenuIds(row.roleId!)
  nextTick(() => {
    menuTreeRef.value?.setCheckedKeys([])
    checkedKeys.forEach((key) => {
      menuTreeRef.value?.setChecked(key, true, false)
    })
  })
}

// 提交权限配置
const submitAuth = async () => {
  if (!currentAuthRoleId.value || !menuTreeRef.value) return
  // 获取当前勾选以及半选的父级节点
  const checkedKeys = menuTreeRef.value.getCheckedKeys() as number[]
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys() as number[]
  const finalMenuIds = [...checkedKeys, ...halfCheckedKeys]

  await assignRoleMenus(currentAuthRoleId.value, finalMenuIds)
  ElMessage.success('权限分配成功')
  authDialogVisible.value = false
}

onMounted(() => {
  fetchRoleData()
})
</script>

<template>
  <div class="role-layout">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchQuery" class="search-form">
        <el-form-item label="角色名称">
          <el-input
              v-model.trim="searchQuery.roleName"
              placeholder="请输入角色名称"
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

    <!-- 表格与操作栏 -->
    <el-card class="table-card" shadow="never">
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增角色</el-button>
        <el-button type="danger" :disabled="!selectedRoleIds.length" @click="handleDelete()">批量删除</el-button>
      </div>

      <el-table
          v-loading="loading"
          :data="roleList"
          row-key="roleId"
          border
          style="width: 100%"
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleCode" label="权限字符" min-width="150" />
        <el-table-column prop="roleSort" label="显示顺序" width="90" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
                v-model="row.status"
                :active-value="0"
                :inactive-value="1"
                @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" align="center" />
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="primary" @click="handleAssignMenus(row)">分配权限</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 增/改角色 模态框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="roleFormRef" :model="roleFormData" :rules="formRules" label-width="90px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model.trim="roleFormData.roleName" placeholder="例如: 运营专员" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleCode">
          <el-input v-model.trim="roleFormData.roleCode" placeholder="例如: role:operator" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="roleSort">
          <el-input-number v-model="roleFormData.roleSort" :min="0" :max="999" controls-position="right" />
        </el-form-item>
        <el-form-item label="角色状态" prop="status">
          <el-radio-group v-model="roleFormData.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="roleFormData.remark" type="textarea" placeholder="请输入角色说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 核心：分配权限树 模态框 -->
    <el-dialog v-model="authDialogVisible" title="分配菜单与操作权限" width="500px" destroy-on-close>
      <div class="auth-tree-container">
        <el-tree
            ref="menuTreeRef"
            :data="menuOptions"
            show-checkbox
            node-key="id"
            default-expand-all
            highlight-current
        />
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="authDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAuth">保存权限</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.role-layout {
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

  .auth-tree-container {
    max-height: 400px;
    overflow-y: auto;
    border: 1px solid var(--el-border-color-lighter);
    padding: 10px;
    border-radius: 4px;
  }
}
</style>