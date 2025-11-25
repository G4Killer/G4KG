package com.example.g4kgdemo.repository.enrichment;

import com.example.g4kgdemo.dto.*;
import com.example.g4kgdemo.model.G4;
import com.example.g4kgdemo.utils.SubGraphUtil;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
/*

@SpringBootTest
public class FunctionAnalyseRepositoryTest {

    @Autowired
    private FunctionAnalyseRepository functionAnalyseRepository;

    //GO功能分布的根据关系获取G4集合测试
    @Test
    public void testAnalyzeGOFunctionDistribution() {
        // Step 1: 使用 findRelatedG4WithPathAndRelations 查询相关的 G4 集合
        String nodeType = "GO"; // 根据实际情况选择节点类型
        String nodeIdProperty = "GoId"; // 节点ID属性
        String nodeNameProperty = "GoTermName"; // 节点名称属性
        String nodeId = "12"; // 假设查询某个特定的GeneId，实际可以调整为动态值
        String nodeName = null; // 可以传入实际的GeneSymbol或null
        Integer threshold = null; // Levenshtein距离阈值，根据需要调整
        int hops = 2; // 跳数（路径长度）
        int limit = 300; // 限制返回结果数量
        int skip = 0;

        // 使用 G4RelationshipRepository 查询相关的G4集合
        List<G4RelationshipSearchResult> relatedG4Results = functionAnalyseRepository.findRelatedG4WithPathAndRelations(
                nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName, threshold, hops, skip, limit
        );

        // Step 2: 提取 G4Id 集合
        List<String> g4Ids = relatedG4Results.stream()
                .map(result -> result.getG4()) // 获取 G4 对象
                .filter(Objects::nonNull) // 确保 G4 对象不为空
                .map(G4::getG4Id)  // 提取 G4Id
                .collect(Collectors.toList());

        System.out.println("G4 Collection Size: " + g4Ids.size());
        g4Ids.forEach(g4Id -> System.out.println("G4 Node: " + g4Id));

        // Step 3: 检查 G4Id 是否为空
        assertFalse(g4Ids.isEmpty(), "G4 collection should not be empty.");

        // Step 4: 测试 GO 功能分布分析
        List<GOFunctionDistributionResult> results = functionAnalyseRepository.analyzeGOFunctionDistributionByIds(g4Ids, 10);

        // Step 5: 验证结果
        System.out.println("GO Distribution Results: " + results);
        assertTrue(results.size() > 0, "GO Function Distribution analysis should return results.");

    }

    //GO功能分布的根据属性获取G4集合测试
    @Test
    public void testAnalyzeGOFunctionDistribution() {
        // Step 1: 查询 G4 集合
        List<G4> g4Collection = functionAnalyseRepository.findByCombinedFilters(
                null, null, null, "HEK293T_Flavopiridol.CUT.Tag_DMSO", null, null, 0, 300
        );

        // Step 2: 提取 G4Id 集合
        List<String> g4Ids = g4Collection.stream()
                .map(G4::getG4Id)
                .collect(Collectors.toList());

        System.out.println("G4 Collection Size: " + g4Ids.size());
        g4Ids.forEach(g4Id -> System.out.println("G4 Node: " + g4Id));

        // Step 3: 检查 G4Id 是否为空
        assertFalse(g4Ids.isEmpty(), "G4 collection should not be empty.");

        // Step 4: 测试 GO 功能分布分析
        List<GOFunctionDistributionResult> results = functionAnalyseRepository.analyzeGOFunctionDistributionByIds(g4Ids, 10);

        // Step 5: 验证结果
        System.out.println("GO Distribution Results: " + results);
        assertTrue(results.size() > 0, "GO Function Distribution analysis should return results.");
    }



    @Test
    public void testAnalyzePathwayDistribution() {
        // Step 1: 查询 G4 集合
        List<G4> g4Collection = functionAnalyseRepository.findByCombinedFilters(
                null, null, null, "HEK293T_Flavopiridol.CUT.Tag_DMSO", null, null, 0, 300
        );

        // Step 2: 提取 G4Id 集合
        List<String> g4Ids = g4Collection.stream()
                .map(G4::getG4Id)
                .collect(Collectors.toList());

        System.out.println("G4 Collection Size: " + g4Ids.size());
        g4Ids.forEach(g4Id -> System.out.println("G4 Node: " + g4Id));

        // Step 3: 检查 G4Id 是否为空
        assertFalse(g4Ids.isEmpty(), "G4 collection should not be empty.");

        // Step 4: 测试 Pathway 分布分析
        List<PathwayDistributionResult> results = functionAnalyseRepository.analyzePathwayDistributionByIds(g4Ids, 10);

        // Step 5: 验证结果
        System.out.println("Pathway Distribution Results: " + results);
        assertTrue(results.size() > 0, "Pathway Distribution analysis should return results.");
    }
/*
    //使用属性查询G4集合测试功能富集网络
    @Test
    public void testBuildG4FunctionEnrichmentNetwork() {
        // 查询 G4 集合
        List<G4> g4Collection = functionAnalyseRepository.findByCombinedFilters(
                null, null, null, "HEK293T_Flavopiridol.CUT.Tag_DMSO", null, null, 0, 50
        );

        // 提取 G4Id 集合
        List<String> g4Ids = g4Collection.stream()
                .map(G4::getG4Id)
                .collect(Collectors.toList());

        System.out.println("G4 Collection Size: " + g4Ids.size());
        g4Ids.forEach(g4Id -> System.out.println("G4 Node: " + g4Id));

        // 确保 G4 集合不为空
        assertFalse(g4Ids.isEmpty(), "G4 collection should not be empty.");

        try {
            // 调用 buildG4FunctionEnrichmentNetwork 方法，构建功能富集网络
            G4FunctionEnrichmentNetwork network = functionAnalyseRepository.buildG4FunctionEnrichmentNetwork(g4Ids, 100);

            // 输出最终的网络结果
            System.out.println("Genes: " + network.getGenes());
            System.out.println("GO Terms: " + network.getGoTerms());
            System.out.println("Pathways: " + network.getPathways());

            // 验证网络结果
            assertNotNull(network, "Network results should not be null.");

            // 验证各个部分是否不为空并且至少包含一个节点
            assertTrue(network.getGenes().size() > 0, "Genes should not be empty.");
            assertTrue(network.getGoTerms().size() > 0, "GO terms should not be empty.");
            assertTrue(network.getPathways().size() > 0, "Pathways should not be empty.");

            // 额外检查：确保节点返回值正确处理单一节点和节点列表情况
            // 测试 Genes 节点的处理
            network.getGenes().forEach(gene -> {
                assertNotNull(gene, "Gene node should not be null.");
                assertTrue(gene instanceof SubGraphNode, "Gene node should be of type SubGraphNode.");
            });

            // 测试 GO Terms 节点的处理
            network.getGoTerms().forEach(goTerm -> {
                assertNotNull(goTerm, "GO Term node should not be null.");
                assertTrue(goTerm instanceof SubGraphNode, "GO Term node should be of type SubGraphNode.");
            });

            // 测试 Pathways 节点的处理
            network.getPathways().forEach(pathway -> {
                assertNotNull(pathway, "Pathway node should not be null.");
                assertTrue(pathway instanceof SubGraphNode, "Pathway node should be of type SubGraphNode.");
            });

            // **打印最终子图结果**
            System.out.println("SubGraph Final Output:");
            System.out.println("Nodes: " + network.getNodes());  // 包括所有节点
            System.out.println("Relationships: " + network.getRelationships());  // 包括所有关系

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }


    //使用关系查询G4集合测试功能富集网络
    @Test
    public void testBuildG4FunctionEnrichmentNetwork() {
        // Step 1: 使用 findRelatedG4WithPathAndRelations 查询相关的 G4 集合
        String nodeType = "GO"; // 根据实际情况选择节点类型
        String nodeIdProperty = "GoId"; // 节点ID属性
        String nodeNameProperty = "GoTermName"; // 节点名称属性
        String nodeId = "12"; // 假设查询某个特定的GeneId，实际可以调整为动态值
        String nodeName = null; // 可以传入实际的GeneSymbol或null
        Integer threshold = null; // Levenshtein距离阈值，根据需要调整
        int hops = 2; // 跳数（路径长度）
        int limit = 100; // 限制返回结果数量
        int skip = 0;

        // 使用 G4RelationshipRepository 查询相关的G4集合
        List<G4RelationshipSearchResult> relatedG4Results = functionAnalyseRepository.findRelatedG4WithPathAndRelations(
                nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName, threshold, hops, skip, limit
        );

        // Step 2: 提取 G4Id 集合
        List<String> g4Ids = relatedG4Results.stream()
                .map(result -> result.getG4()) // 获取 G4 对象
                .filter(Objects::nonNull) // 确保 G4 对象不为空
                .map(G4::getG4Id)  // 提取 G4Id
                .collect(Collectors.toList());

        System.out.println("G4 Collection Size: " + g4Ids.size());
        g4Ids.forEach(g4Id -> System.out.println("G4 Node: " + g4Id));

        // 确保 G4 集合不为空
        assertFalse(g4Ids.isEmpty(), "G4 collection should not be empty.");

        try {
            // 调用 buildG4FunctionEnrichmentNetwork 方法，构建功能富集网络
            G4FunctionEnrichmentNetwork network = functionAnalyseRepository.buildG4FunctionEnrichmentNetwork(g4Ids, 100);

            // 输出最终的网络结果
            System.out.println("Genes: " + network.getGenes());
            System.out.println("GO Terms: " + network.getGoTerms());
            System.out.println("Pathways: " + network.getPathways());

            // 验证网络结果
            assertNotNull(network, "Network results should not be null.");

            // 验证各个部分是否不为空并且至少包含一个节点
            assertTrue(network.getGenes().size() > 0, "Genes should not be empty.");
            assertTrue(network.getGoTerms().size() > 0, "GO terms should not be empty.");
            assertTrue(network.getPathways().size() > 0, "Pathways should not be empty.");

            // 额外检查：确保节点返回值正确处理单一节点和节点列表情况
            // 测试 Genes 节点的处理
            network.getGenes().forEach(gene -> {
                assertNotNull(gene, "Gene node should not be null.");
                assertTrue(gene instanceof SubGraphNode, "Gene node should be of type SubGraphNode.");
            });

            // 测试 GO Terms 节点的处理
            network.getGoTerms().forEach(goTerm -> {
                assertNotNull(goTerm, "GO Term node should not be null.");
                assertTrue(goTerm instanceof SubGraphNode, "GO Term node should be of type SubGraphNode.");
            });

            // 测试 Pathways 节点的处理
            network.getPathways().forEach(pathway -> {
                assertNotNull(pathway, "Pathway node should not be null.");
                assertTrue(pathway instanceof SubGraphNode, "Pathway node should be of type SubGraphNode.");
            });

            // **打印最终子图结果**
            System.out.println("SubGraph Final Output:");
            System.out.println("Nodes: " + network.getNodes());  // 包括所有节点
            System.out.println("Relationships: " + network.getRelationships());  // 包括所有关系

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
*/
