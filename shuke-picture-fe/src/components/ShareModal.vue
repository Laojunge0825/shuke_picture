<template>
  <a-modal v-model:visible="visible" title="分享图片" :footer="false" @cancel="closeModal">
    <h4>复制分享链接</h4>
    <a-typography-link copyable>
      {{ shareLink }}
    </a-typography-link>
    <div style="margin-bottom: 16px" />
    <h4>分享二维码</h4>
    <div>
      <a-qrcode :value="shareLink" />
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import { ref } from 'vue'

/**
 * 定义组件属性类型
 */
interface Props {
  title: string
  shareLink: string
}

// 指定初始值
const props = withDefaults(defineProps<Props>(), {
  title: () => '分享',
  shareLink: () => 'https://www.baidu.com',
})

// 是否可见
const visible = ref(false)

// 打开弹窗
const openModal = () => {
  visible.value = true
}

// 关闭弹窗
const closeModal = () => {
  visible.value = false
}

/**
 * 暴露给父组件调用
 */
defineExpose({
  openModal,
})
</script>
<style scoped></style>
