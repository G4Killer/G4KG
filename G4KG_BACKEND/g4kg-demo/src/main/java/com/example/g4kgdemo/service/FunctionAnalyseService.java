package com.example.g4kgdemo.service;

import com.example.g4kgdemo.dto.*;

import java.util.List;

public interface FunctionAnalyseService {

    /**
     * 获取G4集合并分析GO功能分布
     * @param g4Ids G4Id集合
     * @param limit 返回的最大数量
     * @return GO功能分布分析结果
     */
    List<GOFunctionDistributionResult> analyzeGOFunctionDistribution(
            List<String> g4Ids, int limit);

    /**
     * 获取G4集合并分析Pathway功能分布
     * @param g4Ids G4Id集合
     * @param limit 返回的最大数量
     * @return Pathway功能分布分析结果
     */
    List<PathwayDistributionResult> analyzePathwayDistribution(
            List<String> g4Ids, int limit);

    /**
     * 获取G4集合并构建功能富集分析网络
     *
     * @param g4Ids G4Id集合
     * @return 功能富集分析网络
     */
    G4FunctionEnrichmentNetwork buildG4FunctionEnrichmentNetwork(
            List<String> g4Ids);

    /**
     * 根据属性或关系查找G4集合并返回其ID集合
     * @param nodeType 节点类型
     * @param nodeIdProperty 节点ID属性
     * @param nodeNameProperty 节点名称属性
     * @param nodeId 节点ID
     * @param nodeName 节点名称
     * @param g4Id G4 ID
     * @param chr 染色体
     * @param location 位置
     * @param sampleName 样本名称
     * @param confidenceLevel 置信水平
     * @param score 分数
     * @param threshold 阈值
     * @param hops 跳数
     * @param skip 跳过的条目数
     * @param limit 返回的最大条目数
     * @param useRelationship 是否通过关系查找
     * @return G4 ID 集合
     */
    List<String> getG4Ids(String nodeType, String nodeIdProperty, String nodeNameProperty,
                          String nodeId, String nodeName, String g4Id, String chr, String location,
                          String sampleName, String confidenceLevel, String score,
                          Integer threshold, int hops, int skip, int limit, boolean useRelationship);
}
