<template>
  <div class="visualization-page">
    <!-- 左侧搜索条件 -->
    <div class="sidebar">
      <h3 class="sidebar-title">Search Conditions</h3>
      <el-form :model="filters" label-width="120px" class="form-container">
        <el-form-item label="Node Type">
          <el-select v-model="filters.nodeType" placeholder="Example:G4" @change="fetchNodeProperties">
            <el-option v-for="type in nodeTypes" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>

        <!-- 隐藏 Node ID Property -->
        <el-form-item label="Node ID Property" v-show="false">
          <el-input v-model="filters.nodeIdProperty" placeholder="Auto-filled based on Node Type" disabled />
        </el-form-item>

        <el-form-item label="Node ID">
          <el-input v-model="filters.nodeId" placeholder="Example:HG4_4435" />
        </el-form-item>

        <el-form-item label="Depth">
          <template #label>
            Depth
            <el-tooltip content="The depth of the subgraph" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
          <el-input-number v-model="filters.depth" :min="1" placeholder="Set Depth" />
        </el-form-item>

        <el-form-item label="Node Limit">
          <template #label>
            Node Limit
            <el-tooltip content="The Node counts in your subgraph" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
          <el-input-number v-model="filters.limit" :min="1" placeholder="Set Node Limit" />
        </el-form-item>

        <el-form-item label="Isolated">
          <template #label>
            Isolated
            <el-tooltip content="Decide whether to include isolated nodes" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
          <el-switch v-model="filters.includeIsolated" />
        </el-form-item>

        <!-- 按钮部分 -->
        <div class="button-container">
          <el-button type="primary" @click="handleSearch" class="button button-primary">Search</el-button>
          <el-button @click="resetFilters" class="button button-secondary">Reset</el-button>
        </div>
      </el-form>
    </div>

    <div id="echarts-graph" class="graph-container" v-loading="loading"></div>

  </div>
</template>

<script>
import * as echarts from "echarts";
import { getSubGraph, getGraphNodeProperties } from "@/api/services";
import {InfoFilled} from "@element-plus/icons-vue";

export default {
  components: {InfoFilled},
  data() {
    return {
      filters: {
        nodeType: "",
        nodeIdProperty: "",
        nodeId: "",
        depth: 2,
        limit: 50,
        includeIsolated: false,
      },
      nodeTypes: ["G4", "Gene", "Disease", "Protein", "Pathway", "GO", "Drug"], // 节点类型选项
      loading: false,
      chart: null, // 保存 ECharts 实例
    };
  },
  methods: {
    async fetchNodeProperties() {
      if (!this.filters.nodeType) {
        this.filters.nodeIdProperty = "";
        return;
      }
      try {
        const response = await getGraphNodeProperties(this.filters.nodeType);
        this.filters.nodeIdProperty = response.nodeIdProperty || "";
      } catch (error) {
        console.error("Error fetching node properties:", error);
        this.filters.nodeIdProperty = "";
      }
    },

    async handleSearch() {
      if (!this.filters.nodeType || !this.filters.nodeId || !this.filters.nodeIdProperty) {
        this.$message.error("Please fill in all required fields.");
        return;
      }

      this.loading = true;

      try {
        const response = await getSubGraph(this.filters);
        const graphData = this.processGraphData(response);
        this.renderGraph(graphData);
      } catch (error) {
        console.error("Error fetching subgraph data:", error);
        this.$message.error("Failed to fetch subgraph data.");
      } finally {
        this.loading = false;
      }
    },

    processGraphData(data) {
      const NODE_NAME_MAPPING = {
        G4: "G4Id",
        Gene: "GeneSymbol",
        Disease: "DiseaseId",
        Protein: "ProteinId",
        Pathway: "PathwayId",
        GO: "GoId",
        Drug: "DrugId",
      };

      const nodes = data.nodes.map((node) => {
        const primaryLabel = node.labels[0];
        const propertyKey = NODE_NAME_MAPPING[primaryLabel];
        const name = (propertyKey && node.properties[propertyKey]) || node.id.toString();

        return {
          id: node.id.toString(),
          name,
          category: primaryLabel,
          value: node.properties,
          symbolSize: 30,
        };
      });

      const links = data.relationships.map((relationship) => ({
        source: relationship.startNodeId.toString(),
        target: relationship.endNodeId.toString(),
        value: relationship.properties.RelationName || relationship.type,
      }));

      return { nodes, links };
    },

    renderGraph({ nodes, links }) {
      if (!this.chart) {
        this.chart = echarts.init(document.getElementById("echarts-graph"));
      }

      const option = {
        title: {
          text: 'Graph Visualization', // 标题内容
          left: 'left', // 标题水平居中
          top: 'top', // 标题在图表顶部
          textStyle: {
            fontSize: 20, // 标题字体大小
            fontWeight: 'bold', // 标题加粗
            color: '#333' // 标题颜色
          }
        },
        tooltip: {
          formatter: (params) => {
            if (params.dataType === "node") {
              const properties = params.data.value;
              return Object.entries(properties)
                  .map(([key, value]) => `<b>${key}:</b> ${value}`)
                  .join("<br>");
            } else if (params.dataType === "edge") {
              return `<b>Relation:</b> ${params.data.value}`;
            }
          },
        },
        legend: [
          {
            data: [...new Set(nodes.map((node) => node.category))], // 图例数据
            orient: "vertical", // 设置图例为纵向排列
            right: 10,
            top: "middle",
            textStyle: {
              fontSize: 14,
            },
          },
        ],
        toolbox: {
          show: true,
          feature: {
            saveAsImage: { show: true }, // 保存图片功能
            restore: { show: true }, // 重置布局功能
          },
        },
        series: [
          {
            type: "graph",
            layout: "force",
            data: nodes,
            links: links,
            categories: [...new Set(nodes.map((node) => ({ name: node.category })))],
            roam: true,
            label: {
              show: true,
              position: "inside",
            },
            emphasis: {
              focus: "adjacency",
              lineStyle: {
                width: 3,
                color: "red",
              },
            },
            draggable: true, // 节点可拖拽
            force: {
              repulsion: 700,
              edgeLength: [50, 200],
            },
            edgeLabel: {
              show: true,
              formatter: "{c}", // 显示关系名称
            },
            edgeSymbol: ["none", "arrow"],
            edgeSymbolSize: [2, 8], // 优化箭头大小
          },
        ],
      };

      this.chart.setOption(option);
    },

    resetFilters() {
      this.filters = {
        nodeType: "",
        nodeIdProperty: "",
        nodeId: "",
        depth: 2,
        limit: 50,
        includeIsolated: false,
      };
      if (this.chart) {
        this.chart.clear();
      }
    },
  },
};
</script>

<style src="@/assets/css/visualizationLayout.css"></style>
<style src="@/assets/css/sidebar.css"></style>
