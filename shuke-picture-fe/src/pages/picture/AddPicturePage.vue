<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query?.id ? '修改图片' : '创建图片' }}
    </h2>
    <a-typography-paragragh v-if="spaceId" type="secondary">
      保存至空间：<a :href="`/space/${spaceId}`">{{ spaceId }}</a>
    </a-typography-paragragh>
    <!-- 选择上传方式 -->
    <a-tabs v-model:activeKey="uploadType">
      <a-tab-pane key="file" tab="文件上传">
        <!-- 图片上传组件 -->
        <PictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
      <a-tab-pane key="url" tab="URL 上传" force-render>
        <!-- URL 图片上传组件 -->
        <UrlPictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
    </a-tabs>

    <!-- 编辑图片 -->
    <div v-if="picture" class="edit-bar">
      <a-space size="middle">
        <a-button :icon="h(EditOutlined)" @click="doEditPicture">编辑图片</a-button>
        <a-button :icon="h(FullscreenOutlined)" @click="doImagePainting">AI扩图</a-button>
      </a-space>
      <!-- 图片裁剪组件 -->
      <ImageCropper
        ref="imageCropperRef"
        :imageUrl="picture?.url"
        :picture="picture"
        :spaceId="spaceId"
        :onSuccess="onCropSuccess"
      />
      <!-- 图片扩图组件 -->
      <ImageOutPainting
        ref="imageOutPaintingRef"
        :imageUrl="picture?.url"
        :picture="picture"
        :space="spaceId"
      />
    </div>
    <a-form layout="vertical" :model="pictureForm" @finish="handleSubmit" v-if="picture">
      <a-form-item label="名称" name="picName">
        <a-input v-model:value="pictureForm.picName" placeholder="请输入名称" />
      </a-form-item>
      <a-form-item label="简介" name="introduction">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="请输入简介"
          :rows="2"
          autoSize
          allowClear
        />
      </a-form-item>
      <a-form-item label="分类" name="category">
        <a-auto-complete
          v-model:value="pictureForm.category"
          placeholder="请输入分类"
          :options="categoryOptions"
          allowClear
        />
      </a-form-item>
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="pictureForm.tags"
          mode="tags"
          placeholder="请输入标签"
          :options="tagOptions"
          allowClear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">提交</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref, onMounted, computed, h } from 'vue'
import { message } from 'ant-design-vue'
import PictureUpload from '@/components/PictureUpload.vue'
import UrlPictureUpload from '@/components/UrlPictureUpload.vue'
import ImageCropper from '@/components/ImageCropper.vue'
import ImageOutPainting from '@/components/ImageOutPainting.vue'
import { EditOutlined, FullscreenOutlined } from '@ant-design/icons-vue'
import {
  listPictureTagCategoryUsingGet,
  editPictureUsingPost,
  getPictureVoByIdUsingGet,
} from '@/api/pictureController.ts'
import { useRouter, useRoute } from 'vue-router'

const picture = ref<API.PictureVO>()
const pictureForm = reactive<API.PictureEditDTO>({})

// 上传方式
const uploadType = ref<'file' | 'url'>('file')

const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

// 获取路由的实例对象 操做页面前进、后退、跳转
const router = useRouter()
// 获取当前路由对象 访问当前路由的信息 如参数信息
const route = useRoute()

// 空间id
const spaceId = computed(() => {
  return route.query?.spaceId
})

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()

  if (res.data.code === 0 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('加载选项失败，' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})

// 图片上传成功后 回显到表单
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.picName = newPicture.picName
}

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const pictureId = picture.value.id
  if (!pictureId) {
    return
  }
  const res = await editPictureUsingPost({
    id: pictureId,
    spaceId: spaceId.value,
    ...values,
  })
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success('操作成功')
    // 跳转到图片详情页
    router.push({
      path: `/picture/${pictureId}`,
    })
  } else {
    message.error('操作失败，' + res.data.message)
  }
}

// 获取老数据
const getOldPicture = async () => {
  // 获取到 id
  const id = route.query?.id
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
      pictureForm.picName = data.picName
      pictureForm.introduction = data.introduction
      pictureForm.category = data.category
      pictureForm.tags = data.tags
    }
  }
}

onMounted(() => {
  getOldPicture()
})

// 图片编辑弹窗引用
const imageCropperRef = ref()

// 编辑图片
const doEditPicture = () => {
  if (imageCropperRef.value) {
    imageCropperRef.value.openModal()
  }
}

// 编辑成功事件
const onCropSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}

// ---------------------------- 图片扩图弹窗引用

// AI扩图弹窗引用
const imageOutPaintingRef = ref()

const doImagePainting = () => {
  if (imageOutPaintingRef.value) {
    imageOutPaintingRef.value.openModal()
  }
}

const onImageOutPaintingSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}
</script>
<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}

#addPicturePage .edit-bar {
  text-align: center;
  margin: 16px 0;
}
</style>
