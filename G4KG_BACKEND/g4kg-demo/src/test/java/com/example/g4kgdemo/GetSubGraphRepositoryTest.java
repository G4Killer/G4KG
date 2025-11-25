package com.example.g4kgdemo.repository.visualization;

import com.example.g4kgdemo.dto.SubGraphResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GetSubGraphRepositoryTest {

    @Autowired
    private GetSubGraphRepository repository;

    @Test
    public void testFindSubGraphByNode() {
        String nodeType = "G4";
        String nodeIdProperty = "G4Id";
        String nodeId = "HG4_4435";
        int depth = 2;
        int limit = 50;
        boolean includeIsolated = false;

        SubGraphResult result = repository.findSubGraphByNode(nodeType, nodeIdProperty, nodeId, depth, limit, includeIsolated);

        // 打印结果
        System.out.println("Test Result - Nodes: " + result.getNodes());
        System.out.println("Test Result - Relationships: " + result.getRelationships());

        assertNotNull(result, "Result should not be null");
        assertFalse(result.getNodes().isEmpty(), "Nodes should not be empty");
        assertFalse(result.getRelationships().isEmpty(), "Relationships should not be empty");
    }

}


