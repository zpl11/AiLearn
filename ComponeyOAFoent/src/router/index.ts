import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router' // 引入路由记录类型

// ✅ TS 进阶：扩展 RouteMeta 接口，让 TypeScript 认识 meta.title，消除类型警告
declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    requiresAuth?: boolean // 可以预留给后续的细粒度权限控制
  }
}

import HomeView from '@/views/HomeView.vue'
import ComponeyDept from "@/views/ComponeyDept.vue"
import UserManagement from "@/views/UserManagement.vue"
import PostManagement from "@/views/post.vue"
import ComponeyMenu from "@/views/ComponeyMenu.vue"
import ComponeyRole from "@/views/ComponeyRole.vue"
import Login from "@/views/login.vue"
import FlowDefinition from "@/views/FlowDefinition.vue"
// ✅ 审批实例页面 (我发起的)
import FlowInstance from "@/views/FlowInstance.vue"
// ✅ 新增导入：刚刚写好的审批任务明细页面 (我的待办/已办)
import FlowTask from "@/views/FlowTask.vue"

// 显式声明 routes 的类型为 RouteRecordRaw[]
const routes: Array<RouteRecordRaw> = [
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { title: '系统登录' }
  },
  {
    path: '/',
    name: 'layout',
    component: HomeView,
    redirect: '/userManagement', // 登录后默认重定向到用户管理
    children: [
      {
        path: 'userManagement',
        name: 'userManagement',
        component: UserManagement,
        meta: { title: '用户管理' }
      },
      {
        path: 'componeyDept',
        name: 'componeyDept',
        component: ComponeyDept,
        meta: { title: '部门管理' }
      },
      {
        path: 'postManagement',
        name: 'postManagement',
        component: PostManagement,
        meta: { title: '岗位管理' }
      },
      {
        path: 'componeyRole',
        name: 'componeyRole',
        component: ComponeyRole,
        meta: { title: '角色管理' }
      },
      {
        path: 'componeyMenu',
        name: 'componeyMenu',
        component: ComponeyMenu,
        meta: { title: '菜单管理' }
      },
      // ================= 审批中心模块 =================
      {
        path: 'flowDefinition',
        name: 'flowDefinition',
        component: FlowDefinition,
        meta: { title: '流程定义' }
      },
      {
        path: 'flowInstance',
        name: 'flowInstance',
        component: FlowInstance,
        meta: { title: '审批实例管理' }
      },
      {
        path: 'flowTask',
        name: 'flowTask',
        component: FlowTask,
        meta: { title: '我的任务' } // 支撑【我的待办】与【我的已办】
      },
      // ===============================================
      {
        path: 'about',
        name: 'about',
        component: () => import('@/views/AboutView.vue'),
        meta: { title: '系统关于' }
      }
    ]
  },
  // 兜底重定向
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 全局路由守卫：未登录时强制跳转到登录页
router.beforeEach((to, from, next) => {
  // 动态修改浏览器标签页标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - CompanyOA`
  }

  const token = localStorage.getItem('TOKEN')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router