<template>
  <div class="main-container">
    <!-- 标题 -->
    <h2 class="search-title">Search G4 By Attribute</h2>

    <!-- 搜索表单 -->
    <div class="search-container">
      <el-form :model="filters" label-width="120px" class="search-form">
        <div class="form-row">
          <el-form-item label="G4ID">
            <el-input v-model="filters.g4Id" placeholder="Enter G4 ID, Example: HG4_17" />
          </el-form-item>
          <el-form-item label="Chromosome">
            <el-input v-model="filters.chr" placeholder="Enter Chromosome, Example: chr1" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="Location">
            <el-input v-model="filters.location" placeholder="Enter Location, Example: 2001714-2001746" />
          </el-form-item>
          <el-form-item label="Score">
            <el-input v-model="filters.score" placeholder="Enter Score, Example: 60" />
          </el-form-item>
        </div>
        <div class="form-row">
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
        <el-table-column prop="g4Id" label="G4 ID">
          <template #header>
            G4 ID
            <el-tooltip content="Unique identifier for G4 from EndoQuad" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column prop="chr" label="Chromosome">
          <template #header>
            Chromosome
            <el-tooltip content="Chromosomal location of the G4" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column prop="location" label="Location">
          <template #header>
            Location
            <el-tooltip content="Location range of the G4(version hg19)" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column prop="sampleName" label="Sample Name">
          <template #header>
            Sample Name
            <el-tooltip content="Name of the sample used in the study" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column prop="confidenceLevel" label="Confidence Level">
          <template #header>
            Confidence Level
            <el-tooltip content="Confidence level of the G4 detection" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column prop="score" label="Score">
          <template #header>
            Score
            <el-tooltip content="Score of the G4 detection" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column prop="strand" label="Strand">
          <template #header>
            Strand
            <el-tooltip content="Strand information for the G4" placement="top">
              <InfoFilled class="info-icon" />
            </el-tooltip>
          </template>
        </el-table-column>

      <el-table-column prop="sequence" label="Sequence">
        <template #header>
          Sequence
          <el-tooltip content="Sequence details of the G4" placement="top">
            <InfoFilled class="info-icon" />
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column prop="cellLine" label="Cell Line">
        <template #header>
          Cell Line
          <el-tooltip content="Cell line used in the study" placement="top">
            <InfoFilled class="info-icon" />
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column prop="source" label="Source">
        <template #header>
          Source
          <el-tooltip content="Issue or cell source of the G4 data" placement="top">
            <InfoFilled class="info-icon" />
          </el-tooltip>
        </template>
      </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          background
          layout="prev, pager, next, jumper"
          :page-size="10"
          :total="totalRecords"
          v-model="currentPage"
          @current-change="handlePageChange"
          class="pagination-container"
      />
    </div>
  </div>
</template>

<script>
import { InfoFilled } from "@element-plus/icons-vue";
import { searchG4Records } from "@/api/services";

export default {
  components: {
    InfoFilled,
  },
  data() {
    return {
      filters: {
        g4Id: "",
        chr: "",
        location: "",
        sampleName: "", // 单值支持清空
        confidenceLevel: "", // 单值支持清空
        score: "",
      },
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
      results: [], // 查询结果
      totalRecords: 0, // 总记录数
      currentPage: 1, // 当前页
      loading: false, // 加载状态
    };
  },
  methods: {
    async fetchData() {
      this.loading = true;
      try {
        const response = await searchG4Records(this.filters, this.currentPage);
        this.results = response.content;
        this.totalRecords = response.totalElements;
      } catch (error) {
        console.error("Error fetching data:", error);
      } finally {
        this.loading = false;
      }
    },
    handleSearch() {
      this.currentPage = 1;
      this.fetchData();
    },
    resetFilters() {
      this.filters = {
        g4Id: "",
        chr: "",
        location: "",
        sampleName: "", // 重置为空值
        confidenceLevel: "", // 重置为空值
        score: "",
      };
      this.currentPage = 1;
      this.fetchData();
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.fetchData();
    },
  },
  created() {
    this.fetchData(); // 初始化查询数据
  },
};
</script>

<style src="@/assets/css/searchLayout.css"></style>
