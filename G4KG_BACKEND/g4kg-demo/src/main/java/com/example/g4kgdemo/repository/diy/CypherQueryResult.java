package com.example.g4kgdemo.repository.diy;

import java.util.Map;

public class CypherQueryResult {

    private Map<String, Object> result;

    public CypherQueryResult() {
        // 默认构造器
    }

    public CypherQueryResult(Map<String, Object> result) {
        this.result = result;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
}
