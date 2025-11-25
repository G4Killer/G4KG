<template>
  <div class="analysis-page">
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
              <el-option
                  v-for="type in nodeTypes"
                  :key="type"
                  :label="type"
                  :value="type"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="Node ID">
            <el-input v-model="filters.nodeId" placeholder="Example: HG4_4426" />
          </el-form-item>
          <el-form-item label="Node Name">
            <el-input v-model="filters.nodeName" placeholder="Example: Single Strand Repair" />
          </el-form-item>
          <el-form-item v-if="filters.nodeType === 'Disease'" label="Threshold">
            <el-input-number
                v-model="filters.threshold"
                :min="0"
                placeholder="Enter Threshold"
            />
          </el-form-item>
          <el-form-item label="Hops">
            <el-select v-model="filters.hops" placeholder="Select Number of Hops">
              <el-option
                  v-for="hop in hopsOptions"
                  :key="hop"
                  :label="hop"
                  :value="hop"
              />
            </el-select>
          </el-form-item>
        </template>

        <!-- G4 Limit -->
        <el-form-item label="G4 Limit">
          <template #label>
            G4 Limit
            <el-tooltip content="Decide whether to UseRelationship" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
          <el-input-number
              v-model="filters.g4Limit"
              :min="1"
              :step="10"
              placeholder="Enter G4 Limit"
          />
        </el-form-item>

        <!-- Reset 按钮 -->
        <div class="button-container">
          <el-button @click="resetFilters" class="button button-secondary">Reset</el-button>
        </div>
      </el-form>
    </div>

    <!-- 右侧主内容框 -->
    <div class="analysis-container">
      <!-- Pathway 分布分析 -->
      <div class="analysis-section">
        <h3>Pathway Function Distribution</h3>
        <el-form inline class="analysis-controls">
          <el-form-item label="Result Limit">
            <template #label>
              Result Limit
              <el-tooltip content="Decide whether to UseRelationship" placement="top">
                <InfoFilled class="info-icon" />
              </el-tooltip>
            </template>
            <el-input-number v-model="pathwayResultLimit" :min="1" placeholder="Enter Result Limit" />
          </el-form-item>
          <div class="button-container">
            <el-button @click="handlePathwayAnalysis" class="button button-primary">Analyse</el-button>
            <el-button @click="resetPathwayAnalysis" class="button button-secondary">Reset</el-button>
          </div>
        </el-form>
        <div class="analysis-result" v-if="paginatedResults.length">
          <table class="table">
            <thead>
            <tr>
              <th>Pathway ID</th>
              <th>Pathway Name</th>
              <th>G4 Count</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="result in paginatedResults" :key="result.PathwayId">
              <td>{{ result.pathwayId }}</td>
              <td>{{ result.pathwayName }}</td>
              <td>{{ result.g4Count }}</td>
            </tr>
            <tr v-for="i in (5 - paginatedResults.length)" :key="'empty' + i">
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            </tbody>
          </table>
          <el-pagination
              v-if="totalPages > 1"
              v-model="currentPage"
              :page-size="5"
              :total="pathwayResults.length"
              layout="prev, pager, next"
              @current-change="updatePagination"
          />
        </div>
        <div class="no-result" v-else>
          No results found.
        </div>
      </div>
      <!-- 组合图表容器 -->
      <div id="combined-chart" class="chart-container"></div>
    </div>
  </div>
</template>


<script>
import * as echarts from "echarts";
import {analyzePathwayDistribution} from "@/api/services";
import {InfoFilled} from "@element-plus/icons-vue";


export default {
  components: {InfoFilled},
  data() {
    return {
      // 全局表单状态
      useRelationship: false,
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
        g4Limit: 100,
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
      // GO 分析
      pathwayResultLimit: 5,
      pathwayResults: [],
      currentPage: 1,
      paginatedResults: [],
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.pathwayResults.length / 5);
    },
  },
  methods: {
    updatePagination() {
      const startIndex = (this.currentPage - 1) * 5;
      this.paginatedResults = this.pathwayResults.slice(startIndex, startIndex + 5);
    },

    updateCharts() {
      // 获取容器 DOM
      const chartDom = document.getElementById('combined-chart');
      const chart = echarts.init(chartDom);

      // 准备数据
      const categories = this.pathwayResults.map(item => item.pathwayName);
      const values = this.pathwayResults.map(item => item.g4Count);

      // 配置图表
      const option = {
        title: [
          { text: 'Pathway Distribution - Combined Chart', left: 'left' },
        ],
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { top: '10%', bottom: '10%', left: '25%', right: '50%' },
        xAxis: { type: 'value', name: 'G4 Count' },
        yAxis: {
          type: 'category',
          data: categories,
          axisLabel: {
            interval: 0, // 显示所有标签
            formatter: value => value.length > 15 ? `${value.slice(0, 15)}...` : value, // 超长截断
          },
        },
        series: [
          {
            name: 'G4 Count (Bar)',
            type: 'bar',
            data: values,
            itemStyle: { color: '#409eff' },
            barWidth: '60%', // 调整柱状图宽度
          },
          {
            name: 'G4 Count (Pie)',
            type: 'pie',
            radius: ['30%', '45%'], // 设置内外环的半径
            center: ['80%', '50%'], // 饼图位置
            data: categories.map((name, index) => ({
              name,
              value: values[index],
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
            },
          },
        ],
      };
      // 渲染图表
      chart.setOption(option);
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
        g4Limit: 100,
      };
    },
    async handlePathwayAnalysis() {
      try {
        // 构造请求数据
        const requestData = {
          ...this.filters,
          useRelationship: this.useRelationship, // 显式传递 useRelationship
          resultLimit: this.pathwayResultLimit,
        };

        console.log("Request Data:", requestData); // 调试输出

        // 调用 API
        const response = await analyzePathwayDistribution(requestData);

        // 更新 goResults
        if (response && response.length > 0) {
          this.pathwayResults = response.map((item) => ({
            pathwayId: item.pathwayId,
            pathwayName: item.pathwayName,
            g4Count: item.g4Count,
          }));
          this.currentPage = 1;
          this.updatePagination();
          this.updateCharts(); // 更新图表
          this.$message.success("Pathway Distribution Analysis Complete!");
        } else {
          this.pathwayResults = [];
          this.updateCharts(); // 清空图表
          this.$message.info("No results found.");
        }
      } catch (error) {
        console.error("Error during Pathway Distribution Analysis:", error);
        this.$message.error("Failed to analyze Pathway distribution. Please try again.");
      }
    },

    resetPathwayAnalysis() {
      this.pathwayResultLimit = 5;
      this.pathwayResults = [];
      this.currentPage = 1;
      this.updatePagination();
    },
  },
};
</script>

<style src="@/assets/css/analyseLayout.css"></style>
<style src="@/assets/css/sidebar.css"></style>
