<template>
  <div class="neo4j-browser-page">
    <!-- 页面标题 -->
    <div class="browser-title">
      <h1>Neo4j Browser</h1>
      <p>
        Neo4j is a third-party graph database that supports the Cypher query language, allowing users to query and visualize knowledge graphs.
      </p>
    </div>

    <!-- 第一个子部分: 如何访问Neo4j -->
    <div class="browser-section">
      <h2>How to Access Neo4j</h2>
      <div class="browser-content">
        <div class="text-content">
          <p>
            To access the Neo4j Browser, ensure that the database service is running. Click the button below to open the browser in a new tab.
          </p>
        </div>
        <div class="image-content">
          <img
              src="@/assets/photo/neo4j_step1.png"
              alt="Accessing Neo4j"
              class="browser-image"
          />
        </div>
      </div>
    </div>

    <!-- 第二个子部分: 如何运行Cypher查询 -->
    <div class="browser-section">
      <h2>How to Run Cypher Queries</h2>
      <div class="browser-content">
        <div class="text-content">
          <p>
            Use the Cypher query editor in the Neo4j Browser to run queries. You can type or paste Cypher commands to retrieve and visualize data from the knowledge graph.
          </p>
        </div>
        <div class="image-content">
          <img
              src="@/assets/photo/neo4j_step2.png"
              alt="Running Cypher Queries"
              class="browser-image"
          />
        </div>
      </div>
    </div>

    <!-- 第三个子部分: 如何导出数据 -->
    <div class="browser-section">
      <h2>How to Export Data</h2>
      <div class="browser-content">
        <div class="text-content">
          <p>
            Neo4j allows users to export query results for further analysis. Use the export options in the browser to download the data in CSV or other formats.
          </p>
        </div>
        <div class="image-content">
          <img
              src="@/assets/photo/neo4j_step3.png"
              alt="Exporting Data"
              class="browser-image"
          />
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <a
        class="open-browser-link"
        href="http://10.201.1.80:7474/browser/"
        target="_blank"
    >
      Let's go to the Neo4j Browser →
    </a>
  </div>
</template>

<script>
export default {
  name: "Neo4jBrowserPage",
};
</script>

<style scoped>
/* 主页面容器 */
.neo4j-browser-page {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
}

/* 页面标题样式 */
.browser-title h1 {
  font-size: 2rem;
  font-weight: bold;
  color: #222;
  margin-bottom: 10px;
}

.browser-title p {
  font-size: 1rem;
  color: #555;
  line-height: 1.5;
  margin-bottom: 30px;
}

/* 子部分样式 */
.browser-section {
  margin-bottom: 30px;
}

.browser-section h2 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
  border-left: 4px solid #0073e6; /* 左侧增加装饰条 */
  padding-left: 10px;
}

/* 子部分内容布局 */
.browser-content {
  display: flex;
  flex-direction: column; /* 垂直排列 */
  align-items: center; /* 居中对齐 */
  gap: 20px; /* 图片和文字之间的间距 */
}

/* 文本内容样式 */
.text-content p {
  font-size: 1.5rem;
  line-height: 1.6;
  color: #555;
}

/* 图片内容样式 */
.image-content {
  text-align: center; /* 确保图片在容器内居中显示 */
  display: flex; /* 结合 flex 布局 */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
}

.image-content img {
  max-width: 80%;
  height: auto;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  margin: auto;
}

/* 操作按钮样式 */
.open-browser-link {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #0073e6;
  color: #fff;
  text-decoration: none;
  border-radius: 5px;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.open-browser-link:hover {
  background-color: #005bb5;
}
</style>
