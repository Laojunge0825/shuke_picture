import { h } from 'vue'
import { HomeOutlined } from '@ant-design/icons-vue'
import Home from '@/views/HomeView.vue'

export const routes = [
  {
    path: '/',
    name: '主页',
    component: Home,
    meta: {
      icon: () => h(HomeOutlined),
    },
  },
  {
    path: '/about',
    name: '关于',
    // route level code-splitting
    // this generates a separate chunk (About.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import('../views/HomeView.vue'),
  },
]
