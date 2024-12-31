<template>
<div>内容</div>
</template>
<script lang="ts" setup>
import { healthUsingGet } from '@/api/mainController'

healthUsingGet().then((res) => {
  console.log(res)
})

</script>
<style scoped>

</style>
