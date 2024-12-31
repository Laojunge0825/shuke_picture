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
            <a-button type="primary" href="/user/login">登录</a-button>
        </div>
      </a-col>
    </a-row>

  </div>
</template>
<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import { HomeOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { routes } from '@/router/routes.ts'
const router = useRouter();
const current = ref<string[]>(['home'])
const items = ref(null);
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

