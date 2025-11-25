<template>
  <el-dropdown
      class="dropdown"
      @command="navigateTo"
      trigger="hover"
  >
    <!-- 显示下拉菜单的按钮 -->
    <template #default>
      <span class="el-dropdown-link">
        Analyse
      </span>
    </template>

    <!-- 下拉菜单内容 -->
    <template #dropdown>
      <el-dropdown-menu class="dropdown-menu">
        <el-dropdown-item class="dropdown-item" command="/godistribution">
          GO Distribution
        </el-dropdown-item>
        <el-dropdown-item class="dropdown-item" command="/pathwaydistribution">
          Pathway Distribution
        </el-dropdown-item>
        <el-dropdown-item class="dropdown-item" command="/enrichmentnetwork">
          Enrichment Network
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script>
export default {
  methods: {
    navigateTo(route) {
      this.$router.push(route); // 路由跳转
      this.$emit("updateActiveIndex", "4"); // 通知父组件更新导航栏高亮到 Analyse
    },
  },
};
</script>

<style src="@/assets/css/queryDrop.css"></style>
