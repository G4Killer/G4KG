<template>
  <div class="header-container">
    <!-- 标题部分 -->
    <div class="title">G4KG</div>

    <!-- 导航栏菜单 -->
    <el-menu
        mode="horizontal"
        :default-active="activeIndex"
        class="navbar"
    >
      <el-menu-item index="1" @click="navigate('/')">Home</el-menu-item>
      <el-menu-item index="2">
        <QueryDropdown @updateActiveIndex="setActiveIndex" />
      </el-menu-item>
      <el-menu-item index="3" @click="navigate('/visualization')">Visualization</el-menu-item>
      <el-menu-item index="4">
        <AnalyseDropdown @updateActiveIndex="setActiveIndex" />
      </el-menu-item>
      <el-menu-item index="5" @click="navigate('/custom-query')">Neo4jBroswer</el-menu-item>
      <el-menu-item index="6" @click="navigate('/download')">Download</el-menu-item>
      <el-menu-item index="7" @click="navigate('/help')">Help</el-menu-item>
      <el-menu-item index="8" @click="navigate('/chatbot')">ChatBot</el-menu-item>
    </el-menu>
  </div>
</template>

<script>
import QueryDropdown from "@/components/QueryDropdown.vue";
import AnalyseDropdown from "@/components/AnalyseDropdown.vue";

export default {
  components: {
    QueryDropdown,
    AnalyseDropdown,
  },
  data() {
    return {
      activeIndex: "1", // 默认高亮 Home
    };
  },
  methods: {
    navigate(route) {
      this.$router.push(route); // 跳转路由
    },
    setActiveIndex(index) {
      this.activeIndex = index; // 更新导航栏的高亮
    },
    updateActiveIndex(path) {
      const routeToIndex = {
        "/": "1",
        "/attribute-query": "2", // Search 子页面
        "/relation-query": "2",  // Search 子页面
        "/visualization": "3",
        "/godistribution": "4", // Analyse 子页面
        "/pathwaydistribution": "4", // Analyse 子页面
        "/enrichmentnetwork": "4", // Analyse 子页面
        "/custom-query": "5",
        "/download": "6",
        "/help": "7",
        "/chatbot": "8",
      };
      this.activeIndex = routeToIndex[path] || "1"; // 默认 Home
    },
  },
  watch: {
    // 监听路由变化，动态更新导航栏高亮状态
    $route(to) {
      this.updateActiveIndex(to.path);
    },
  },
  created() {
    this.updateActiveIndex(this.$route.path); // 初始化时根据当前路由设置高亮
  },
};
</script>

<style scoped>
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin: 0 auto;
  padding: 0 10px;
}

.title {
  font-size: 35px;
  color: white;
  font-weight: bold;
}

.navbar {
  background-color: transparent;
  display: flex;
  flex-grow: 1;
  justify-content: flex-end;
  align-items: center;
}

.navbar .el-menu-item {
  color: white;
  font-size: 16px;
  padding: 0 15px;
  transition: all 0.3s ease;
  outline: none; /* 去掉默认边框 */
}

.navbar .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.navbar .el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  font-weight: bold;
}
</style>
