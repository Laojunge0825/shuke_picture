<template>
  <div id="globalSider">
        <a-layout-sider v-if="loginUserStore.loginUser.id" width="200"
        breakpoint="lg"
        collapsed-width = "0"
        :style="{ overflow: 'auto', height: '100vh', position: 'fixed', left: 0 }"
        >
          <a-menu
            v-model:selectedKeys="current"
            mode="inline"
            style="height: 100%"
            :items="menuItems"
            @click="doMenuClick"
          >
          </a-menu>
        </a-layout-sider>
  </div>
</template>
<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import { PictureOutlined , CloudOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { routes } from '@/router/routes'
import { useLoginUserStore } from "@/stores/user/useLoginUserStore.ts"
import { userLogOutUsingGet } from '@/api/userController.ts'

const router = useRouter();
const current = ref<string[]>(['home'])
const items = ref(null);
const loginUserStore = useLoginUserStore()


const menuItems = [
  {
    key: '/',
    icon: () => h(PictureOutlined),
    label:'公共图库',
    title:'公共图库',
  },
  {
    key: '/my_space',
    icon: () => h(CloudOutlined),
    label:'我的空间',
    title:'我的空间',
  },
]


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
#globalSider .ant-layout-sider {
  background: none;

}

.page-content {
  margin-left: 200px; /* 侧边栏宽度 */
}

</style>

