import Vue from 'vue'
import VueRouter from 'vue-router'

import Layout from '@/Layout'

Vue.use(VueRouter)

const routes = [
  // {
  //   path: '/about',
  //   name: 'About',
  //   component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  // },
  {
    path: '/myfund',
    component: Layout,
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home')
      }
    ]
  },
  {
    path: '/mysearch',
    component: Layout,
    // component: () => import('@/views/funddata')
    children: [
      {
        path: 'fundList',
        component: resolve => require(['@/views/funddata'], resolve)
      }
    ]
  },
  {
    path: '*',
    redirect: '/myfund/home'
  }
]

const router = new VueRouter({
  routes
})

export default router
