<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
       <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="https://www.baidu.com" target="_blank"> 舒克、舒克 </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup>
import GlobalHeader from "@/components/GlobalHeader.vue";
</script>
<style scoped>
#basicLayout .header {
  padding-inline: 20px;
  margin-bottom: 16px;
  color: unset;
  background: #ffffff;
}
#basicLayout .footer {
  background: #ffffff;
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}

#basicLayout .content {
  background: linear-gradient(to right, #e6f5f9, #ace4f1);
  margin-bottom: 26px;
  padding: 20px;
}


</style>
