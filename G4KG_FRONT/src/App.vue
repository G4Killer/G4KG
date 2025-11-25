<template>
  <el-container style="min-height: 100vh; /* 确保覆盖整个视口高度 */">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <HeaderCom :activeIndex="activeIndex" />
    </el-header>

    <!-- 主体内容部分 -->
    <el-main class="main-content">
      <router-view />
    </el-main>

    <!-- 底部版权信息 -->
    <el-footer class="footer">
      <FooterCom />
    </el-footer>
  </el-container>
</template>

<script>
import HeaderCom from "@/components/HeaderCom.vue";
import FooterCom from "@/components/FooterCom.vue";

export default {
  components: {
    HeaderCom,
    FooterCom,
  },
  data() {
    return {
      activeIndex: "1", // 默认选中的导航项
    };
  },
};
</script>

<style scoped>
/* 顶部导航栏 */
.header {
  background-color: #2c6a7f;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: 60px;
  z-index: 1000;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
}

/* 主体内容 */
.main-content {
  margin-top: 60px; /* 留出顶部导航栏空间 */
  margin-bottom: 50px; /* 留出底部栏空间 */
  padding: 0;
  overflow-y: auto;
  height: calc(100vh - 110px); /* 动态计算高度 (导航栏 + 底部高度) */
  background-color: #f5f7fa;
}

/* 底部栏 */
.footer {
  background-color: #2c6a7f;
  height: 50px;
  z-index: 1000;
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  text-align: center;
  line-height: 50px;
  color: #fff;
}

</style>
