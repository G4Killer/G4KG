package com.example.g4kgdemo.repository.enrichment;

import com.example.g4kgdemo.dto.*;
import com.example.g4kgdemo.model.G4;
import com.example.g4kgdemo.repository.basic.G4RelationshipRepository;
import com.example.g4kgdemo.repository.basic.G4Repository;
import com.example.g4kgdemo.utils.SubGraphUtil;
import org.neo4j.cypherdsl.core.Relationship;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import org.w3c.dom.Node;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Repository
public interface FunctionAnalyseRepository
        extends Neo4jRepository<G4, Long>, G4Repository, G4RelationshipRepository {

    @Query("MATCH (g:G4)-[:G4ToGene]->(gen:Gene)-[:GeneToGO]->(go:GO) " +
            "WHERE g.G4Id IN $g4Ids " +
            "RETURN go.GoId AS goId, go.GoTermName AS goName, COUNT(g) AS g4Count " +
            "ORDER BY g4Count DESC " +
            "LIMIT $limit")
    List<GOFunctionDistributionResult> analyzeGOFunctionDistributionByIds(
            @Param("g4Ids") List<String> g4Ids,
            @Param("limit") int limit);

    @Query("MATCH (g:G4)-[:G4ToGene]->(gen:Gene)-[:GeneToPathway]->(pathway:Pathway) " +
            "WHERE g.G4Id IN $g4Ids " +
            "RETURN pathway.PathwayId AS pathwayId, pathway.PathwayName AS pathwayName, COUNT(g) AS g4Count " +
            "ORDER BY g4Count DESC " +
            "LIMIT $limit")
    List<PathwayDistributionResult> analyzePathwayDistributionByIds(
            @Param("g4Ids") List<String> g4Ids,
            @Param("limit") int limit);

    @Query("CALL apoc.cypher.run($query, $params) YIELD value RETURN value")
    List<Map<String, Object>> executeRawQuery(
            @Param("query") String query,
            @Param("params") Map<String, Object> params
    );

    public default G4FunctionEnrichmentNetwork buildG4FunctionEnrichmentNetwork(List<String> g4Ids) {
        // 构建Cypher查询字符串
        String cypherQuery =
                "MATCH (g4:G4) " +
                        "WHERE g4.G4Id IN $g4Ids AND NOT 'Isolated' IN labels(g4) " +
                        // OPTIONAL MATCH to find G4ToGene relationships
                        "OPTIONAL MATCH (g4)-[rel1:G4ToGene]->(gene:Gene) " +
                        // OPTIONAL MATCH to find GeneToGO relationships
                        "OPTIONAL MATCH (gene)-[rel2:GeneToGO]->(go:GO) " +
                        // OPTIONAL MATCH to find GeneToPathway relationships
                        "OPTIONAL MATCH (gene)-[rel3:GeneToPathway]->(pathway:Pathway) " +
                        // Collect all relevant nodes
                        "WITH g4, collect(DISTINCT gene) AS genes, collect(DISTINCT go) AS goTerms, collect(DISTINCT pathway) AS pathways, " +
                        "     collect(DISTINCT rel1) AS g4ToGeneRels, collect(DISTINCT rel2) AS geneToGORels, collect(DISTINCT rel3) AS geneToPathwayRels " +
                        // Combine relationships into one list for processing
                        "WITH g4, genes, goTerms, pathways, " +
                        "     g4ToGeneRels + geneToGORels + geneToPathwayRels AS allRelationships " +
                        // Return all the gathered data: nodes and relationships
                        "RETURN g4, genes, goTerms, pathways, allRelationships ";

        // 设置查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("g4Ids", g4Ids);  // 传递 g4Id 列表

        // 执行查询
        List<Map<String, Object>> rawResults = this.executeRawQuery(cypherQuery, params);

        // 如果查询结果为空，返回空的 G4FunctionEnrichmentNetwork
        if (rawResults.isEmpty()) {
            System.out.println("No results found for G4Id: " + g4Ids);
            return new G4FunctionEnrichmentNetwork();
        }
/*
        // 输出原始查询结果
        System.out.println("Raw Results: " + rawResults);
*/
        // 解析节点（根据图谱设计）
        List<SubGraphNode> g4 = new ArrayList<>();
        List<SubGraphNode> genes = new ArrayList<>();
        List<SubGraphNode> goTerms = new ArrayList<>();
        List<SubGraphNode> pathways = new ArrayList<>();

        // 解析每个记录
        for (Map<String, Object> rawResult : rawResults) {
            g4.addAll(SubGraphUtil.parseNodes(getNodeList(rawResult.get("g4"))));
            genes.addAll(SubGraphUtil.parseNodes(getNodeList(rawResult.get("genes"))));
            goTerms.addAll(SubGraphUtil.parseNodes(getNodeList(rawResult.get("goTerms"))));
            pathways.addAll(SubGraphUtil.parseNodes(getNodeList(rawResult.get("pathways"))));
        }
/*
        // 输出解析后的节点
        System.out.println("Parsed Nodes:");
        System.out.println("G4 Nodes: " + g4.size());
        System.out.println("Genes: " + genes.size());
        System.out.println("GO Terms: " + goTerms.size());
        System.out.println("Pathways: " + pathways.size());
*/
        // 解析关系（根据图谱设计）
        List<SubGraphRelation> relations = new ArrayList<>();
        for (Map<String, Object> rawResult : rawResults) {
            relations.addAll(SubGraphUtil.parseRelationships(getRelationshipList(rawResult.get("allRelationships"))));
        }
/*
        // 输出解析后的关系
        System.out.println("Parsed Relationships: " + relations.size());
        relations.forEach(relation -> {
            System.out.println("Relationship: " + relation);
        });
*/
        // 创建 G4FunctionEnrichmentNetwork 对象，并设置各个字段
        G4FunctionEnrichmentNetwork enrichmentNetwork = new G4FunctionEnrichmentNetwork();
        enrichmentNetwork.setG4(g4);  // 设置 G4 节点
        enrichmentNetwork.setGenes(genes);
        enrichmentNetwork.setGoTerms(goTerms);
        enrichmentNetwork.setPathways(pathways);
        enrichmentNetwork.setGeneRelations(relations);  // 关系需要根据实际定义做进一步细分
/*
        // 输出最终的网络数据
        System.out.println("Final Network Output:");
        System.out.println("G4 Nodes: " + enrichmentNetwork.getG4().size());
        System.out.println("Genes: " + enrichmentNetwork.getGenes().size());
        System.out.println("GO Terms: " + enrichmentNetwork.getGoTerms().size());
        System.out.println("Pathways: " + enrichmentNetwork.getPathways().size());
*/
        // 返回最终的 G4FunctionEnrichmentNetwork
        return enrichmentNetwork;
    }

    // 辅助方法：处理可能是单一节点的情况，将其转换为节点列表
    private List<Object> getNodeList(Object nodeObject) {
        if (nodeObject instanceof List) {
            return (List<Object>) nodeObject;
        } else if (nodeObject instanceof InternalNode) {
            // 如果是单个 InternalNode，包装成 List 返回
            return Collections.singletonList(nodeObject);
        } else {
            return Collections.emptyList();
        }
    }

    // 辅助方法：处理可能是单一关系或多个关系的情况，将其转换为关系列表
    private List<Object> getRelationshipList(Object relationshipObject) {
        if (relationshipObject instanceof List) {
            // 如果是一个列表，则将列表中的元素直接作为关系传递给解析方法
            return ((List<?>) relationshipObject).stream()
                    .flatMap(item -> {
                        if (item instanceof List) {
                            // 如果是嵌套的列表，递归处理
                            return ((List<?>) item).stream().map(rel -> (InternalRelationship) rel);
                        } else {
                            // 直接传递关系
                            return Stream.of((InternalRelationship) item);
                        }
                    })
                    .collect(Collectors.toList());
        } else {
            // 如果是单一的关系，直接转换
            return Collections.singletonList((InternalRelationship) relationshipObject);
        }
    }

    // 辅助方法：将单个关系对象转换为 SubGraphRelation
    private SubGraphRelation mapToRelation(Object relationship) {
        // 确保传入的是 Neo4j 5.x 的 InternalRelationship 类型
        if (relationship instanceof InternalRelationship) {
            InternalRelationship rel = (InternalRelationship) relationship;

            // 获取关系的起始节点和结束节点 ID（返回的是 long 类型）
            long startNodeId = rel.startNodeId();
            long endNodeId = rel.endNodeId();

            // 获取关系的类型
            String relationshipType = rel.type();

            // 获取关系的属性
            Map<String, Object> properties = rel.asMap();

            // 创建并返回 SubGraphRelation 对象，其中包含节点 ID 和关系类型
            return new SubGraphRelation(rel.id(), relationshipType, startNodeId, endNodeId, properties);
        }
        return null;
    }
}

