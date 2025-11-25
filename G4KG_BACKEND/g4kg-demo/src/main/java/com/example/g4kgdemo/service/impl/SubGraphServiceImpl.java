package com.example.g4kgdemo.service.impl;

import com.example.g4kgdemo.dto.SubGraphResult;
import com.example.g4kgdemo.repository.visualization.GetSubGraphRepository;
import com.example.g4kgdemo.service.SubGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubGraphServiceImpl implements SubGraphService {

    @Autowired
    private GetSubGraphRepository getSubGraphRepository;

    private static final Map<String, String> NODE_PROPERTIES = new HashMap<>();

    static {
        NODE_PROPERTIES.put("Gene", "GeneId");
        NODE_PROPERTIES.put("Disease", "DiseaseId");
        NODE_PROPERTIES.put("Protein", "ProteinId");
        NODE_PROPERTIES.put("GO", "GoId");
        NODE_PROPERTIES.put("Pathway", "PathwayId");
        NODE_PROPERTIES.put("Drug", "DrugId");
        NODE_PROPERTIES.put("G4", "G4Id");
    }

    @Override
    public SubGraphResult getSubGraph(String nodeType, String nodeIdProperty, String nodeId, int depth, int limit, boolean includeIsolated) {
        // 参数验证
        if (depth < 1 || limit < 1) {
            throw new IllegalArgumentException("Depth and limit must be greater than 0.");
        }
        if (nodeType == null || nodeType.trim().isEmpty()) {
            throw new IllegalArgumentException("Node type cannot be null or empty.");
        }
        if (nodeId == null || nodeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Node ID cannot be null or empty.");
        }
        return getSubGraphRepository.findSubGraphByNode(nodeType, nodeIdProperty, nodeId, depth, limit, includeIsolated);
    }

    @Override
    public Map<String, String> getGraphNodeProperties(String nodeType) {
        if (nodeType == null || !NODE_PROPERTIES.containsKey(nodeType)) {
            throw new IllegalArgumentException("Invalid node type: " + nodeType);
        }
        return Map.of("nodeIdProperty", NODE_PROPERTIES.get(nodeType));
    }
}
