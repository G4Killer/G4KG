package com.example.g4kgdemo.repository.diy;

import java.util.List;

public interface CustomQueryRepository {

    List<CypherQueryResult> executeCustomQuery(String cypherQuery);

    default void validateQuery(String cypherQuery) {
        String query = cypherQuery.toLowerCase();
        // 检查是否包含危险的写操作
        if (query.contains("create") || query.contains("delete") || query.contains("set") ||
                query.contains("merge") || query.contains("remove") || query.contains("detach")) {
            throw new IllegalArgumentException("Only read-only queries are allowed, write operations are not permitted");
        }

        // 可以扩展更多的安全检查，比如检查是否包含其他敏感关键词等
    }
}
