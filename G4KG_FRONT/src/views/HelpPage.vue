<template>
  <div class="help-page">
    <!-- 页面标题 -->
    <div class="help-title">
      <h1>Help</h1>
      <p>
        This page provides detailed guidance on how to use the G4KG platform, including information about nodes, relationships, and platform features.
      </p>
    </div>

    <!-- 第一个子部分: 详细信息 -->
    <div class="help-section">
      <h2>Details About The G4KG</h2>
      <div class="help-content">

        <!-- 第一张图片和文字说明 -->
        <div class="text-content">
          <p>
            The G4KG knowledge graph consists of nodes representing entities like genes, proteins, and pathways, and relationships defining interactions among these entities. This helps researchers better understand the functional significance of G4 structures in biological contexts.
          </p>
        </div>
        <div class="image-block">
          <div class="image-content">
            <img
                src="@/assets/photo/details.png"
                alt="Node Details"
                class="help-image"
            />
            <p class="image-caption">Fig 1. Node Properties Details</p> <!-- 添加说明文字 -->
          </div>
        </div>

        <!-- 第二张图片和文字说明 -->
        <div class="text-content">
          <p>
            The following diagram illustrates the relationships between entities in the G4KG, showing how different nodes connect to form a comprehensive knowledge graph.
          </p>
        </div>
        <div class="image-block">
          <div class="image-content">
            <img
                src="@/assets/photo/G4&GO&Pathway.png"
                alt="Relationship Details"
                class="help-image"
            />
            <p class="image-caption">Fig 2. G4&GO&Pathway Relationships</p>
          </div>
        </div>

        <!-- 第三张图片和文字说明 -->
        <div class="image-block">
          <div class="image-content">
            <img
                src="@/assets/photo/Protein.png"
                alt="Relationship Details"
                class="help-image"
            />
            <p class="image-caption">Fig 3. Protein Relationships</p>
          </div>
        </div>

        <!-- 第四张图片和文字说明 -->
        <div class="image-block">
          <div class="image-content">
            <img
                src="@/assets/photo/Gene.png"
                alt="Relationship Details"
                class="help-image"
            />
            <p class="image-caption">Fig 4. Gene Relationships</p>
          </div>
        </div>

        <!-- 第五张图片和文字说明 -->
        <div class="image-block">
          <div class="image-content">
            <img
                src="@/assets/photo/Disease&Drug.png"
                alt="Relationship Details"
                class="help-image"
            />
            <p class="image-caption">Fig 5. Disease&Drug Relationships</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 第二个子部分: 功能介绍 -->
    <div class="help-section">
      <h2>Function Introduction</h2>
      <div class="help-content">

        <!-- 第六张图片和文字说明 -->
        <div class="text-content">
          <p>
            The G4KG platform provides multiple functionalities, including data visualization, advanced querying, and pathway enrichment analysis. Each tool is designed to support research in genomics, epigenomics, and transcriptomics.
          </p>
        </div>
        <div class="image-block">
          <div class="image-content">
            <img
                src="../assets/photo/function_search.png"
                alt="Function Introduction"
                class="help-image"
            />
            <p class="image-caption">Fig 6. Attribute&Relationship Search Guide</p>
          </div>
        </div>


        <!-- 第七张图片和文字说明 -->
        <div class="text-content">
          <p>
            The G4KG platform provides multiple functionalities, including data visualization, advanced querying, and pathway enrichment analysis. Each tool is designed to support research in genomics, epigenomics, and transcriptomics.
          </p>
        </div>
        <div class="image-block">
          <div class="image-content">
            <img
                src="../assets/photo/function_visualization.png"
                alt="Function Introduction"
                class="help-image"
            />
            <p class="image-caption">Fig 7. Visualization Guide</p>
          </div>
        </div>

        <!-- 第八张图片和文字说明 -->
        <div class="text-content">
          <p>
            The G4KG platform provides multiple functionalities, including data visualization, advanced querying, and pathway enrichment analysis. Each tool is designed to support research in genomics, epigenomics, and transcriptomics.
          </p>
        </div>
        <div class="image-block">
          <div class="image-content">
            <img
                src="../assets/photo/function_go&pathway_analyse.png"
                alt="Function Introduction"
                class="help-image"
            />
            <p class="image-caption">Fig 8. GO&Pathway Distribution Analyse Guide</p>
          </div>
        </div>

        <!-- 第九张图片和文字说明 -->
        <div class="text-content">
          <p>
            The G4KG platform provides multiple functionalities, including data visualization, advanced querying, and pathway enrichment analysis. Each tool is designed to support research in genomics, epigenomics, and transcriptomics.
          </p>
        </div>
        <div class="image-block">
          <div class="image-content">
            <img
                src="../assets/photo/function_enrichment_network.png"
                alt="Function Introduction"
                class="help-image"
            />
            <p class="image-caption">Fig 9. EnrichmentNetwork Analyse Guide</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 第三个子部分: 联系方式 -->
    <div class="help-section">
      <h2>Contact</h2>
      <div class="help-content">
        <div class="text-content">
          <p>
            For questions, support, or feedback, please contact us at <strong>support@g4kg.com</strong>. We value your input to improve the platform and make it more useful for your research.
          </p>
        </div>
        <div class="image-content">
          <img
              src="@/assets/photo/contact.png"
              alt="Contact Us"
              class="help-image"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "HelpPage",
};
</script>

<style scoped>
/* 主页面容器 */
.help-page {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
}

/* 页面标题样式 */
.help-title h1 {
  font-size: 2rem;
  font-weight: bold;
  color: #222;
  margin-bottom: 10px;
}

.help-title p {
  font-size: 1rem;
  color: #555;
  line-height: 1.5;
  margin-bottom: 30px;
}

/* 子部分样式 */
.help-section {
  margin-bottom: 30px;
}

.help-section h2 {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
  border-left: 4px solid #0073e6; /* 左侧增加装饰条 */
  padding-left: 10px;
}

/* 子部分内容布局 */
.help-content {
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
.image-block {
  text-align: center; /* 确保图片及说明文字居中 */
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px; /* 图片与说明文字之间的间距 */
  margin-bottom: 30px; /* 每个图片块之间的间距 */
}

.image-content {
  text-align: center; /* 确保图片在容器内居中显示 */
  display: flex;
  flex-direction: column; /* 垂直排列图片和说明文字 */
  align-items: center; /* 垂直居中 */
}

.image-content img {
  max-width: 80%;
  height: auto;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  margin: auto;
}

/* 图片说明文字样式 */
.image-caption {
  margin-top: 10px; /* 图片与说明文字之间的间距 */
  font-size: 1rem; /* 说明文字字体大小 */
  color: #666; /* 说明文字颜色 */
  text-align: center; /* 居中对齐说明文字 */
}
</style>
