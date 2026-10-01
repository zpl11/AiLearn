import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import ComponeyDept from "@/views/ComponeyDept.vue"
import UserManagement from "@/views/UserManagement.vue"
import PostManagement from "@/views/post.vue"
import ComponeyMenu from "@/views/ComponeyMenu.vue"
import ComponeyRole from "@/views/ComponeyRole.vue"
import Login from "@/views/login.vue"
import FlowDefinition from "@/views/FlowDefinition.vue" // 1. 导入流程定义页面组件

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login,
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
        {
          path: 'flowDefinition', // 2. 新增流程定义模板路由，对应访问路径为 /flowDefinition
          name: 'flowDefinition',
          component: FlowDefinition,
          meta: { title: '流程定义' }
        },
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
  ],
})

// 全局路由守卫：未登录时强制跳转到登录页
router.beforeEach((to, from, next) => {
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