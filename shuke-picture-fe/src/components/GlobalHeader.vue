<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <div class="title">舒克云图库</div>
          </div>
        </RouterLink>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </a-col>
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
                <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </a-space>
                <template #overlay>
                 <a-menu>
                    <a-menu-item>
                      <router-link to="/my_space">
                        我的空间
                      </router-link>
                    </a-menu-item>
                    <a-menu-item @click="doLogout">
                      <LogoutOutlined />
                      退出登录
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>

  </div>
</template>
<script lang="ts" setup>
import { computed, h, ref } from 'vue'
// import { HomeOutlined,LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
import { LogoutOutlined } from '@ant-design/icons-vue'
import { MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { routes } from '@/router/routes'
import { useLoginUserStore } from "@/stores/user/useLoginUserStore"
import { userLogOutUsingGet } from '@/api/userController'

const router = useRouter();
const current = ref<string[]>(['home'])
const items = ref(null);
const loginUserStore = useLoginUserStore()

const routesItems = ()=>{
   items.value =  routes
    .map((item) => {
      return {
        key: item.path,
        label: item.name,
        title: item.name,
        icon: item.meta?.icon,
      }
    })
}
routesItems()

const doMenuClick =({ key } : { key : string }) => {
  router.push({
    path: key,
  })
}


// 用户注销
const doLogout = async () => {
  const res = await userLogOutUsingGet()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}


// 将目标页面的路由赋值给current  实现菜单栏同步高亮
router.afterEach((to,from,next)=>{
  current.value = [to.path];
})
</script>
<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>

