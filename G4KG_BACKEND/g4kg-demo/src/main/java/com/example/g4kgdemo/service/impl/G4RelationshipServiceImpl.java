package com.example.g4kgdemo.service.impl;

import com.example.g4kgdemo.dto.G4RelationshipSearchResult;
import com.example.g4kgdemo.dto.PaginatedResult;
import com.example.g4kgdemo.repository.basic.G4RelationshipRepository;
import com.example.g4kgdemo.service.G4RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class G4RelationshipServiceImpl implements G4RelationshipService {

    @Autowired
    private G4RelationshipRepository g4RelationshipRepository;

    private static final Map<String, Map<String, String>> NODE_PROPERTIES = new HashMap<>();

    static {
        NODE_PROPERTIES.put("Gene", Map.of("nodeIdProperty", "GeneId", "nodeNameProperty", "GeneSymbol"));
        NODE_PROPERTIES.put("Disease", Map.of("nodeIdProperty", "DiseaseId", "nodeNameProperty", "DiseaseName"));
        NODE_PROPERTIES.put("Protein", Map.of("nodeIdProperty", "ProteinId", "nodeNameProperty", "FullName"));
        NODE_PROPERTIES.put("GO", Map.of("nodeIdProperty", "GoId", "nodeNameProperty", "GoTermName"));
        NODE_PROPERTIES.put("Pathway", Map.of("nodeIdProperty", "PathwayId", "nodeNameProperty", "PathwayName"));
        NODE_PROPERTIES.put("Drug", Map.of("nodeIdProperty", "DrugId", "nodeNameProperty", "Name"));
    }

    @Override
    public boolean validateNodeExistence(String nodeType, String nodeId, String nodeName) {
        if (!NODE_PROPERTIES.containsKey(nodeType)) {
            throw new IllegalArgumentException("Invalid node type: " + nodeType);
        }

        Map<String, String> properties = getNodeProperties(nodeType);
        String nodeIdProperty = properties.get("nodeIdProperty");
        String nodeNameProperty = properties.get("nodeNameProperty");

        // 使用 repository 层方法执行验证查询
        return g4RelationshipRepository.executeValidationQuery(nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName);
    }

    @Override
    public PaginatedResult<G4RelationshipSearchResult> findRelatedG4WithPathAndRelations(
            String nodeType,
            String nodeIdProperty,
            String nodeNameProperty,
            String nodeId,
            String nodeName,
            Integer threshold,
            int hops,
            int skip,
            int limit) {

        try {
            // 验证节点类型
            if (!NODE_PROPERTIES.containsKey(nodeType)) {
                throw new IllegalArgumentException("Invalid node type: " + nodeType);
            }

            // 如果属性为空，则从节点类型获取默认属性
            if (nodeIdProperty == null || nodeNameProperty == null) {
                Map<String, String> properties = getNodeProperties(nodeType);
                nodeIdProperty = properties.get("nodeIdProperty");
                nodeNameProperty = properties.get("nodeNameProperty");
            }

            // 设置阈值
            if ("Disease".equals(nodeType)) {
                if (threshold == null) {
                    threshold = 5;
                } else if (threshold < 0 || threshold > 100) {
                    throw new IllegalArgumentException("Threshold must be between 0 and 100.");
                }
            } else {
                threshold = null;
            }

            // 查询当前页数据
            List<G4RelationshipSearchResult> results = g4RelationshipRepository.findRelatedG4WithPathAndRelations(
                    nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName, threshold, hops, skip, limit);

            // 不查询总记录数以优化性能，延迟加载
            Long totalElements = null; // 用 NULL 表示未加载总数
            Integer totalPages = null;     // 用 NULL 表示未知页数

            // 计算当前页，确保第一页为1
            int currentPage = (skip / limit) + 1; // 修改为从1开始

            // 如果查询结果小于limit，说明这是最后一页
            if (results.size() < limit) {
                // 返回最后一页数据，currentPage 应该是最后一页
                return new PaginatedResult<>(results, totalElements, totalPages, currentPage);
            }

            // 如果查询结果数等于 limit，说明可能还有下一页数据
            // 返回分页结果
            return new PaginatedResult<>(results, totalElements, totalPages, currentPage);

        } catch (IllegalArgumentException e) {
            // 处理非法参数异常，抛出更清晰的消息
            throw new IllegalArgumentException("Invalid input parameters: " + e.getMessage(), e);
        } catch (DataAccessException e) {
            // 处理数据库访问异常
            throw new RuntimeException("Database error occurred. Please try again later.", e);
        } catch (Exception e) {
            // 处理其他未捕获的异常
            throw new RuntimeException("Unexpected error occurred. Please try again later.", e);
        }
    }

    @Override
    public Map<String, String> getNodeProperties(String nodeType) {
        try {
            Map<String, String> properties = NODE_PROPERTIES.get(nodeType);
            if (properties == null) {
                throw new IllegalArgumentException(
                        "Invalid node type: " + nodeType + ". Supported types: " + NODE_PROPERTIES.keySet());
            }
            return properties;
        } catch (IllegalArgumentException e) {
            // 捕获并重新抛出异常
            throw new IllegalArgumentException("Invalid input: " + e.getMessage(), e);
        } catch (Exception e) {
            // 处理其他异常
            throw new RuntimeException("Unexpected error occurred while fetching node properties.", e);
        }
    }
}
