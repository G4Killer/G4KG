package com.example.g4kgdemo.repository.visualization;

import com.example.g4kgdemo.dto.SubGraphNode;
import com.example.g4kgdemo.dto.SubGraphRelation;
import com.example.g4kgdemo.dto.SubGraphResult;
import com.example.g4kgdemo.model.GraphEntity;
import com.example.g4kgdemo.utils.SubGraphUtil;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

@Repository
public interface GetSubGraphRepository extends Neo4jRepository<GraphEntity, Long> {

    @Query("CALL apoc.cypher.run($query, $params) YIELD value RETURN value")
    List<Map<String, Object>> executeRawQuery(
            @Param("query") String query,
            @Param("params") Map<String, Object> params
    );

    default SubGraphResult findSubGraphByNode(
            String nodeType, String nodeIdProperty, String nodeId, int depth, int limit, boolean includeIsolated) {

        String cypherQuery = String.format(
                "MATCH (startNode:%s) WHERE startNode.%s = $nodeId " +
                        "WITH startNode " +
                        "MATCH path = (startNode)-[*1..%d]-(relatedNode) " +
                        "WITH startNode, relatedNode, relationships(path) AS rels " +
                        "LIMIT $limit " +
                        "RETURN collect(DISTINCT startNode) + collect(DISTINCT relatedNode) AS nodes, " +
                        "collect(DISTINCT rels) AS relationships",
                nodeType, nodeIdProperty, depth
        );

        Map<String, Object> params = Map.of("nodeId", nodeId, "limit", limit);

        List<Map<String, Object>> rawResults = this.executeRawQuery(cypherQuery, params);

        if (rawResults.isEmpty()) {
            return new SubGraphResult(List.of(), List.of());
        }

        Map<String, Object> rawResult = rawResults.get(0);

        // 使用工具类进行解析
        List<SubGraphNode> parsedNodes = SubGraphUtil.parseNodes((List<Object>) rawResult.get("nodes"));
        List<SubGraphRelation> parsedRelationships = SubGraphUtil.parseRelationships(
                ((List<List<Object>>) rawResult.get("relationships")).stream()
                        .flatMap(List::stream)
                        .toList()
        );

        return new SubGraphResult(parsedNodes, parsedRelationships);
    }
}
