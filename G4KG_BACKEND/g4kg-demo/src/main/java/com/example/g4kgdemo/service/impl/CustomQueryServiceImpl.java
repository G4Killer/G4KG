package com.example.g4kgdemo.service.impl;

import com.example.g4kgdemo.repository.diy.CustomQueryRepository;
import com.example.g4kgdemo.repository.diy.CypherQueryResult;
import com.example.g4kgdemo.service.CustomQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CustomQueryServiceImpl implements CustomQueryService {

    @Autowired
    private CustomQueryRepository customQueryRepository;

    @Override
    public List<CypherQueryResult> executeCustomQuery(String cypherQuery) {
        try {
            // 验证查询语句安全性
            customQueryRepository.validateQuery(cypherQuery);

            // 调用 repository 执行查询
            return customQueryRepository.executeCustomQuery(cypherQuery);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Query Validation Failure: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while executing the query: " + e.getMessage(), e);
        }
    }

    @Override
    public List<CypherQueryResult> executeCustomQueryAsync(String cypherQuery) {
        try {
            // 验证查询语句安全性
            customQueryRepository.validateQuery(cypherQuery);

            // 异步执行查询
            CompletableFuture<List<CypherQueryResult>> futureResult = CompletableFuture.supplyAsync(() -> {
                return customQueryRepository.executeCustomQuery(cypherQuery);
            });

            // 返回查询结果，阻塞线程直到结果返回
            return futureResult.join();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Query Validation Failure: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while executing the query asynchronously: " + e.getMessage(), e);
        }
    }
}
