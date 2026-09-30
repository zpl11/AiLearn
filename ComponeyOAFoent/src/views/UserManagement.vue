<script setup lang="ts">
import { ref, reactive, watch, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type TreeInstance } from 'element-plus'
import { getDeptList, type SysDept } from '@/api/dept'
import {
  getUserList,
  getUserByDept,
  addUser,
  updateUser,
  deleteUser,
  getRoleIdsByUser,
  assignUserRoles,
  type SysUser
} from '@/api/user'
import {
  getPostList,
  getPostIdsByUser,
  assignUserPosts,
  type SysPost
} from '@/api/post'
import { getRoleList, type SysRole } from '@/api/role'

// --- 1. 类型定义 ---
interface DeptNode {
  id: number | string
  label: string
  children?: DeptNode[]
}

// 原始全量部门与员工数据（内存支撑高响应度过滤）
const rawDeptList = ref<SysDept[]>([])
const rawUserList = ref<SysUser[]>([])
// 全量启用的岗位与角色字典
const allPostList = ref<SysPost[]>([])
const allRoleList = ref<SysRole[]>([])

// --- 2. 左侧部门树逻辑 ---
const treeFilterText = ref('')
const deptTreeRef = ref<TreeInstance>()
const currentDeptId = ref<number | string | null>(null)
const deptTreeData = ref<DeptNode[]>([])

// 将部门扁平列表组装为 Tree 结构
const buildTree = (list: SysDept[], parentId: number | string = 0): DeptNode[] => {
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

// 部门树检索过滤
watch(treeFilterText, (val) => {
  deptTreeRef.value?.filter(val)
})

const filterNode = (value: string, data: DeptNode) => {
  if (!value) return true
  return data.label.includes(value)
}

// 点击左侧树联动拉取/筛选员工
const handleNodeClick = async (data: DeptNode) => {
  currentDeptId.value = data.id
  loading.value = true
  try {
    const res = await getUserByDept(data.id)
    rawUserList.value = res || []
    filterTableData()
  } finally {
    loading.value = false
  }
}

// --- 3. 右侧搜索与员工表格 ---
const loading = ref(false)
const tableData = ref<SysUser[]>([])
const searchQuery = reactive({
  userName: '',
  nickName: '',
  status: '',
})

// 初始化拉取部门树、员工列表、岗位字典与角色字典
const initData = async () => {
  loading.value = true
  try {
    const [deptRes, userRes, postRes, roleRes] = await Promise.all([
      getDeptList(),
      getUserList(),
      getPostList(),
      getRoleList()
    ])
    rawDeptList.value = deptRes || []
    deptTreeData.value = buildTree(deptRes || [], 0)
    rawUserList.value = userRes || []
    allPostList.value = (postRes || []).filter((p: SysPost) => p.status === 0)
    allRoleList.value = (roleRes || []).filter((r: SysRole) => r.status === 0)
    filterTableData()
  } finally {
    loading.value = false
  }
}

// 本地组合条件过滤
const filterTableData = () => {
  tableData.value = rawUserList.value.filter((item) => {
    const matchUserName = !searchQuery.userName || item.userName.includes(searchQuery.userName)
    const matchNickName = !searchQuery.nickName || item.nickName.includes(searchQuery.nickName)
    const matchStatus = searchQuery.status === '' || String(item.status) === searchQuery.status
    return matchUserName && matchNickName && matchStatus
  })
}

const handleSearch = () => {
  filterTableData()
}

const handleReset = async () => {
  searchQuery.userName = ''
  searchQuery.nickName = ''
  searchQuery.status = ''
  currentDeptId.value = null
  loading.value = true
  try {
    const res = await getUserList()
    rawUserList.value = res || []
    filterTableData()
  } finally {
    loading.value = false
  }
}

// 部门 ID 转换名称展示
const getDeptName = (deptId?: number | string) => {
  if (!deptId) return '未分配'
  const dept = rawDeptList.value.find((d) => d.deptId === deptId)
  return dept ? dept.deptName : '未知部门'
}

// --- 4. 增删改表单模态框逻辑 ---
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const userFormRef = ref<FormInstance>()

const userFormData = reactive<SysUser>({
  userId: undefined,
  deptId: undefined,
  userName: '',
  nickName: '',
  password: '',
  phone: '',
  email: '',
  sex: '0',
  status: 0,
})

// 表单校验规则
const formRules: FormRules = {
  userName: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
  ],
  nickName: [{ required: true, message: '请输入员工姓名', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择归属部门', trigger: 'change' }],
  password: [
    { required: true, message: '请输入登录初始密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 位', trigger: 'blur' },
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' },
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
}

// 打开新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增员工'
  userFormData.userId = undefined
  userFormData.deptId = currentDeptId.value ?? undefined
  userFormData.userName = ''
  userFormData.nickName = ''
  userFormData.password = ''
  userFormData.phone = ''
  userFormData.email = ''
  userFormData.sex = '0'
  userFormData.status = 0

  dialogVisible.value = true
  nextTick(() => {
    userFormRef.value?.clearValidate()
  })
}

// 打开修改
const handleEdit = (row: SysUser) => {
  isEdit.value = true
  dialogTitle.value = '修改员工资料'
  Object.assign(userFormData, row)
  userFormData.password = ''
  dialogVisible.value = true
  nextTick(() => {
    userFormRef.value?.clearValidate()
  })
}

// 提交保存员工信息
const submitForm = async () => {
  if (!userFormRef.value) return
  await userFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value && userFormData.userId) {
        await updateUser(userFormData)
        ElMessage.success('修改成功')
      } else {
        await addUser(userFormData)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      if (currentDeptId.value) {
        const res = await getUserByDept(currentDeptId.value)
        rawUserList.value = res || []
      } else {
        const res = await getUserList()
        rawUserList.value = res || []
      }
      filterTableData()
    } catch (error) {
      // 拦截器统一处理
    }
  })
}

// 逻辑删除员工
const handleDelete = (row: SysUser) => {
  ElMessageBox.confirm(`确认删除员工【${row.nickName} (${row.userName})】吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    if (!row.userId) return
    await deleteUser(row.userId)
    ElMessage.success('删除成功')
    if (currentDeptId.value) {
      const res = await getUserByDept(currentDeptId.value)
      rawUserList.value = res || []
    } else {
      const res = await getUserList()
      rawUserList.value = res || []
    }
    filterTableData()
  }).catch(() => {})
}

// 公用当前被授权/分配的用户状态
const currentAssignUser = reactive({
  userId: 0,
  userName: '',
  nickName: ''
})

// --- 5. 分配岗位弹窗逻辑 ---
const postDialogVisible = ref(false)
const postSubmitting = ref(false)
const selectedPostIds = ref<number[]>([])

// 打开分配岗位对话框并回显
const handleOpenAssignPost = async (row: SysUser) => {
  if (!row.userId) return
  currentAssignUser.userId = row.userId as number
  currentAssignUser.userName = row.userName
  currentAssignUser.nickName = row.nickName
  selectedPostIds.value = []

  postDialogVisible.value = true
  try {
    const postIds = await getPostIdsByUser(row.userId)
    selectedPostIds.value = postIds || []
  } catch (error) {
    // 拦截器统一提示
  }
}

// 提交岗位分配
const submitPostAssign = async () => {
  postSubmitting.value = true
  try {
    await assignUserPosts({
      userId: currentAssignUser.userId,
      postIds: selectedPostIds.value
    })
    ElMessage.success('岗位分配成功')
    postDialogVisible.value = false
  } finally {
    postSubmitting.value = false
  }
}

// --- 6. 核心增补：分配角色弹窗逻辑 ---
const roleDialogVisible = ref(false)
const roleSubmitting = ref(false)
const selectedRoleIds = ref<number[]>([])

// 打开分配角色对话框并回显
const handleOpenAssignRole = async (row: SysUser) => {
  if (!row.userId) return
  currentAssignUser.userId = row.userId as number
  currentAssignUser.userName = row.userName
  currentAssignUser.nickName = row.nickName
  selectedRoleIds.value = []

  roleDialogVisible.value = true
  try {
    const roleIds = await getRoleIdsByUser(row.userId)
    selectedRoleIds.value = roleIds || []
  } catch (error) {
    // 拦截器统一提示
  }
}

// 提交角色分配（对接后端的 UserRoleDTO）
const submitRoleAssign = async () => {
  roleSubmitting.value = true
  try {
    await assignUserRoles({
      userId: currentAssignUser.userId,
      roleIds: selectedRoleIds.value
    })
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
  } finally {
    roleSubmitting.value = false
  }
}

onMounted(() => {
  initData()
})
</script>

<template>
  <div class="user-layout">
    <!-- 左侧：部门组织树卡片 -->
    <el-card class="tree-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">组织架构</span>
          <el-button link type="primary" @click="handleReset">重置所有</el-button>
        </div>
      </template>
      <el-input
          v-model="treeFilterText"
          placeholder="输入部门过滤"
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
          <el-form-item label="登录账号">
            <el-input
                v-model.trim="searchQuery.userName"
                placeholder="请输入账号"
                clearable
                @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="员工姓名">
            <el-input
                v-model.trim="searchQuery.nickName"
                placeholder="请输入姓名"
                clearable
                @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select
                v-model="searchQuery.status"
                placeholder="账号状态"
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

      <!-- 列表与操作区 -->
      <el-card class="table-card" shadow="never">
        <div class="toolbar">
          <el-button type="primary" @click="handleAdd">新增员工</el-button>
        </div>

        <el-table
            v-loading="loading"
            :data="tableData"
            row-key="userId"
            border
            style="width: 100%"
        >
          <el-table-column prop="userName" label="登录账号" min-width="120" />
          <el-table-column prop="nickName" label="员工姓名" min-width="120" />
          <el-table-column label="归属部门" min-width="140">
            <template #default="{ row }">
              <span>{{ getDeptName(row.deptId) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="手机号码" min-width="130" />
          <el-table-column label="性别" width="70" align="center">
            <template #default="{ row }">
              <span>{{ row.sex === '0' ? '男' : row.sex === '1' ? '女' : '未知' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'danger'">
                {{ row.status === 0 ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="170" align="center" />

          <!-- 操作列增设【分配角色】按钮，宽度调到 290 保证自适应不换行 -->
          <el-table-column label="操作" width="290" align="center" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
              <el-button link type="warning" @click="handleOpenAssignPost(row)">分配岗位</el-button>
              <el-button link type="success" @click="handleOpenAssignRole(row)">分配角色</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 员工资料增改弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form ref="userFormRef" :model="userFormData" :rules="formRules" label-width="90px">
        <el-form-item label="归属部门" prop="deptId">
          <el-tree-select
              v-model="userFormData.deptId"
              :data="deptTreeData"
              value-key="id"
              check-strictly
              :render-after-expand="false"
              placeholder="请选择归属部门"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="登录账号" prop="userName">
          <el-input
              v-model.trim="userFormData.userName"
              :disabled="isEdit"
              placeholder="请输入登录账号"
          />
        </el-form-item>
        <el-form-item label="员工姓名" prop="nickName">
          <el-input v-model.trim="userFormData.nickName" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="初始密码" prop="password">
          <el-input
              v-model.trim="userFormData.password"
              type="password"
              show-password
              placeholder="请输入登录密码"
          />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model.trim="userFormData.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="用户邮箱" prop="email">
          <el-input v-model.trim="userFormData.email" placeholder="请输入用户邮箱" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="userFormData.sex">
            <el-radio label="0">男</el-radio>
            <el-radio label="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="账号状态" prop="status">
          <el-radio-group v-model="userFormData.status">
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

    <!-- 分配岗位弹窗 -->
    <el-dialog
        v-model="postDialogVisible"
        title="分配岗位"
        width="500px"
        destroy-on-close
    >
      <div style="padding: 6px 0;">
        <el-alert
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 16px"
        >
          <template #title>
            正在为员工 <b>{{ currentAssignUser.nickName }}</b>（账号：{{ currentAssignUser.userName }}）分配岗位
          </template>
        </el-alert>

        <el-form label-width="80px">
          <el-form-item label="所属岗位">
            <el-select
                v-model="selectedPostIds"
                multiple
                filterable
                placeholder="请选择岗位（支持多选）"
                style="width: 100%"
            >
              <el-option
                  v-for="post in allPostList"
                  :key="post.postId"
                  :label="`${post.postName} (${post.postCode})`"
                  :value="post.postId"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="postDialogVisible = false">取消</el-button>
          <el-button
              type="primary"
              :loading="postSubmitting"
              @click="submitPostAssign"
          >
            保存分配
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 核心增补：分配角色弹窗 -->
    <el-dialog
        v-model="roleDialogVisible"
        title="分配角色"
        width="500px"
        destroy-on-close
    >
      <div style="padding: 6px 0;">
        <el-alert
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 16px"
        >
          <template #title>
            正在为员工 <b>{{ currentAssignUser.nickName }}</b>（账号：{{ currentAssignUser.userName }}）分配角色
          </template>
        </el-alert>

        <el-form label-width="80px">
          <el-form-item label="所属角色">
            <el-select
                v-model="selectedRoleIds"
                multiple
                filterable
                placeholder="请选择角色（支持多选）"
                style="width: 100%"
            >
              <el-option
                  v-for="role in allRoleList"
                  :key="role.roleId"
                  :label="`${role.roleName} (${role.roleCode})`"
                  :value="role.roleId"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="roleDialogVisible = false">取消</el-button>
          <el-button
              type="primary"
              :loading="roleSubmitting"
              @click="submitRoleAssign"
          >
            保存分配
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.user-layout {
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