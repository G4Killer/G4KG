package com.example.g4kgdemo.service.impl;

import com.example.g4kgdemo.service.NodeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NodeDetailsServiceImpl implements NodeDetailsService {

    private final Neo4jClient neo4jClient;

    @Autowired
    public NodeDetailsServiceImpl(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @Override
    public Map<String, Object> getNodeDetailsById(String label, String idProperty, String idValue) {
        String cypherQuery = String.format(
                "MATCH (n:%s) WHERE n.%s = '%s' RETURN properties(n) AS properties",
                label, idProperty, idValue
        );

        return neo4jClient.query(cypherQuery)
                .fetchAs(Map.class)
                .mappedBy((typeSystem, record) -> record.get("properties").asMap())
                .one()
                .orElse(null);
    }
}
