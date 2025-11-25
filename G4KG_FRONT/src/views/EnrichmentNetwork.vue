<template>
  <div class="visualization-page">
    <!-- 左侧侧边栏 -->
    <div class="sidebar">
      <h3 class="sidebar-title">Search Conditions</h3>
      <el-form :model="filters" label-width="120px" class="form-container">
        <!-- useRelationship 开关按钮 -->
        <el-form-item label="Relationship">
          <template #label>
            Relationship
            <el-tooltip content="Decide whether to UseRelationship" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
          <el-switch v-model="useRelationship" />
        </el-form-item>

        <!-- 根据 useRelationship 切换表单内容 -->
        <template v-if="!useRelationship">
          <!-- AttributeQuery 表单 -->
          <el-form-item label="G4 ID">
            <el-input v-model="filters.g4Id" placeholder="Enter G4 ID, Example: HG4_17" />
          </el-form-item>
          <el-form-item label="Chromosome">
            <el-input v-model="filters.chr" placeholder="Enter Chromosome, Example: chr1" />
          </el-form-item>
          <el-form-item label="Location">
            <el-input v-model="filters.location" placeholder="Enter Location, Example: 2001714-2001746" />
          </el-form-item>
          <el-form-item label="SampleName">
            <el-select v-model="filters.sampleName" placeholder="Select Sample Name">
              <el-option :value="''" label="No Filter" />
              <el-option v-for="name in sampleNames" :key="name" :label="name" :value="name" />
            </el-select>
          </el-form-item>
          <el-form-item label="ConfidenceLevel">
            <el-select v-model="filters.confidenceLevel" placeholder="Select Confidence Level">
              <el-option :value="''" label="No Filter" />
              <el-option v-for="level in confidenceLevels" :key="level" :label="level" :value="level" />
            </el-select>
          </el-form-item>
        </template>

        <template v-else>
          <!-- RelationQuery 表单 -->
          <el-form-item label="Node Type">
            <el-select v-model="filters.nodeType" placeholder="Example: GO" @change="handleNodeTypeChange">
              <el-option v-for="type in nodeTypes" :key="type" :label="type" :value="type" />
            </el-select>
          </el-form-item>
          <el-form-item label="Node ID">
            <el-input v-model="filters.nodeId" placeholder="Example: HG4_4426" />
          </el-form-item>
          <el-form-item label="Node Name">
            <el-input v-model="filters.nodeName" placeholder="Example: Single Strand Repair" />
          </el-form-item>
          <el-form-item v-if="filters.nodeType === 'Disease'" label="Threshold">
            <el-input-number v-model="filters.threshold" :min="0" placeholder="Enter Threshold" />
          </el-form-item>
          <el-form-item label="Hops">
            <el-select v-model="filters.hops" placeholder="Select Number of Hops">
              <el-option v-for="hop in hopsOptions" :key="hop" :label="hop" :value="hop" />
            </el-select>
          </el-form-item>
        </template>

        <!-- G4 Limit -->
        <el-form-item label="G4 Limit">
          <template #label>
            G4 Limit
            <el-tooltip content="Decide the maximum number of G4s" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
          <el-input-number v-model="filters.g4Limit" :min="1" :step="10" placeholder="Enter G4 Limit" />
        </el-form-item>

        <div class="button-container">
          <el-button @click="handleNetworkAnalysis" class="button button-primary">Analyse</el-button>
          <el-button @click="resetFilters" class="button button-secondary">Reset</el-button>
        </div>
      </el-form>
    </div>

    <div id="echarts-graph" class="graph-container" v-loading="loading"></div>

  </div>
</template>

<script>
import * as echarts from "echarts";
import { InfoFilled } from "@element-plus/icons-vue";
import { buildG4FunctionEnrichmentNetwork } from "@/api/services";

export default {
  components: { InfoFilled },
  data() {
    return {
      useRelationship: false,
      loading: false,
      filters: {
        g4Id: "",
        chr: "",
        location: "",
        sampleName: "",
        confidenceLevel: "",
        nodeType: "",
        nodeId: "",
        nodeName: "",
        threshold: null,
        hops: 1,
        g4Limit: 50,
      },
      nodeTypes: ["GO", "Pathway", "Gene", "Disease", "Protein"],
      hopsOptions: [1, 2, 3],
      sampleNames: [
        "HEK293T_Flavopiridol.CUT.Tag_DMSO",
        "STG201_181",
        "AB521M",
        "A549_G4chip",
        "HEK293T_PDS.CUT.Tag_PDS",
        "K562_NA",
        "ESC",
        "HEK293T_PDS.CUT.Tag_DMSO",
        "H1975_G4chip",
        "K562_SMARCA4",
        "AB551",
        "CNCC",
        "HEK293T_Flavopiridol.CUT.Tag_FP",
        "VHIO179_181",
        "AB580",
        "STG331",
        "AB577M",
        "HACAT_Entinostat",
        "HCI005",
        "HEK293T_CUT.Tag",
        "VHIO098_181",
        "K562_normoxia",
        "STG139M_284",
        "HepG2_NA",
        "AB636M",
        "K562_hypoxia",
        "VHIO098_284",
        "HACAT_NA",
        "K562_DRB",
        "NSC",
        "HEK293T",
        "STG139M_181",
        "iPSC.derived_neurons",
        "K562_noDRB",
        "STG201_284",
        "HeLa.S3_G4chip",
        "VHIO179_284",
        "K562_TPL",
        "HEK293T_TMPyP4.CUT.Tag_TMPYP4",
        "AB863M",
        "K562_noTPL",
        "AB555M",
        "STG316",
        "STG195M",
        "PAR1006",
        "HCI009",
        "PAR1022",
        "U2OS_normoxia",
        "HEK293T_TMPyP4.CUT.Tag_DMSO",
        "STG143_317",
        "U2OS_hypoxia",
        "AB790",
        "93T449.BG4",
        "STG143_284",
        "STG139",
      ],
      confidenceLevels: ["Level1", "Level2", "Level3", "Level4", "Level5", "Level6"],
    };
  },
  methods: {
    async handleNetworkAnalysis() {
      this.loading = true; // 开启 loading 状态
      try {
        const requestData = {
          ...this.filters,
          useRelationship: this.useRelationship,
        };
        const response = await buildG4FunctionEnrichmentNetwork(requestData);

        console.log("Response Data:", response);

        if (response && response.nodes && response.relationships) {
          const graphData = this.processGraphData(response);

          this.renderNetworkChart(graphData.nodes, graphData.links);
        } else {
          this.$message.info("No data available for visualization.");
        }
      } catch (error) {
        console.error("Error fetching network data:", error);
        this.$message.error("Failed to fetch network data. Please try again.");
      } finally {
        this.loading = false; // 关闭 loading 状态
      }
    },

    processGraphData(data) {
      const NODE_NAME_MAPPING = {
        G4: "G4Id",
        Gene: "GeneSymbol",
        Pathway: "PathwayId",
        GO: "GoId",
      };

      const uniqueNodes = new Map();
      const uniqueLinks = new Set();

      data.nodes.forEach((node) => {
        const labels = Array.isArray(node.labels) ? node.labels : [node.labels];
        const primaryLabel = labels.find((label) => NODE_NAME_MAPPING[label]) || "Unknown";
        const propertyKey = NODE_NAME_MAPPING[primaryLabel];
        const name =
            (node.properties && propertyKey && node.properties[propertyKey]) || `${node.id} (${primaryLabel})`;

        if (!uniqueNodes.has(node.id)) {
          uniqueNodes.set(node.id, {
            id: node.id.toString(),
            name,
            category: primaryLabel,
            value: node.properties || {},
            symbolSize: node.size || 20,
          });
        }
      });

      data.relationships.forEach((relationship) => {
        const linkKey = `${relationship.startNodeId}-${relationship.endNodeId}-${relationship.type}`;
        if (!uniqueLinks.has(linkKey)) {
          uniqueLinks.add({
            source: relationship.startNodeId.toString(),
            target: relationship.endNodeId.toString(),
            value: relationship.properties.RelationName || relationship.type, // 使用 RelationName 或 type
          });
        }
      });

      const nodes = Array.from(uniqueNodes.values());
      const links = Array.from(uniqueLinks); // 直接将链接对象数组返回


      return {nodes, links};
    },

    renderNetworkChart(nodes, links) {
      const chartDom = document.getElementById("echarts-graph");
      const myChart = echarts.init(chartDom);

      const option = {
        title: {
          text: "G4 Function Enrichment Network",
          left: "center",
          textStyle: {
            fontSize: 20,
            fontWeight: "bold",
            color: "#333",
          },
        },
        tooltip: {
          formatter: (params) => {
            if (params.dataType === "node") {
              return Object.entries(params.data.value || {})
                  .map(([key, value]) => `<b>${key}:</b> ${value}`)
                  .join("<br>");
            } else if (params.dataType === "edge") {
              return `<b>Relation:</b> ${params.data.value}`;
            }
          },
        },
        legend: {
          data: [...new Set(nodes.map((node) => node.category))],
          orient: "vertical",
          right: 10,
          top: "middle",
          textStyle: {
            fontSize: 14,
          },
        },
        toolbox: {
          show: true,
          feature: {
            saveAsImage: { show: true }, // 保存图片功能
            restore: { show: true }, // 重置布局功能
          },
        },
        series: [
          {
            name: "G4 Network",
            type: "graph",
            layout: "force",
            data: nodes,
            links: links,
            categories: [...new Set(nodes.map((node) => ({name: node.category})))],
            roam: true,
            label: {
              show: true,
              position: "right",
              formatter: "{b}",
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
              show: false,
              formatter: "{c}",
            },
            edgeSymbol: ["none", "arrow"],
            edgeSymbolSize: [2, 8], // 优化箭头大小
          },
        ],
      };

      myChart.setOption(option);
    },

    handleNodeTypeChange() {
      if (this.filters.nodeType !== "Disease") {
        this.filters.threshold = null;
      }
    },

    resetFilters() {
      this.filters = {
        g4Id: "",
        chr: "",
        location: "",
        sampleName: "",
        confidenceLevel: "",
        nodeType: "",
        nodeId: "",
        nodeName: "",
        threshold: null,
        hops: null,
        g4Limit: 50,
      };
    },
  },
};
</script>

<style src="@/assets/css/visualizationLayout.css"></style>
<style src="@/assets/css/sidebar.css"></style>
