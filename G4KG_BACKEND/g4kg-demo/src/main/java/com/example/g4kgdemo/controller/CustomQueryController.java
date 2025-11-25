package com.example.g4kgdemo.controller;

import com.example.g4kgdemo.repository.diy.CypherQueryResult;
import com.example.g4kgdemo.service.CustomQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/custom-query")
public class CustomQueryController {

    @Autowired
    private CustomQueryService customQueryService;

    // 1. 同步查询接口
    @PostMapping("/execute")
    public ResponseEntity<?> executeCustomQuery(@RequestBody Map<String, String> request) {
        try {
            // 获取查询语句
            String cypherQuery = request.get("query");

            if (cypherQuery == null || cypherQuery.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Request parameters must contain 'query' and must not be null");
            }

            // 调用 Service 执行查询
            List<CypherQueryResult> results = customQueryService.executeCustomQuery(cypherQuery);

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Illegal search: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An internal error occurred while executing the query: " + e.getMessage());
        }
    }

    // 2. 异步查询接口
    @PostMapping("/execute-async")
    public ResponseEntity<?> executeCustomQueryAsync(@RequestBody Map<String, String> request) {
        try {
            // 获取查询语句
            String cypherQuery = request.get("query");

            if (cypherQuery == null || cypherQuery.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Request parameters must contain 'query' and must not be null");
            }

            // 调用 Service 异步执行查询
            List<CypherQueryResult> results = customQueryService.executeCustomQueryAsync(cypherQuery);

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Illegal search: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error while executing a query asynchronously: " + e.getMessage());
        }
    }
}
