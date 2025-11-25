package com.example.g4kgdemo.repository.basic;

import com.example.g4kgdemo.model.G4;
import com.example.g4kgdemo.dto.G4RelationshipSearchResult;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface G4RelationshipRepository extends Neo4jRepository<G4, Long> {

    /**
     * 根据给定的节点类型、ID或名称查询与G4相关的节点及路径，并支持分页
     */
    default List<G4RelationshipSearchResult> findRelatedG4WithPathAndRelations(
            String nodeType,
            String nodeIdProperty,
            String nodeNameProperty,
            String nodeId,
            String nodeName,
            Integer threshold,
            int hops,
            int skip,  // 分页参数 skip
            int limit  // 分页参数 limit
    ) {
        // 若 nodeId 和 nodeName 都为空，则直接返回空结果，避免无效查询
        if (nodeId == null && nodeName == null) {
            return List.of(); // 返回空列表
        }

        // 动态拼接 Cypher 查询，支持 skip 和 limit
        String cypherQuery = "MATCH path = (startNode)-[*1.." + hops + "]-(g:G4) " +
                "WHERE " +
                "  (($nodeType = 'Disease' AND startNode:Disease) OR " +
                "   ($nodeType = 'Gene' AND startNode:Gene) OR " +
                "   ($nodeType = 'Protein' AND startNode:Protein) OR " +
                "   ($nodeType = 'Pathway' AND startNode:Pathway) OR " +
                "   ($nodeType = 'GO' AND startNode:GO)) " +
                "  AND ($nodeId IS NULL OR $nodeId = '' OR startNode[$nodeIdProperty] = $nodeId) " +
                "  AND ($nodeName IS NULL OR $nodeName = '' OR " +
                "       (CASE " +
                "            WHEN $nodeType = 'Disease' " +
                "            THEN apoc.text.levenshteinDistance(startNode[$nodeNameProperty], $nodeName) < $threshold " +
                "            ELSE startNode[$nodeNameProperty] = $nodeName " +
                "       END)) " +
                "  AND NOT (g:Isolated) AND NOT (startNode:Isolated) " +
                "RETURN g AS g4, " +
                "       [n IN nodes(path) | properties(n)] AS path, " +
                "       [rel IN relationships(path) | rel.RelationName] AS relationNames " +
                "SKIP $skip LIMIT $limit";

        // 调用动态查询方法，传入分页参数
        return this.executeDynamicCypherQuery(
                cypherQuery, nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName, threshold, skip, limit
        );
    }

    /**
     * 执行动态 Cypher 查询，返回查询结果
     */
    @Query("CALL apoc.cypher.run($query, " +
            "{nodeType: $nodeType, nodeIdProperty: $nodeIdProperty, nodeNameProperty: $nodeNameProperty, " +
            "nodeId: $nodeId, nodeName: $nodeName, threshold: $threshold, skip: $skip, limit: $limit}) " +
            "YIELD value RETURN value.g4 AS g4, value.path AS path, value.relationNames AS relationNames")
    List<G4RelationshipSearchResult> executeDynamicCypherQuery(
            @Param("query") String query,
            @Param("nodeType") String nodeType,
            @Param("nodeIdProperty") String nodeIdProperty,
            @Param("nodeNameProperty") String nodeNameProperty,
            @Param("nodeId") String nodeId,
            @Param("nodeName") String nodeName,
            @Param("threshold") Integer threshold,
            @Param("skip") int skip,
            @Param("limit") int limit
    );
    /**
     * 验证指定的节点是否存在
     */
    @Query("MATCH (n) " +
            "WHERE $nodeType IN labels(n) " +
            "  AND ($nodeId IS NULL OR $nodeId = '' OR n[$nodeIdProperty] = $nodeId) " +
            "  AND ($nodeName IS NULL OR $nodeName = '' OR n[$nodeNameProperty] = $nodeName) " +
            "RETURN COUNT(n) > 0")
    boolean executeValidationQuery(
            @Param("nodeType") String nodeType,
            @Param("nodeIdProperty") String nodeIdProperty,
            @Param("nodeNameProperty") String nodeNameProperty,
            @Param("nodeId") String nodeId,
            @Param("nodeName") String nodeName
    );
}

