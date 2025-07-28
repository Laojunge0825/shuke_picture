<template>
  <div id="addSpacePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query?.id ? '修改空间' : '创建空间' }} {{SPACE_TYPE_MAP[spaceType]}}
    </h2>
    <a-form layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item label="空间名称" name="spaceName">
        <a-input v-model:value="formData.spaceName" placeholder="请输入空间名称" allow-clear />
      </a-form-item>
      <a-form-item label="空间级别" name="spaceLevel">
        <a-select
          v-model:value="formData.spaceLevel"
          :options="SPACE_LEVEL_OPTIONS"
          placeholder="请输入空间级别"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          提交
        </a-button>
      </a-form-item>
    </a-form>

    <!-- 空间级别信息 -->
    <a-card title="空间级别介绍">
      <a-typography-paragraph>
        * 目前仅支持开通普通版，如需升级空间，请邮件联系
        <span style="color: #1890ff; font-weight: bold;">shukeshuke_pcj@163.com</span>*
      </a-typography-paragraph>
      <a-typography-paragraph v-for="spaceLevel in spaceLevelList">
        {{ spaceLevel.text }}： 大小 {{ formatSize(spaceLevel.maxSize) }}， 数量
        {{ spaceLevel.maxCount }}
      </a-typography-paragraph>
    </a-card>


  </div>
</template>
<script lang="ts" setup>
import { reactive, ref, onMounted, computed } from 'vue'
import { SPACE_LEVEL_ENUM, SPACE_LEVEL_OPTIONS, SPACE_TYPE_ENUM, SPACE_TYPE_MAP } from '@/constants/space.ts'
import { message } from 'ant-design-vue'
import { useRouter , useRoute } from 'vue-router'
import { addSpaceUsingPost , listSpaceLevelUsingGet , getSpaceVoByIdUsingGet , updateSpaceUsingPost} from '@/api/SpaceController.ts'
import { formatSize } from "@/utils"

const space = ref<API.SpaceVO>()

const formData = reactive<API.SpaceAddDTO | API.SpaceUpdateDTO | API.SpaceEditDTO >({
  spaceName: '',
  spaceLevel: SPACE_LEVEL_ENUM.COMMON,
})
const loading = ref(false);

const router = useRouter();
// 获取当前路由对象 访问当前路由的信息 如参数信息
const route = useRoute()

//  空间类别 默认为私有空间
const spaceType = computed(() => {
  if(route.query?.type){
    return Number(route.query.type)
  }else {
    return SPACE_TYPE_ENUM.PRIVATE
  }

})


/**
 * 提交表单
 */
const handleSubmit = async (values: any) => {
  const spaceId = space.value?.id;
  loading.value = true;
  let res;
  if(spaceId){
    // 更新空间
  res = await updateSpaceUsingPost({
    id:spaceId,
    ...formData,
  })
  }else{
    // 创建空间
    res = await addSpaceUsingPost({
    ...formData,
      spaceType: spaceType.value,
  })
  }

  if (res.data.code === 0 && res.data.data) {
    message.success("操作成功")
    router.push({
      path: `/space/${spaceId}`,
    })
  } else {
    message.error('操作失败，' + res.data.message)
  }
  loading.value = false;
}


const spaceLevelList = ref<API.SpaceLevel[]>([])

// 获取空间级别
const fetchSpaceLevelList = async () => {
  const res = await listSpaceLevelUsingGet()
  if (res.data.code === 0 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error('加载空间级别失败，' + res.data.message)
  }
}

// 获取老数据
const getOldPicture = async () => {
  // 获取到 id
  const id = route.query?.id
  if (id) {
    const res = await getSpaceVoByIdUsingGet({
      id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      space.value = data
      formData.spaceName = data.spaceName
      formData.spaceLevel = data.spaceLevel
    }
  }
}

onMounted(() => {
  fetchSpaceLevelList(),
  getOldPicture()
})





</script>

<style scoped>
#addSpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
