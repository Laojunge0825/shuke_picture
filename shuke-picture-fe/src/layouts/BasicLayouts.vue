<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
       <GlobalHeader />
      </a-layout-header>
      <a-layout>
        <GlobalSider class="sider" />
        <a-layout-content class="content" :style="{ marginLeft: '200px', marginTop: '64px' }">
          <router-view />
        </a-layout-content>
      </a-layout>

      <a-layout-footer class="footer">
        <a href="https://www.baidu.com" target="_blank"> 舒克、舒克 </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup>
import GlobalHeader from "@/components/GlobalHeader.vue";
import GlobalSider from "@/components/GlobalSider.vue";
</script>
<style scoped>
#basicLayout .header {
   padding: 0 20px;
  margin-bottom: 1px;
  color: unset;
  background: rgb(173, 216, 230);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;

  width: 100%;
  z-index: 1000;
}

#basicLayout .sider {
  background:linear-gradient(to bottom, rgba(193, 230, 245, 0.8), rgba(193, 230, 245, 1));
  border-right: 0.5px solid rgb(238, 238, 238);
  padding-top: 64px;
}

#basicLayout :deep(.ant-menu-root) {
  border-bottom: none !important;
  border-inline-end: none !important;
  background: linear-gradient(to bottom, rgba(193, 230, 245, 0.8), rgba(193, 230, 245, 1));
}

#basicLayout .footer {
  background: linear-gradient(to bottom, rgba(193, 230, 245, 0.8), rgba(193, 230, 245, 1));
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1); /* 阴影效果 */
}

#basicLayout .content {
  background: linear-gradient(to right,  rgb(238, 246, 248), rgb(142, 213, 241));
  margin-bottom: 26px;
  padding: 28px;
}
/*
#basicLayout.header {
    padding: 0 20px;
    margin-bottom: 1px;
    color: white;
    background: rgb(218, 173, 91);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    position: fixed;
    width: 100%;
    z-index: 1000;
}

#basicLayout.sider {
    background: lightgray;
    border-right: 0.5px solid rgb(238, 238, 238);
    padding-top: 64px;
}

#basicLayout :deep(.ant-menu-root) {
    border-bottom: none!important;
    border-inline-end: none!important;
}

#basicLayout.footer {
    background: lightblue;
    padding: 16px;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    text-align: center;
    color: white;
}

#basicLayout.content {
    background: linear-gradient(to right, #e6f5f9, #ace4f1);
    margin-bottom: 26px;
    padding: 28px;
} */


</style>
