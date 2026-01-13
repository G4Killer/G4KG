<template>
  <div class="download-container">
    <!-- 页面标题 -->
    <div class="download-title">
      <h1>Download Data</h1>
      <p>Download data available in the G4KG database for further analysis and research.</p>
    </div>

    <!-- 节点数据下载 -->
    <div class="download-sub-section">
      <h2>Nodes Data</h2>
      <ul class="download-list">
        <li>
          <p>Disease data:</p>
          <a href="/data/Disease.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Drug data:</p>
          <a href="/data/Drug.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>G4 (G-quadruplex) data:</p>
          <a href="/data/G4.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Gene data:</p>
          <a href="/data/Gene.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Gene Ontology (GO) data:</p>
          <a href="/data/GO.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Pathway data:</p>
          <a href="/data/Pathway.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Protein data:</p>
          <a href="/data/Protein.csv" class="download-link" download>[Download]</a>
        </li>
      </ul>
    </div>

    <!-- 关系数据下载 -->
    <div class="download-sub-section">
      <h2>Relationships Data</h2>
      <ul class="download-list">
        <li>
          <p>Disease to Disease relationships:</p>
          <a href="/data/DiseaseToDisease.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Drug to Disease relationships:</p>
          <a href="/data/DrugToDisease.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Drug to Drug relationships:</p>
          <a href="/data/DrugToDrug.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Drug to Protein relationships:</p>
          <a href="/data/DrugToProtein.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>G4 to Disease relationships:</p>
          <a href="/data/G4ToDisease.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>G4 to Gene relationships:</p>
          <a href="/data/G4ToGene.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Gene to Disease relationships:</p>
          <a href="/data/GeneToDisease.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Gene to Gene relationships:</p>
          <a href="/data/GeneToGene.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Gene to GO relationships:</p>
          <a href="/data/GeneToGO.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Gene to Pathway relationships:</p>
          <a href="/data/GeneToPathway.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Gene to Protein relationships:</p>
          <a href="/data/GeneToProtein.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>GO to GO relationships:</p>
          <a href="/data/GOToGO.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Pathway to Pathway relationships:</p>
          <a href="/data/PathwayToPathway.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Protein to G4 relationships:</p>
          <a href="/data/ProteinToG4.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Protein to Gene relationships:</p>
          <a href="/data/ProteinToGene.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Protein to GO relationships:</p>
          <a href="/data/ProteinToGO.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Protein to Pathway relationships:</p>
          <a href="/data/ProteinToPathway.csv" class="download-link" download>[Download]</a>
        </li>
        <li>
          <p>Protein to Protein relationships:</p>
          <a href="/data/ProteinToProtein.csv" class="download-link" download>[Download]</a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  name: "DownloadPage",
};
</script>

<style scoped>
/* 主容器 */
.download-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
}

/* 页面标题 */
.download-title h1 {
  font-size: 2rem;
  font-weight: bold;
  color: #222;
  margin-bottom: 10px;
}

.download-title p {
  font-size: 1rem;
  color: #555;
  line-height: 1.5;
  margin-bottom: 30px;
}

/* 子标题样式 */
.download-sub-section h2 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
  border-left: 4px solid #0073e6; /* 左侧增加装饰条 */
  padding-left: 10px;
}

.download-sub-section p {
  flex: 1; /* 让文字占据可用空间 */
  font-size: 1.5rem;
  color: #555;
  margin: 0;
  line-height: 1.5;
}

/* 下载列表样式 */
.download-list {
  list-style-type: disc; /* 使用默认的圆点标记 */
  padding-left: 20px; /* 设置左边距，让圆点有显示空间 */
  margin: 0; /* 去除默认的外边距 */
}

.download-list li {
  display: list-item; /* 恢复默认的列表项行为 */
  margin-bottom: 10px; /* 列表项之间的间距 */
  font-size: 1.5rem; /* 设置字体大小 */
  color: #555; /* 设置文字颜色 */
  line-height: 1.5; /* 调整行间距 */
}

/* 文本样式 */
.download-list li p {
  display: inline; /* 确保段落文字在同一行 */
  margin: 0; /* 去掉默认的外边距 */
}

/* 下载链接样式 */
.download-link {
  color: #0073e6;
  font-weight: bold;
  text-decoration: none;
  margin-left: 5px; /* 文字与链接之间的间距 */
  transition: color 0.3s ease; /* 添加平滑过渡效果 */
}

.download-link:hover {
  text-decoration: underline;
  color: #005bb5;
}
</style>