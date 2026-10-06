<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  User,
  OfficeBuilding,
  Briefcase,
  Key,
  Menu as MenuIcon,
  InfoFilled,
  SwitchButton,
  Avatar,
  Operation,
  Document,
  WarningFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 获取当前登录人信息
const userInfo = ref<{ userName: string; nickName: string }>({
  userName: '',
  nickName: ''
})

onMounted(() => {
  const infoStr = localStorage.getItem('USER_INFO')
  if (infoStr) {
    try {
      userInfo.value = JSON.parse(infoStr)
    } catch (e) {
      // 容错处理
    }
  }
})

// 高亮当前选中的路由菜单项
const activeMenu = computed(() => route.path)

// 安全退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出当前企业 OA 系统吗？', '提示', {
    confirmButtonText: '确定退出',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    localStorage.clear()
    ElMessage.success('已安全退出登录')
    router.push('/login')
  }).catch(() => {})
}
</script>
<template>
  <el-container class="layout-container">
    <!-- 1. 左侧侧边栏导航 -->
    <el-aside width="230px" class="layout-aside">
      <div class="logo-box">
        <span class="logo-title">企业协同办公 OA</span>
      </div>

      <el-menu
          :default-active="activeMenu"
          router
          class="aside-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
      >
        <!-- 系统与权限管理模块 -->
        <el-sub-menu index="system">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/userManagement">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/componeyDept">
            <el-icon><OfficeBuilding /></el-icon>
            <span>部门管理</span>
          </el-menu-item>
          <el-menu-item index="/postManagement">
            <el-icon><Briefcase /></el-icon>
            <span>岗位管理</span>
          </el-menu-item>
          <el-menu-item index="/componeyRole">
            <el-icon><Key /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/componeyMenu">
            <el-icon><MenuIcon /></el-icon>
            <span>菜单管理</span>
          </el-menu-item>
          <el-menu-item index="/operLog">
            <el-icon><Document /></el-icon>
            <span>操作日志</span>
          </el-menu-item>
          <el-menu-item index="/errorLog">
            <el-icon><WarningFilled /></el-icon>
            <span>异常监控</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 审批中心模块：承载流程定义及后续审批事务流转 -->
        <el-sub-menu index="flow">
          <template #title>
            <el-icon><Operation /></el-icon>
            <span>审批中心</span>
          </template>
          <el-menu-item index="/flowDefinition">
            <el-icon><Operation /></el-icon>
            <span>流程定义</span>
          </el-menu-item>
          <!-- 审批实例管理 (如: 我发起的) -->
          <el-menu-item index="/flowInstance">
            <el-icon><Operation /></el-icon>
            <span>审批实例管理</span>
          </el-menu-item>
          <!-- ✅ 新增菜单项：我的任务，对应路由 flowTask (支撑待办/已办的流转操作) -->
          <el-menu-item index="/flowTask">
            <el-icon><Operation /></el-icon>
            <span>我的任务</span>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/about">
          <el-icon><InfoFilled /></el-icon>
          <span>关于系统</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 2. 右侧主体容器 -->
    <el-container class="layout-body">
      <!-- 顶部 Header -->
      <el-header class="layout-header">
        <div class="header-left">
          <span class="system-tag">企业协同办公与权限控制中心</span>
        </div>

        <div class="header-right">
          <el-dropdown trigger="click">
            <div class="user-profile">
              <el-avatar :size="32" :icon="Avatar" class="user-avatar" />
              <span class="user-name">{{ userInfo.nickName || userInfo.userName || '系统操作员' }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="SwitchButton" @click="handleLogout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 动态路由展示区 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;

  .layout-aside {
    background-color: #304156;
    display: flex;
    flex-direction: column;
    height: 100%;

    .logo-box {
      height: 56px;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #2b3647;

      .logo-title {
        color: #ffffff;
        font-size: 16px;
        font-weight: 600;
        letter-spacing: 0.5px;
      }
    }

    .aside-menu {
      border-right: none;
      flex: 1;
      overflow-y: auto;
    }
  }

  .layout-body {
    display: flex;
    flex-direction: column;
    background-color: #f0f2f5;

    .layout-header {
      background-color: #ffffff;
      height: 56px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid var(--el-border-color-lighter);
      padding: 0 20px;

      .system-tag {
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }

      .header-right {
        .user-profile {
          display: flex;
          align-items: center;
          gap: 8px;
          cursor: pointer;
          outline: none;

          .user-name {
            font-size: 14px;
            color: var(--el-text-color-primary);
          }
        }
      }
    }

    .layout-main {
      padding: 0;
      flex: 1;
      overflow-y: auto;
    }
  }
}
</style>
