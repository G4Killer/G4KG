<template>
  <div class="footer-container">
    <div>{{ copyright }}</div>
  </div>
</template>

<script>
export default {
  props: {
    copyright: {
      type: String,
      default: "© 2024 数据库项目. 版权所有.", // 默认版权信息
    },
  },
};
</script>

<style scoped>
.footer-container {
  background-color: #2c6a7f; /* 与导航栏颜色一致 */
  color: white;
  text-align: center;
  padding: 10px;
  font-size: 14px;
}
</style>
