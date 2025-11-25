package com.example.g4kgdemo.service.impl;

import com.example.g4kgdemo.dto.SubGraphResult;
import com.example.g4kgdemo.service.SubGraphService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SubGraphServiceTest {

    @Autowired
    private SubGraphService subGraphService;

    @Test
    public void testGetSubGraph() {
        // 设置测试输入
        String nodeType = "G4";
        String nodeIdProperty = "G4Id";
        String nodeId = "HG4_4435";
        int depth = 2;
        int limit = 50;
        boolean includeIsolated = false;

        // 调用 Service 方法获取子图
        SubGraphResult result = subGraphService.getSubGraph(nodeType, nodeIdProperty, nodeId, depth, limit, includeIsolated);

        // 打印结果，手动检查
        System.out.println("Test Result - Nodes: " + result.getNodes());
        System.out.println("Test Result - Relationships: " + result.getRelationships());

        // 断言结果
        assertNotNull(result, "Result should not be null");
        assertFalse(result.getNodes().isEmpty(), "Nodes should not be empty");
        assertFalse(result.getRelationships().isEmpty(), "Relationships should not be empty");
    }

    @Test
    public void testInvalidDepth() {
        String nodeType = "G4";
        String nodeIdProperty = "G4Id";
        String nodeId = "HG4_4435";
        int depth = 0; // 设置无效的深度
        int limit = 50;
        boolean includeIsolated = false;

        // 测试非法参数，应该抛出 IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            subGraphService.getSubGraph(nodeType, nodeIdProperty, nodeId, depth, limit, includeIsolated);
        });
    }

    @Test
    public void testInvalidLimit() {
        String nodeType = "G4";
        String nodeIdProperty = "G4Id";
        String nodeId = "HG4_4435";
        int depth = 2;
        int limit = 0; // 设置无效的限制
        boolean includeIsolated = false;

        // 测试非法参数，应该抛出 IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            subGraphService.getSubGraph(nodeType, nodeIdProperty, nodeId, depth, limit, includeIsolated);
        });
    }

    @Test
    public void testNullNodeType() {
        String nodeType = null; // 设置无效的节点类型
        String nodeIdProperty = "G4Id";
        String nodeId = "HG4_4435";
        int depth = 2;
        int limit = 50;
        boolean includeIsolated = false;

        // 测试非法参数，应该抛出 IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            subGraphService.getSubGraph(nodeType, nodeIdProperty, nodeId, depth, limit, includeIsolated);
        });
    }

    @Test
    public void testNullNodeId() {
        String nodeType = "G4";
        String nodeIdProperty = "G4Id";
        String nodeId = null; // 设置无效的节点ID
        int depth = 2;
        int limit = 50;
        boolean includeIsolated = false;

        // 测试非法参数，应该抛出 IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            subGraphService.getSubGraph(nodeType, nodeIdProperty, nodeId, depth, limit, includeIsolated);
        });
    }
}
