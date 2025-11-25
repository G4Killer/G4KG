package com.example.g4kgdemo.repository.diy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public class CustomQueryRepositoryImpl implements CustomQueryRepository {

    @Autowired
    private Neo4jClient neo4jClient;

    @Override
    public List<CypherQueryResult> executeCustomQuery(String cypherQuery) {
        try {
            // 验证查询是否为只读操作
            validateQuery(cypherQuery);

            // 执行查询并将结果封装到 CypherQueryResult 中
            return neo4jClient.query(cypherQuery)
                    .fetchAs(CypherQueryResult.class)
                    .mappedBy((typeSystem, record) -> new CypherQueryResult(record.asMap()))
                    .all()
                    .stream()
                    .toList(); // 将结果转换为 List
        } catch (IllegalArgumentException e) {
            // 捕获无效查询异常并抛出
            throw new IllegalArgumentException("查询失败，禁止执行写操作: " + e.getMessage());
        } catch (Exception e) {
            // 捕获其他任何异常并抛出
            throw new RuntimeException("查询执行失败，发生错误: " + e.getMessage());
        }
    }

    // 异步执行查询的方法
    public CompletableFuture<List<CypherQueryResult>> executeCustomQueryAsync(String cypherQuery) {
        return CompletableFuture.supplyAsync(() -> {
            return executeCustomQuery(cypherQuery);
        });
    }
}
