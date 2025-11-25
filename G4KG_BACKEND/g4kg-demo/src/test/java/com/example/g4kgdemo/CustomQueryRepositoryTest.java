package com.example.g4kgdemo.repository.diy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomQueryRepositoryTest {

    @Autowired
    private CustomQueryRepository customQueryRepository;

    @Test
    public void testExecuteCustomQuery() {
        // 定义只读 Cypher 查询
        String cypherQuery = "MATCH (g:G4)-[:G4ToDisease]->(d:Disease) RETURN g.G4Id AS g4Id, d.DiseaseName AS diseaseName LIMIT 5";

        // 执行查询并验证结果
        List<CypherQueryResult> results = customQueryRepository.executeCustomQuery(cypherQuery);
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
            customQueryRepository.executeCustomQuery(invalidQuery);
        });
        assertEquals("仅允许只读查询，不允许执行写操作", exception.getMessage());
    }
}
