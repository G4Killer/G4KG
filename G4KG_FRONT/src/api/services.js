import apiClient from './axios-config';

//模块一、属性查询G4
/**
 * 1. 分页查询接口
 * @param {Object} filters - 查询条件
 * @param {string} [filters.g4Id] - G4 ID
 * @param {string} [filters.chr] - 染色体
 * @param {string} [filters.location] - 位置
 * @param {string} [filters.sampleName] - 样本名称
 * @param {string} [filters.confidenceLevel] - 置信度
 * @param {string} [filters.score] - 分数
 * @param {number} page - 当前页码，从 1 开始
 * @returns {Promise<Object>} - 返回分页结果
 */
export const searchG4Records = async (filters, page) => {
  const params = {
    ...filters,
    page,
  };
  return apiClient.get('/g4/search', { params });
};

/**
 * 2. 统计符合条件的记录数量
 * @param {Object} filters - 查询条件
 * @param {string} [filters.g4Id] - G4 ID
 * @param {string} [filters.chr] - 染色体
 * @param {string} [filters.location] - 位置
 * @param {string} [filters.sampleName] - 样本名称
 * @param {string} [filters.confidenceLevel] - 置信度
 * @param {string} [filters.score] - 分数
 * @returns {Promise<number>} - 返回符合条件的记录数量
 */
export const countG4Records = async (filters) => {
  return apiClient.get('/g4/count', { params: filters });
};

/**
 * 3. 获取所有唯一的 SampleName
 * @returns {Promise<string[]>} - 返回唯一 SampleName 列表
 */
export const getDistinctSampleNames = async () => {
  return apiClient.get('/g4/distinct/sample-names');
};

/**
 * 4. 获取所有唯一的 ConfidenceLevel
 * @returns {Promise<string[]>} - 返回唯一 ConfidenceLevel 列表
 */
export const getDistinctConfidenceLevels = async () => {
  return apiClient.get('/g4/distinct/confidence-levels');
};

//模块一、关系查询G4
/**
 * 分页查询 G4 关系
 * @param {Object} filters - 查询条件
 * @param {string} filters.nodeType - 节点类型（必填）
 * @param {string} [filters.nodeIdProperty] - 节点 ID 属性（可选）
 * @param {string} [filters.nodeNameProperty] - 节点名称属性（可选）
 * @param {string} [filters.nodeId] - 节点 ID（可选）
 * @param {string} [filters.nodeName] - 节点名称（可选）
 * @param {number} [filters.threshold] - 阈值（可选）
 * @param {number} [filters.hops] - 跳数（可选）
 * @param {number} page - 当前页码，从 1 开始
 * @returns {Promise<Object>} - 返回分页结果
 */
export const searchG4Relationships = async (filters, page) => {
    const params = {
      ...filters,
      page,
    };
    return apiClient.get('/g4-relationship/search', { params });
  };

/**
 * 获取节点的属性映射
 * @param {string} nodeType - 节点类型（必填），例如 Gene、Disease 等
 * @returns {Promise<Object>} - 返回属性映射 { nodeIdProperty, nodeNameProperty }
 */
export const getNodeProperties = async (nodeType) => {
  return apiClient.get(`/g4-relationship/properties`, { params: { nodeType } });
};

// 模块二、获取子图
/**
 * 1. 获取子图
 * @param {Object} params - 查询参数
 * @param {string} params.nodeType - 节点类型
 * @param {string} [params.nodeIdProperty] - 节点 ID 属性（可选）
 * @param {string} params.nodeId - 节点 ID
 * @param {number} params.depth - 搜索深度
 * @param {number} params.limit - 限制节点数量
 * @param {boolean} [params.includeIsolated=false] - 是否包含孤立节点
 * @returns {Promise<Object>} - 返回子图结果
 */
export const getSubGraph = async (params) => {
  return apiClient.get('/subgraph/get', { params });
};

/**
 * 2. 获取节点的属性映射
 * @param {string} nodeType - 节点类型（必填），例如 Gene、Disease 等
 * @returns {Promise<Object>} - 返回属性映射 { nodeIdProperty, nodeNameProperty }
 */
export const getGraphNodeProperties = async (nodeType) => {
  return apiClient.get('/subgraph/properties', { params: { nodeType } });
};

//模块三、功能分析
/**
 * 1.分析 GO 功能分布
 * @param {Object} request - 请求体
 * @returns {Promise<Object>} - 返回 GO 功能分布分析结果
 */
export const analyzeGOFunctionDistribution = async (request) => {
    return apiClient.post('/function-analyse/analyze-go', request);
  };
  
  /**
   * 2.分析 Pathway 功能分布
   * @param {Object} request - 请求体
   * @returns {Promise<Object>} - 返回 Pathway 功能分布分析结果
   */
  export const analyzePathwayDistribution = async (request) => {
    return apiClient.post('/function-analyse/analyze-pathway', request);
  };
  
  /**
   * 3.构建 G4 功能富集网络
   * @param {Object} request - 请求体
   * @returns {Promise<Object>} - 返回功能富集网络
   */
  export const buildG4FunctionEnrichmentNetwork = async (request) => {
    return apiClient.post('/function-analyse/build-enrichment-network', request);
  };
  
  /**
   * 4.获取 G4 ID 集合
   * @param {Object} params - 查询参数
   * @param {boolean} params.useRelationship - 是否使用关系查询模式
   * @param {string} [params.g4Id] - G4 ID
   * @param {string} [params.chr] - 染色体
   * @param {string} [params.location] - 位置
   * @param {string} [params.sampleName] - 样本名称
   * @param {string} [params.confidenceLevel] - 置信度
   * @param {string} [params.score] - 分数
   * @param {string} [params.nodeType] - 节点类型
   * @param {string} [params.nodeIdProperty] - 节点 ID 属性
   * @param {string} [params.nodeNameProperty] - 节点名称属性
   * @param {string} [params.nodeId] - 节点 ID
   * @param {string} [params.nodeName] - 节点名称
   * @param {number} [params.threshold] - 阈值
   * @param {number} [params.hops=1] - 跳数
   * @param {number} [params.limit=100] - 返回结果限制
   * @returns {Promise<string[]>} - 返回 G4 ID 集合
   */
  export const getG4Ids = async (params) => {
    return apiClient.get('/function-analyse/get-g4-ids', { params });
  };

//模块四、自定义查询
/**
 * 执行自定义查询（同步）
 * @param {Object} request - 请求体
 * @param {string} request.query - Cypher 查询语句
 * @returns {Promise<Object[]>} - 返回查询结果列表
 */
export const executeCustomQuery = async (request) => {
    return apiClient.post('/custom-query/execute', request);
  };
  
  /**
   * 执行自定义查询（异步）
   * @param {Object} request - 请求体
   * @param {string} request.query - Cypher 查询语句
   * @returns {Promise<Object[]>} - 返回查询结果列表
   */
  export const executeCustomQueryAsync = async (request) => {
    return apiClient.post('/custom-query/execute-async', request);
  };

//通用方法

/**
 * 1.获取节点详细信息
 * @param {string} label - 节点类型（例如 GO, Gene）
 * @param {string} idProperty - 唯一标识属性（例如 GoId, GeneId）
 * @param {string} idValue - 唯一标识值（例如 12, HG4_2963）
 * @returns {Promise<Object>} 返回节点的详细信息
 */
export const getNodeDetails = async (label, idProperty, idValue) => {
  try {
    const response = await apiClient.get("/node-details", {
      params: { label, idProperty, idValue },
    });

    // 打印完整的响应对象
    console.log("API Response:", response);

    // 确保返回的是 response.data
    return response;
  } catch (error) {
    console.error("Error fetching node details:", error);
    throw error;
  }
};


