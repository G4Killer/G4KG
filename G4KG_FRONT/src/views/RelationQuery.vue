<template>
  <div class="main-container">
    <!-- 标题 -->
    <h2 class="search-title">Search G4 By Relationship</h2>

    <!-- 搜索表单 -->
    <div class="search-container">
      <el-form :model="filters" label-width="120px" class="search-form">
        <!-- 第一行：Node Type, Node ID -->
        <div class="form-row">
          <el-form-item label="Node Type">
            <el-select v-model="filters.nodeType" placeholder="Example:GO" @change="handleNodeTypeChange">
              <el-option v-for="type in nodeTypes" :key="type" :label="type" :value="type" />
            </el-select>
          </el-form-item>
          <el-form-item label="Node ID">
            <el-input v-model="filters.nodeId" placeholder="Example:12" />
          </el-form-item>
        </div>

        <!-- 第二行：Node Name, Threshold（仅当 nodeType 为 Disease 时显示） -->
        <div class="form-row">
          <el-form-item label="Node Name">
            <el-input v-model="filters.nodeName" placeholder="Example:Single strand break repair" />
          </el-form-item>
          <el-form-item v-if="filters.nodeType === 'Disease'" label="Threshold">
            <template #label>
              Threshold
              <el-tooltip content="Minimum threshold value for filtering results" placement="top">
                <InfoFilled class="info-icon" />
              </el-tooltip>
            </template>
            <el-input-number v-model="filters.threshold" :min="0" placeholder="Enter Threshold" />
          </el-form-item>
        </div>

        <!-- 第三行：Hops -->
        <div class="form-row">
          <el-form-item label="Hops">
            <template #label>
              Hops
              <el-tooltip content="Number of hops to traverse in the graph" placement="top">
                <InfoFilled class="info-icon" />
              </el-tooltip>
            </template>
            <el-select v-model="filters.hops" placeholder="Default Hops is 1">
              <el-option v-for="hop in hopsOptions" :key="hop" :label="hop" :value="hop" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>

      <!-- 按钮 -->
      <div class="button-container">
        <el-button type="primary" @click="handleSearch" class="button button-primary">Search</el-button>
        <el-button @click="resetFilters" class="button button-secondary">Reset</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="results" border style="width: 100%" v-loading="loading">
        <!-- G4 Details -->
        <el-table-column prop="g4Details.G4Id" label="G4 ID" />
        <el-table-column prop="g4Details.Chr" label="Chromosome" />
        <el-table-column prop="g4Details.Location" label="Location" />
        <el-table-column prop="g4Details.SampleName" label="Sample Name" />
        <el-table-column prop="g4Details.ConfidenceLevel" label="Confidence Level" />
        <el-table-column prop="g4Details.Score" label="Score" />
        <el-table-column prop="g4Details.Strand" label="Strand" />
        <el-table-column prop="g4Details.Sequence" label="Sequence" />
        <el-table-column prop="g4Details.CellLine" label="Cell Line" />
        <el-table-column prop="g4Details.Source" label="Source" />

        <!-- Path Details -->
        <el-table-column label="Path Details" prop="formattedPathDetails">
          <template #header>
            Path Details
            <el-tooltip content="Details of the path traversed in the graph" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
          <template #default="scope">
            <!-- 遍历路径节点 -->
            <div>
              <template v-for="(node, index) in scope.row.formattedPathDetails" :key="index">
                <a href="#" @click.prevent="viewNodeDetails(node)">
                  {{ node }}
                </a>
                <span v-if="index < scope.row.formattedPathDetails.length - 1">, </span>
              </template>
            </div>
          </template>
        </el-table-column>

        <!-- Relation Names -->
        <el-table-column prop="relationNames" label="Relation Names">
          <template #header>
            Relation Names
            <el-tooltip content="Names of the relationships between nodes in the path" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
        </el-table-column>

      </el-table>

      <!-- 分页 -->
      <el-pagination
          layout="prev, next, jumper"
          :page-size="pageSize"
          :current-page="currentPage"
          :total="fakeTotal"
          @current-change="handlePageChange"
          :hide-on-single-page="fakeTotal <= pageSize"
          class="pagination-container"
      />
    </div>
  </div>
</template>

<script>
import { searchG4Relationships } from "@/api/services";
import { getNodeProperties } from "@/api/services";
import {InfoFilled} from "@element-plus/icons-vue";

export default {
  components: {InfoFilled},
  data() {
    return {
      filters: {
        nodeType: "",
        nodeIdProperty: "",
        nodeNameProperty: "",
        nodeId: "",
        nodeName: "",
        threshold: null,
        hops: null,
      },
      nodeTypes: ["Gene", "Disease", "Protein", "Drug", "GO", "Pathway"],
      hopsOptions: [1, 2, 3],
      results: [], // 当前页的数据
      currentPage: 1, // 当前页码
      pageSize: 10, // 每页大小
      loading: false, // 加载状态
      fakeTotal: 10000000, // 设置为一个较大的伪总数
    };
  },

  methods: {
    async handleSearch() {
      this.loading = true;
      try {
        const response = await searchG4Relationships({
          ...this.filters,
          page: this.currentPage, // 当前页码
        });

        console.log("Response from API:", response);
        this.results = response.content.map(item => {
          const g4Details = this.parseFormattedG4Details(item.formattedG4Details);
          return { ...item, g4Details: g4Details || {} };
        });

        // 如果后端返回的数据少于每页大小，可以推断这是最后一页
        if (response.content.length < this.pageSize) {
          this.fakeTotal = (this.currentPage - 1) * this.pageSize + response.content.length;
          console.log("Updated fakeTotal:", this.fakeTotal);
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      } finally {
        this.loading = false;
      }
    },
    /**
     * 点击路径节点时触发，解析节点信息并跳转到详情页面
     * @param {string} node - 路径节点信息字符串 (e.g., "GO{GoId='12'}")
     */
    async viewNodeDetails(node) {
      try {
        console.log("Clicked node:", node);

        // 正则提取节点类型 (label)、ID 属性 (idProperty) 和 ID 值 (idValue)
        const match = node.match(/^(\w+)\{(\w+)='(.+)'\}$/);
        if (!match) {
          console.error("Invalid node format:", node);
          return;
        }

        const label = match[1]; // 节点类型
        const idProperty = match[2]; // 唯一标识属性
        const idValue = match[3]; // 唯一标识值

        // 跳转到详情页面，传递参数
        this.$router.push({
          name: "NodeDetails", // 确保这个路由存在
          query: { label, idProperty, idValue },
        });
      } catch (error) {
        console.error("Error handling node details view:", error);
      }
    },

    parseFormattedG4Details(detailsString) {
      try {
        if (!detailsString || typeof detailsString !== "string") {
          return null; // 如果字符串为空或者不是字符串，直接返回 null
        }

        // 去掉字符串中的 "G4{" 和 "}"，然后按逗号分割
        const content = detailsString.replace(/^G4\{|}$/g, "");
        const keyValuePairs = content.split(/,\s*/); // 按逗号和可选的空格分割

        // 将键值对转换为 JSON 对象
        const result = {};
        keyValuePairs.forEach(pair => {
          const [key, value] = pair.split("="); // 按等号分割键值对

          // 去掉字段值两边的单引号，并处理 "null" 值
          const cleanedValue =
              value === undefined
                  ? null
                  : value.trim().replace(/^'(.*)'$/, "$1").trim(); // 去掉单引号并清理空格

          result[key.trim()] = cleanedValue === "null" ? null : cleanedValue; // 将键和值加入结果对象
        });

        return result;
      } catch (error) {
        console.error("Error parsing formattedG4Details:", error, detailsString); // 打印错误和原始字符串
        return null; // 如果解析失败，返回 null
      }
    },

    resetFilters() {
      this.filters = {
        nodeType: "",
        nodeIdProperty: "",
        nodeNameProperty: "",
        nodeId: "",
        nodeName: "",
        threshold: null,
        hops: null,
      };
      this.currentPage = 1;
      this.results = [];
      this.loading = false;
    },
    handlePageChange(page) {
      console.log("Page change triggered. New Page:", page);
      this.currentPage = page;
      this.handleSearch(); // 每次翻页时重新调用搜索接口
    },
    async handleNodeTypeChange() {
      if (!this.filters.nodeType) {
        this.filters.nodeIdProperty = "";
        this.filters.nodeNameProperty = "";
        return;
      }

      try {
        console.log("Fetching node properties for:", this.filters.nodeType);
        const response = await getNodeProperties(this.filters.nodeType);
        this.filters.nodeIdProperty = response.nodeIdProperty || "";
        this.filters.nodeNameProperty = response.nodeNameProperty || "";
        console.log("Node properties fetched:", response);
      } catch (error) {
        console.error("Error fetching node properties:", error);
        this.filters.nodeIdProperty = "";
        this.filters.nodeNameProperty = "";
      }
    },
  },
};
</script>


<style src="@/assets/css/searchLayout.css"></style>
