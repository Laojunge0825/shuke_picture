import { h } from 'vue'
import { HomeOutlined } from '@ant-design/icons-vue'
import HomePege from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import AddPicturePage from '@/pages/picture/AddPicturePage.vue'
import PictureManagePage from '@/pages/admin/PictureManagePage.vue'
import PictureDetailPage from '@/pages/picture/PictureDetailPage.vue'
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
  {
    path: '/admin/pictureManage',
    name: '图片管理',
    title: '图片管理',
    component: PictureManagePage,
  },
  {
    path: '/picture/:id',
    name: '图片详情',
    component: PictureDetailPage,
    // 允许将路由的参数作为组件的props传递给相应的组件
    props: true,
  }



]
