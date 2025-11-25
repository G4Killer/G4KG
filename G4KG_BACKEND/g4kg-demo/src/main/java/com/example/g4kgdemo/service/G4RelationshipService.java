package com.example.g4kgdemo.service;

import com.example.g4kgdemo.dto.G4RelationshipSearchResult;
import com.example.g4kgdemo.dto.PaginatedResult;

import java.util.Map;

public interface G4RelationshipService {

    PaginatedResult<G4RelationshipSearchResult> findRelatedG4WithPathAndRelations(
            String nodeType,
            String nodeIdProperty,
            String nodeNameProperty,
            String nodeId,
            String nodeName,
            Integer threshold,
            int hops,
            int skip,
            int limit
    );

    /**
     * 获取节点类型对应的 ID 和 Name 属性
     */
    Map<String, String> getNodeProperties(String nodeType);

    /**
     * 验证指定节点是否存在
     *
     * @param nodeType 节点类型
     * @param nodeId 节点 ID
     * @param nodeName 节点名称
     * @return 如果节点存在返回 true，否则返回 false
     */
    boolean validateNodeExistence(String nodeType, String nodeId, String nodeName);

}
