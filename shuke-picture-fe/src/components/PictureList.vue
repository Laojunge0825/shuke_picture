<template>
<div class="picture-list">
    <!-- 图片列表 -->
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 4, xxl: 6 }"
      :data-source="dataList"
      :pagination="pagination"
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
          </a-card>
        </a-list-item>
      </template>
    </a-list>

</div>
</template>
<script lang="ts" setup>
import { useRouter } from 'vue-router'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
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


</script>
<style scoped>

</style>
