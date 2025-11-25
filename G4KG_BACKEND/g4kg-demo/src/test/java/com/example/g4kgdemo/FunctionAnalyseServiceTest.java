package com.example.g4kgdemo.service.impl;

import com.example.g4kgdemo.dto.G4FunctionEnrichmentNetwork;
import com.example.g4kgdemo.dto.GOFunctionDistributionResult;
import com.example.g4kgdemo.dto.PathwayDistributionResult;
import com.example.g4kgdemo.service.FunctionAnalyseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class FunctionAnalyseServiceTest {

    @Autowired
    private FunctionAnalyseService functionAnalyseService;

    /**
     * 测试通过属性查找 G4 集合，并进行富集分析（GO功能分布）
     */
    @Test
    public void testAnalyzeGOFunctionDistributionByAttribute() {
        // 模拟属性查找的条件
        String sampleName = "HEK293T_Flavopiridol.CUT.Tag_DMSO"; // 示例样本名
        int skip = 0;
        int limit = 300;

        // 获取 G4 ID 集合
        List<String> g4Ids = functionAnalyseService.getG4Ids(
                null, null, null, null, null,
                null, null, null, sampleName,null, null,
                null, 0, skip, limit, false
        );

        // 调用富集分析方法：GO 功能分布分析
        List<GOFunctionDistributionResult> goResults = functionAnalyseService.analyzeGOFunctionDistribution(g4Ids, 10);

        // 断言富集分析结果不为空
        assertFalse(goResults.isEmpty(), "GO 功能分布分析结果应不为空");
    }

    /**
     * 测试通过关系查找 G4 集合，并进行富集分析（Pathway功能分布）
     */
    @Test
    public void testAnalyzePathwayDistributionByRelationship() {
        // 模拟关系查找的条件
        String nodeType = "GO";
        String nodeIdProperty = null;
        String nodeNameProperty = null;
        String nodeId = "12";  // 示例节点ID
        String nodeName = null;  // 示例节点名称
        Integer threshold = null;
        int hops = 2;
        int skip = 0;
        int limit = 300;

        // 获取 G4 ID 集合
        List<String> g4Ids = functionAnalyseService.getG4Ids(
                nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName,
                null, null, null, null, null,null,
                threshold, hops, skip, limit, true
        );

        // 调用富集分析方法：Pathway 功能分布分析
        List<PathwayDistributionResult> pathwayResults = functionAnalyseService.analyzePathwayDistribution(g4Ids, 10);

        // 断言富集分析结果不为空
        assertFalse(pathwayResults.isEmpty(), "Pathway 功能分布分析结果应不为空");
    }

    /**
     * 测试构建 G4 功能富集分析网络
     */
    @Test
    public void testBuildG4FunctionEnrichmentNetwork() {
        // 使用属性查找或关系查找获取 G4 ID 集合
        List<String> g4Ids = functionAnalyseService.getG4Ids(
                null, null, null, null, null,
                null, null, null, "HEK293T_Flavopiridol.CUT.Tag_DMSO", null,
                null, 0, 0, 0, 50,false
        );

        // 调用方法构建 G4 功能富集分析网络
        G4FunctionEnrichmentNetwork enrichmentNetwork = functionAnalyseService.buildG4FunctionEnrichmentNetwork(g4Ids);

        // 断言富集分析网络对象不为空
        assertNotNull(enrichmentNetwork, "G4 功能富集分析网络应不为空");

        // 断言网络中应该包含一些富集信息（可以根据业务逻辑进一步调整断言）
        assertFalse(enrichmentNetwork.getNodes().isEmpty(), "富集分析网络的节点集合不应为空");
        assertFalse(enrichmentNetwork.getRelationships().isEmpty(), "富集分析网络的边集合不应为空");
    }
}
