package com.example.g4kgdemo.service.impl;

import com.example.g4kgdemo.dto.*;
import com.example.g4kgdemo.model.G4;
import com.example.g4kgdemo.repository.enrichment.FunctionAnalyseRepository;
import com.example.g4kgdemo.service.FunctionAnalyseService;
import com.example.g4kgdemo.service.G4RelationshipService;
import com.example.g4kgdemo.service.G4Service;
import com.example.g4kgdemo.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FunctionAnalyseServiceImpl implements FunctionAnalyseService {

    @Autowired
    private FunctionAnalyseRepository functionAnalyseRepository;

    @Autowired
    private G4Service g4Service;

    @Autowired
    private G4RelationshipService g4RelationshipService;

    /**
     * 获取 G4 集合
     **/
    @Override
    public List<String> getG4Ids(
            String nodeType, String nodeIdProperty, String nodeNameProperty,
            String nodeId, String nodeName, String g4Id, String chr, String location,
            String sampleName, String confidenceLevel, String score,
            Integer threshold, int hops, int skip, int limit, boolean useRelationship) {

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0.");
        }

        // 根据 useRelationship 固定参数
        if (useRelationship) {
            // 属性查询相关参数设置为无效值
            g4Id = null;
            chr = null;
            location = null;
            sampleName = null;
            confidenceLevel = null;
            score = null;
        } else {
            // 关系查询相关参数设置为无效值
            nodeType = null;
            nodeIdProperty = null;
            nodeNameProperty = null;
            nodeId = null;
            nodeName = null;
            threshold = null;
            hops = 0;
        }

        // 使用关系查询
        if (useRelationship) {
            List<G4RelationshipSearchResult> relatedG4Results = getG4CollectionByRelationship(
                    nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName,
                    threshold, hops, skip, limit);

            return relatedG4Results.stream()
                    .map(result -> result.getG4()) // 获取 G4 对象
                    .filter(Objects::nonNull)     // 确保 G4 不为空
                    .map(G4::getG4Id)             // 提取 G4 ID
                    .collect(Collectors.toList());
        }

        // 使用属性查询
        List<G4AttributeSearchResult> g4Collection = getG4CollectionByAttribute(
                g4Id, chr, location, sampleName, confidenceLevel, score,
                skip, limit);

        return g4Collection.stream()
                .map(G4AttributeSearchResult::getG4Id) // 提取 G4 ID
                .collect(Collectors.toList());
    }


    /**
     * 获取 G4 集合并进行 GO 功能分析
     **/
    @Override
    public List<GOFunctionDistributionResult> analyzeGOFunctionDistribution(
            List<String> g4Ids, int limit) {

        if (g4Ids == null || g4Ids.isEmpty()) {
            throw new IllegalArgumentException("G4 ID list cannot be null or empty.");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0.");
        }

        // 返回 GO 功能分布分析结果
        return functionAnalyseRepository.analyzeGOFunctionDistributionByIds(g4Ids, limit);
    }

    /**
     * 获取 G4 集合并进行 Pathway 功能分析
     **/
    @Override
    public List<PathwayDistributionResult> analyzePathwayDistribution(
            List<String> g4Ids, int limit) {

        if (g4Ids == null || g4Ids.isEmpty()) {
            throw new IllegalArgumentException("G4 ID list cannot be null or empty.");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0.");
        }

        // 返回 Pathway 功能分布分析结果
        return functionAnalyseRepository.analyzePathwayDistributionByIds(g4Ids, limit);
    }

    /**
     * 获取 G4 集合并构建富集功能网络
     **/
    @Override
    public G4FunctionEnrichmentNetwork buildG4FunctionEnrichmentNetwork(
            List<String> g4Ids) {

        if (g4Ids == null || g4Ids.isEmpty()) {
            throw new IllegalArgumentException("G4 ID list cannot be null or empty.");
        }

        // 返回功能富集分析网络
        return functionAnalyseRepository.buildG4FunctionEnrichmentNetwork(g4Ids);
    }

    /**
     * 获取 G4 集合（通过属性）
     **/
    private List<G4AttributeSearchResult> getG4CollectionByAttribute(
            String g4Id, String chr, String location, String sampleName,
            String confidenceLevel, String score, int skip, int limit) {

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0.");
        }

        // 使用 G4Service 获取分页结果并解包
        PaginatedResult<G4AttributeSearchResult> paginatedResult = g4Service.findByCombinedFilters(
                g4Id, chr, location, sampleName, confidenceLevel, score, skip, limit);
        return PaginationUtils.extractContent(paginatedResult);
    }

    /**
     * 获取 G4 集合（通过关系）
     **/
    private List<G4RelationshipSearchResult> getG4CollectionByRelationship(
            String nodeType, String nodeIdProperty, String nodeNameProperty,
            String nodeId, String nodeName, Integer threshold, int hops, int skip, int limit) {

        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0.");
        }

        // 使用 G4RelationshipService 获取分页结果并解包
        PaginatedResult<G4RelationshipSearchResult> paginatedResult = g4RelationshipService.findRelatedG4WithPathAndRelations(
                nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName, threshold, hops, skip, limit);
        return PaginationUtils.extractContent(paginatedResult);
    }
}

