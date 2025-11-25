package com.example.g4kgdemo.service;

import java.util.Map;

public interface NodeDetailsService {

    /**
     * 根据节点的类型和唯一标识属性值查询节点的详细信息
     *
     * @param label      节点类型（例如 GO, Gene）
     * @param idProperty 唯一标识属性（例如 GoId, GeneId）
     * @param idValue    唯一标识值（例如 12, HG4_2963）
     * @return 节点的详细属性信息
     */
    Map<String, Object> getNodeDetailsById(String label, String idProperty, String idValue);
}
