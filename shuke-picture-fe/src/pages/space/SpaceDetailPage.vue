<template>
  <div id="spaceDetailPage">
    <!-- 空间信息 -->
    <a-flex justify="space-between">
      <h2>{{ space.spaceName }}(私人空间)</h2>
      <a-space>
        <a-button type="primary" :href="`/add_picture?spaceId=${id}`" target="_blank">
          + 创建图片
        </a-button>
        <a-tooltip
          :title="`占用空间 ${formatSize(space.totalSize)} / ${formatSize(space.maxSize)}`"
        >
          <a-progress
            type="circle"
            :precent="((space.totalSize * 100) / space.maxSize).toFixed(1)"
            :size="42"
          />
        </a-tooltip>
      </a-space>
    </a-flex>

    <!-- 搜索表单 -->
    <PictureSearchForm :onSearch="onSearch" />
    <div style="margin-bottom: 16px" />
    <!-- 按颜色搜索  注意format 设置成hex，得到十六进制的颜色值-->
    <a-form-item label="按颜色搜索" style="margin-top: 16px">
      <ColorPicker format="hex" @pureColorChange="onColorChange" />
    </a-form-item>

    <div style="margin-bottom: 16px"></div>
    <!-- 图片列表 -->
    <PictureList :dataList="dataList" :loading="loading" :showOp="true" :onReload="fetchData" />

    <!-- 分页 -->
    <a-pagination
      style="text-align: right"
      v-model:current="searchParams.current"
      v-model:pageSize="searchParams.pageSize"
      :total="total"
      @change="onPageChange"
    />
  </div>
</template>
<script lang="ts" setup>
import { ref, onMounted, computed, reactive } from 'vue'
import { getSpaceVoByIdUsingGet, deleteSpaceUsingPost } from '@/api/spaceController.ts'
import {
  listPictureVoByPageUsingPost,
  searchPictureByColorUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/user/useLoginUserStore'
import { useRouter } from 'vue-router'
import { formatSize } from '@/utils'
import { ColorPicker } from 'vue3-colorpicker'
import 'vue3-colorpicker/style.css'
import PictureList from '@/components/PictureList.vue'
import PictureSearchForm from '@/components/PictureSearchForm.vue'

const props = defineProps<{
  id: string | number
}>()

const space = ref<API.SpaceVO>({})

const router = useRouter()

// 获取空间详情
const fetchSpaceDetail = async () => {
  try {
    const res = await getSpaceVoByIdUsingGet({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      space.value = res.data.data
    } else {
      message.error('获取空间详情失败，' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取空间详情失败：' + e.message)
  }
}

onMounted(() => {
  fetchSpaceDetail()
})

// 定义数据
const dataList = ref<API.PictureVO[]>([])
const total = ref(0)
const loading = ref(true)

// 搜索条件  搜索参数可能被置空 所以将searchParams 从reactive 变成 ref，这样可以整体给它赋值为空对象
const searchParams = ref<API.PictureQueryDTO>({
  current: 1,
  pageSize: 10,
  sortField: 'create_time',
  sortOrder: 'descend',
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.value.current ?? 1,
    pageSize: searchParams.value.pageSize ?? 10,
    total: total.value,
  }
})

// 切换页号时，修改参数并获取数据
const onPageChange = (page: number, pageSize: number) => {
  searchParams.value.current = page
  searchParams.value.pageSize = pageSize
  fetchData()
}

// 搜索
const onSearch = (newSearchParams: API.PictureQueryDTO) => {
  searchParams.value = {
    ...searchParams.value,
    ...newSearchParams,
    current: 1,
  }
  fetchData()
}

/**
 *  获取数据
 */
const fetchData = async () => {
  loading.value = true
  // 转换搜索参数
  const params = {
    ...searchParams.value,
    spaceId: props.id,
  }

  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  loading.value = false
}

onMounted(() => {
  fetchData()
})

/**
 * 切换颜色事件 切换颜色时会触发搜索
 */
const onColorChange = async (color: string) => {
  const res = await searchPictureByColorUsingPost({
    picColor: color,
    spaceId: props.id,
  })
  if (res.data.code === 0 && res.data.data) {
    const data = res.data.data ?? []
    dataList.value = data
    total.value = data.length
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}
</script>
<style scoped>
#spaceDetailPage {
  margin-bottom: 16px;
}
</style>
