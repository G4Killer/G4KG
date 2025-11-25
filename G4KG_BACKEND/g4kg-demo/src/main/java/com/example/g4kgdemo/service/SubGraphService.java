package com.example.g4kgdemo.service;

import com.example.g4kgdemo.dto.SubGraphResult;

import java.util.Map;

public interface SubGraphService {

    SubGraphResult getSubGraph(String nodeType, String nodeIdProperty, String nodeId, int depth, int limit, boolean includeIsolated);

    /**
     * 获取节点类型对应的 ID 属性
     */
    Map<String, String> getGraphNodeProperties(String nodeType);
}
