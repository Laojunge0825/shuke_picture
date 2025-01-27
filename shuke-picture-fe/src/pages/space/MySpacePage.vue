<template>
  <div id="mySpacePage">
  </div>
</template>
<script lang="ts" setup>
import { useRouter } from "vue-router";
import { useLoginUserStore } from '@/stores/user/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import { listSpaceVoByPageUsingPost } from '@/api/spaceController.ts'
import { onMounted } from 'vue';

const router = useRouter();
const loginUserStore = useLoginUserStore();

const checkUserSpace = async () => {
  const loginUser = loginUserStore.loginUser;
  if(!loginUser?.id){
     router.replace("/user/login");
     message.error("请登录后查看");
     return
  }

  const res = await listSpaceVoByPageUsingPost({
    userId:loginUser.id,
    current:1,
    pageSize:1
  })
  if(res.data.code === 0){
    if(res.data.data?.records?.length > 0){
      const space = res.data.data.records[0]
      router.replace(`/space/${space.id}`)
    }else {
      router.replace('/add_space')
      message.warn("请先创建空间")
    }
  }else {
    message.error("加载我的空间失败，"+res.data.message)
  }

}
onMounted(() => {

    checkUserSpace()
  })

</script>

<style scoped>
#mySpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
