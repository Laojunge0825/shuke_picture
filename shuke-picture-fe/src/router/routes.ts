import { h } from 'vue'
import { HomeOutlined } from '@ant-design/icons-vue'
import HomePege from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import AddPicturePage from '@/pages/picture/AddPicturePage.vue'
export const routes = [
  {
    path: '/',
    name: '主页',
    component: HomePege,
    meta: {
      icon: () => h(HomeOutlined),
    },
  },
  {
    path: '/user/login',
    name: '用户登录',
    component: UserLoginPage,
  },
  {
    path: '/user/register',
    name: '用户注册',
    component: UserRegisterPage,
  },
  {
    path: '/admin/userManage',
    name: '用户管理',
    component: UserManagePage,
  },
  {
    path: '/add_picture',
    name: '创建图片',
    title: '创建图片',
    component: AddPicturePage,
  },

]
