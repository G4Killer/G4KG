package com.example.g4kgdemo.service;

import com.example.g4kgdemo.repository.diy.CypherQueryResult;

import java.util.List;

public interface CustomQueryService {

    /**
     * 执行自定义查询语句
     *
     * @param cypherQuery Cypher 查询语句
     * @return 查询结果
     */
    List<CypherQueryResult> executeCustomQuery(String cypherQuery);

    /**
     * 异步执行自定义查询语句
     *
     * @param cypherQuery Cypher 查询语句
     * @return 异步查询结果
     */
    List<CypherQueryResult> executeCustomQueryAsync(String cypherQuery);
}
