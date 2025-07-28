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
import { computed, h, ref, watchEffect } from 'vue'
import { PictureOutlined , CloudOutlined , TeamOutlined} from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { routes } from '@/router/routes'
import { useLoginUserStore } from "@/stores/user/useLoginUserStore.ts"
import { SPACE_TYPE_ENUM, SPACE_TYPE_MAP } from '@/constants/space.ts'
import { message } from 'ant-design-vue'
import { listMyTeamSpaceUsingPost } from '@/api/spaceUserController.ts'

const router = useRouter();
const current = ref<string[]>(['home'])
const items = ref(null);
const loginUserStore = useLoginUserStore()

// 固定菜单项
const fixedmenuItems = [
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
  {
    key: '/add_space?type='+ SPACE_TYPE_ENUM.TEAM,
    icon: () => h(TeamOutlined),
    label:'创建团队空间',
    title:'创建团队空间',
  },
]

// 用户已参与的团队列表
const teamSpaceList = ref<API.SpaceUserVO[]>([])
const menuItems = computed(() => {
  // 如果没有团队空间
  if (teamSpaceList.value.length === 0){
    return fixedmenuItems;
  }else{
    // 展示团队空间分组
    const teamSpaceSubMenus = teamSpaceList.value.map((spaceUser) => {
      const space = spaceUser.space
      return {
        key: '/space/' + spaceUser.spaceId,
        label: space?.spaceName,
      }
    })
    const teamSpaceMenuGroup = {
      type: 'group',
      label: '我的团队',
      key: 'teamSpace',
      children: teamSpaceSubMenus,
    }
    return [...fixedmenuItems, teamSpaceMenuGroup]
  }
})

// 加载团队空间列表
const fetchTeamSpaceList = async () => {
  const res = await listMyTeamSpaceUsingPost()
  if (res.data.code === 0 && res.data.data) {
    teamSpaceList.value = res.data.data
  } else {
    message.error('加载我的团队空间失败，' + res.data.message)
  }
}

/**
 * 监听变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  // 登录才加载
  if (loginUserStore.loginUser.id) {
    fetchTeamSpaceList()
  }
})


const doMenuClick =({ key } : { key : string }) => {
  router.push(key)
}



// 将目标页面的路由赋值给current  实现菜单栏同步高亮
router.afterEach((to)=>{
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

