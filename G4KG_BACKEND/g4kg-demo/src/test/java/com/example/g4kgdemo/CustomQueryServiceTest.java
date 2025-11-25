package com.example.g4kgdemo.service.impl;

import com.example.g4kgdemo.repository.diy.CypherQueryResult;
import com.example.g4kgdemo.service.CustomQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomQueryServiceTest {

    @Autowired
    private CustomQueryService customQueryService;

    @Test
    public void testExecuteCustomQuery() {
        // 定义只读 Cypher 查询
        String cypherQuery = "MATCH (g:G4)-[:G4ToDisease]->(d:Disease) RETURN g.G4Id AS g4Id, d.DiseaseName AS diseaseName LIMIT 5";

        // 执行查询并验证结果
        List<CypherQueryResult> results = customQueryService.executeCustomQuery(cypherQuery);
        assertNotNull(results, "查询结果不应为空");
        assertFalse(results.isEmpty(), "查询结果应至少包含一个记录");

        // 输出结果用于手动检查
        results.forEach(result -> System.out.println("Result: " + result.getResult()));
    }

    @Test
    public void testInvalidWriteQuery() {
        // 定义一个包含写操作的查询
        String invalidQuery = "CREATE (n:TestNode {name: 'test'}) RETURN n";

        // 验证写操作会抛出异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customQueryService.executeCustomQuery(invalidQuery);
        });
        assertEquals("查询失败，禁止执行写操作: 仅允许只读查询，不允许执行写操作", exception.getMessage());
    }

    @Test
    public void testExecuteCustomQueryAsync() {
        // 定义只读 Cypher 查询
        String cypherQuery = "MATCH (g:G4)-[:G4ToDisease]->(d:Disease) RETURN g.G4Id AS g4Id, d.DiseaseName AS diseaseName LIMIT 5";

        // 执行异步查询并验证结果
        List<CypherQueryResult> results = customQueryService.executeCustomQueryAsync(cypherQuery);
        assertNotNull(results, "查询结果不应为空");
        assertFalse(results.isEmpty(), "查询结果应至少包含一个记录");

        // 输出结果用于手动检查
        results.forEach(result -> System.out.println("Async Result: " + result.getResult()));
    }

    @Test
    public void testInvalidQueryWithIllegalCharacters() {
        // 定义一个包含非法字符的查询
        String invalidQuery = "DELETE n WHERE n.name = 'test'";

        // 验证非法查询会抛出异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            customQueryService.executeCustomQuery(invalidQuery);
        });
        assertEquals("查询失败，禁止执行写操作: 仅允许只读查询，不允许执行写操作", exception.getMessage());
    }


    @Test
    public void testValidQuery() {
        // 定义合法的只读查询
        String validQuery = "MATCH (n:Drug) RETURN n";

        // 执行查询并验证
        List<CypherQueryResult> results = customQueryService.executeCustomQuery(validQuery);
        assertNotNull(results, "查询结果不应为空");
        assertFalse(results.isEmpty(), "查询结果应至少包含一个记录");
    }
}
