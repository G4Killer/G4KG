<template>
  <div class="homepage-container">
    <!-- 页面标题 -->
    <div class="header-content">
      <h1>Welcome to G4KG Database</h1>
      <p>A graph database for exploring and analyzing G4-related knowledge.</p>
    </div>

    <!-- 第一部分: 数据库简介 -->
    <div class="section introduction-section">
      <h2 class="section-title">Introduction</h2>
      <div class="section-content">
        <p>
          G4KG is a comprehensive graph database designed for exploring and analyzing G-quadruplex-related knowledge. It provides insights into the relationships between G4 structures and their biological and functional contexts, supporting research in genomics, epigenomics, and more.
        </p>
      </div>
    </div>

    <!-- 第二部分: 数据库功能概览 -->
    <div class="section overview-section">
      <h2 class="section-title">Overview</h2>
      <div class="feature-container">
        <!-- Search 部分 -->
        <div class="feature-item">
          <img src="@/assets/photo/search.png" alt="Search" class="feature-image" />
          <h3>Search</h3>
          <p>Explore G4-related data by searching nodes, relationships, and their attributes in the database.</p>
          <div class="sub-feature-buttons">
            <button class="btn-link" @click="navigate('/attribute-query')">
              <span class="btn-text">Attribute Query</span>
              <span class="btn-icon">→</span>
            </button>
            <button class="btn-link" @click="navigate('/relation-query')">
              <span class="btn-text">Relation Query</span>
              <span class="btn-icon">→</span>
            </button>
          </div>
        </div>

        <!-- Visualization 部分 -->
        <div class="feature-item">
          <img src="@/assets/photo/visualization.png" alt="Visualization" class="feature-image" />
          <h3>Visualization</h3>
          <p>Visualize G4 interactions and relationships in a graphical format for better insights.</p>
          <button class="btn-link" @click="navigate('/visualization')">
            <span class="btn-text">Go to Visualization</span>
            <span class="btn-icon">→</span>
          </button>
        </div>

        <!-- Analyse 部分 -->
        <div class="feature-item">
          <img src="@/assets/photo/analyse.png" alt="Analyse" class="feature-image" />
          <h3>Analyse</h3>
          <p>Analyze G4 functional distributions, pathways, and enrichment networks.</p>
          <div class="sub-feature-buttons">
            <button class="btn-link" @click="navigate('/godistribution')">
              <span class="btn-text">Go Distribution</span>
              <span class="btn-icon">→</span>
            </button>
            <button class="btn-link" @click="navigate('/pathwaydistribution')">
              <span class="btn-text">Pathway Distribution</span>
              <span class="btn-icon">→</span>
            </button>
            <button class="btn-link" @click="navigate('/enrichmentnetwork')">
              <span class="btn-text">Enrichment Network</span>
              <span class="btn-icon">→</span>
            </button>
          </div>
        </div>

        <!-- Neo4jBroswer 部分 -->
        <div class="feature-item">
          <img src="@/assets/photo/neo4jBroswer.png" alt="Neo4jBroswer" class="feature-image" />
          <h3>Neo4j Broswer</h3>
          <p>Visualize G4 interactions and relationships in a graphical format for better insights.</p>
          <button class="btn-link" @click="navigate('/custom-query')">
            <span class="btn-text">Open the Neo4j browser</span>
            <span class="btn-icon">→</span>
          </button>
        </div>

        <!-- Download 部分 -->
        <div class="feature-item">
          <img src="@/assets/photo/download.png" alt="Download" class="feature-image" />
          <h3>Download</h3>
          <p>Visualize G4 interactions and relationships in a graphical format for better insights.</p>
          <button class="btn-link" @click="navigate('/download')">
            <span class="btn-text">Go to Download</span>
            <span class="btn-icon">→</span>
          </button>
        </div>

        <!-- Help 部分 -->
        <div class="feature-item">
          <img src="@/assets/photo/help.png" alt="Help" class="feature-image" />
          <h3>Help</h3>
          <p>Visualize G4 interactions and relationships in a graphical format for better insights.</p>
          <button class="btn-link" @click="navigate('/help')">
            <span class="btn-text">Go to Help</span>
            <span class="btn-icon">→</span>
          </button>
        </div>

      </div>
    </div>
    <!-- 第三部分: 关于 G4KG -->
    <div class="section about-section">
      <h2 class="section-title">About</h2>

      <!-- 子部分: What is G4KG? -->
      <div class="sub-section">
        <div class="sub-content">
          <div class="text-content">
            <h2>What is G4KG?</h2>
            <p>
              G4KG is a specialized knowledge graph designed to analyze and visualize G-quadruplex (G4) interactions and relationships in biological systems. The knowledge graph includes various entities such as genes, proteins, pathways, and diseases.<br>
                Right is the brief metagraph of the G4KG, If you want more details,Please go to the Help Page.
            </p>
            <button class="btn-link" @click="navigate('/help')">
              <span class="btn-text">For more Details about the KG</span>
              <span class="btn-icon">→</span>
            </button>
          </div>
          <div class="image-content">
            <img src="@/assets/photo/metagraph.png" alt="G4KG Knowledge Graph Metagraph" class="about-image" />
            <p class="image-caption">Figure: Overview of the G4KG Metagraph</p> <!-- 添加说明性文字 -->
          </div>
        </div>
        <hr />
      </div>

      <!-- 子部分: What's in G4KG? -->
      <div class="sub-section">
        <div class="sub-content">
          <div class="text-content">
            <h3>What's in G4KG?</h3>
            <p>
              G4KG integrates data from multiple sources, including experimental datasets, literature, and curated databases. The distribution of entities covers various biological contexts such as epigenomics, genomics, and transcriptomics.
            </p>
          </div>
          <div class="image-content">
            <img src="@/assets/photo/datadistribution.png" alt="G4KG Data Distribution" class="about-image" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    navigate(route) {
      this.$router.push(route);
    },
  },
};
</script>

<style scoped>
/* 整体容器样式 */
.homepage-container {
  margin: 0;
  padding: 0;
  min-height: 100vh;
  background: url('@/assets/photo/background.jpg') no-repeat center center fixed;
  background-size: cover;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
}

/* 页面标题样式 */
.header-content {
  margin-top: 20px;
  margin-bottom: 30px;
  text-align: center;
}

.header-content h1 {
  font-size: 3rem;
  margin-bottom: 10px;
  font-weight: bold;
}

.header-content p {
  font-size: 1.5rem;
  line-height: 1.6;
}

/* 通用部分样式 */
.section {
  margin: 0 20px 20px 20px; /* 上方 margin 为 0，底部为 20px */
  width: 80%;
  background-color: rgba(255, 255, 255, 0.9);
  color: #333;
  border-radius: 10px;
  padding: 0;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* 主标题样式 */
.section-title {
  background-color: lightgray; /* 主标题容器背景颜色 */
  padding: 20px;
  border-radius: 10px;
  font-size: 2rem;
  text-align: center;
  margin: 0 0 10px 0; /* 上方 margin 为 0，底部为 20px */
}

.section-content {
  padding: 10px; /* 保留除标题以外的 padding */
}

.section-content p{
  flex: 1;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  font-size: 1.5rem;
  line-height: 1.5;
}


/* 功能概览样式 */
.feature-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
  padding: 10px;
}

.feature-item {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.feature-image {
  max-width: 100%;
  height: 150px;
  object-fit: cover;
  margin-bottom: 10px;
}

.feature-item h3 {
  font-size: 1.5rem;
  margin-bottom: 10px;
}

.feature-item p {
  font-size: 1rem;
  margin-bottom: 15px;
}

/* About 容器样式 */
.about-section {
  display: flex;
  flex-direction: column;
}

/* 子部分样式 */
.sub-section {
  margin-bottom: 20px;
  padding: 10px;
}

.sub-content {
  display: flex;
  flex-direction: row; /* 横向排列文字和图片 */
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

/* 文字部分样式 */
.text-content h2{
  font-size: 2rem;

}

.text-content {
  flex: 1;
  text-align: left;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  font-size: 1.5rem;
  line-height: 1.5;
}

/* 图片部分样式 */
.image-content {
  flex: 1;
  display: flex;
  flex-direction: column; /* 垂直排列图片和说明文字 */
  justify-content: center;
  align-items: center;
}

.about-image {
  max-width: 100%; /* 图片宽度适配容器 */
  height: auto; /* 保持图片比例 */
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  object-fit: cover;
}

/* 图片说明文字样式 */
.image-caption {
  margin-top: 10px; /* 图片与说明文字之间的间距 */
  font-size: 1rem; /* 说明文字字体大小 */
  color: #666; /* 说明文字颜色 */
  text-align: center; /* 居中对齐说明文字 */
}


/* 分割线样式 */
hr {
  border: none;
  border-top: 1px solid #ccc;
  margin: 20px 0;
}

/* 子功能按钮组 */
.sub-feature-buttons {
  display: flex;
  flex-direction: column; /* 纵向排列 */
}

/* 替换按钮样式 */
.btn-link {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 10px 15px;
  font-size: 1rem;
  color: #007bff;
  background-color: transparent;
  border: none;
  cursor: pointer;
  text-align: left;
  transition: color 0.3s ease;
}

.btn-link:hover {
  color: #0056b3;
  text-decoration: underline;
}

.btn-text {
  margin-right: 10px;
}

.btn-icon {
  font-weight: bold;
  font-size: 1.4rem;
  transform: translateX(0);
  transition: transform 0.3s ease;
}

.btn-link:hover .btn-icon {
  transform: translateX(5px);
}

</style>

