<template>
  <div class="picture-list">
    <!-- 图片列表 -->
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :data-source="dataList"
      :loading="loading"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0">
          <!-- 单张图片 -->
          <!-- hoverable  鼠标悬停的时候有阴影效果 -->
          <a-card hoverable @click="doClickPicture(picture)">
            <template #cover>
              <!-- 每张图片的高宽都是不一样的，设置统一的高宽 用object-fit: cover 优化图片展示效果，不会受到挤压 -->
              <!-- 暂时不处理缩略图  :src =" picture.thumbnailUrl ?? picture.url" -->
              <img
                style="height: 180px; object-fit: cover"
                :alt="picture.picName"
                :src="picture.url"
                :loading="lazy"
              />
            </template>
            <a-card-meta :title="picture.picName">
              <template #description>
                <a-flex>
                  <a-tag color="green">
                    {{ picture.category ?? '默认' }}
                  </a-tag>
                  <a-tag v-for="tag in picture.tags" :key="tag">
                    {{ tag }}
                  </a-tag>
                </a-flex>
              </template>
            </a-card-meta>
            <template v-if="showOp" #actions>
              <a-sapce @click="(e) => doSearch(picture, e)">
                <SearchOutlined key="search" />
                搜索
              </a-sapce>
              <a-sapce @click="(e) => doEdit(picture, e)">
                <EditOutlined key="edit" />
                编辑
              </a-sapce>
              <a-space @click="(e) => doDelete(picture, e)">
                <DeleteOutlined key="delete" />
                删除
              </a-space>
            </template>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>
<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { DeleteOutlined, EditOutlined, SearchOutlined } from '@ant-design/icons-vue'
import { deletePictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
  showOp?: boolean
  onReload?: () => {}
}

const props = withDefaults(defineProps<Props>(), {
  dataList: () => [],
  loading: false,
})

const router = useRouter()
// 跳转至图片详情
const doClickPicture = (picture) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}

// 编辑
const doEdit = (picture, e) => {
  // 阻止冒泡
  e.stopPropagation()
  router.push({
    path: '/add_picture',
    query: {
      id: picture.id,
      spaceId: picture.spaceId,
    },
  })
}

// 搜索
const doSearch = (picture, e) => {
  e.stopPropagation()
  window.open(`/search_picture?pictureId=${picture.id}`)
}

// 删除
const doDelete = async (picture, e) => {
  // 阻止冒泡
  e.stopPropagation()
  const id = picture.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    props.onReload?.()
  } else {
    message.error('删除失败')
  }
}
</script>
<style scoped></style>
