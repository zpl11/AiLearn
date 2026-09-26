import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ComponeyDept from "@/views/ComponeyDept.vue";
import UserManagement from "@/views/UserManagement.vue";
import postManagement from "@/views/post.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/ComponeyDept',
      name: 'componeyDept',
      component: ComponeyDept,
    },
    {
      path: '/UserManagement',
      name: 'userManagement',
      component: UserManagement,
    },
    {
      path: '/postManagement',
      name: 'postManagement',
      component: postManagement,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
