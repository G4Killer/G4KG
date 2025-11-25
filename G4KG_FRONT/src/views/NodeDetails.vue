<template>
  <div class="node-details-container">
    <h2 class="title">Node Details</h2>

    <!-- 返回按钮 -->
    <el-button type="primary" icon="el-icon-arrow-left" @click="$router.go(-1)" class="back-button">
      Back
    </el-button>

    <!-- 加载中 -->
    <el-empty v-if="loading" description="Loading..." />

    <!-- 节点详情展示 -->
    <el-card v-else-if="nodeDetails && Object.keys(nodeDetails).length > 0" class="attribute-container">
      <div class="attribute-item" v-for="(value, key) in nodeDetails" :key="key">
        <span class="attribute-key">{{ key }}:</span>
        <span class="attribute-value">{{ value }}</span>
      </div>
    </el-card>

    <!-- 空状态 -->
    <el-empty v-else description="No Data Available" />
  </div>
</template>

<script>
import { getNodeDetails } from "@/api/services";

export default {
  data() {
    return {
      nodeDetails: null, // 存储节点详情信息
      loading: false, // 加载状态
    };
  },
  async created() {
    try {
      const { label, idProperty, idValue } = this.$route.query;

      console.log("Route Parameters:", { label, idProperty, idValue });

      if (!label || !idProperty || !idValue) {
        this.$message.error("Invalid node details parameters.");
        this.$router.back();
        return;
      }

      this.loading = true;

      // 调试 API 请求及其结果
      console.log("Fetching details for:", { label, idProperty, idValue });
      const details = await getNodeDetails(label, idProperty, idValue);

      console.log("Fetched Node Details:", details);

      if (!details || Object.keys(details).length === 0) {
        this.$message.warning("No details available for the selected node.");
        return;
      }

      this.nodeDetails = details;
    } catch (error) {
      console.error("Error fetching node details:", error);
      this.$message.error(
          error.response?.data?.message || "Failed to fetch node details."
      );
    } finally {
      this.loading = false;
    }
  },
};
</script>

<style src="@/assets/css/nodeDetails.css"></style>