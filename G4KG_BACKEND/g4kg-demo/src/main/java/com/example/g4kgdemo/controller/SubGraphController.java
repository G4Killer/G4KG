package com.example.g4kgdemo.controller;

import com.example.g4kgdemo.dto.SubGraphResult;
import com.example.g4kgdemo.service.SubGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subgraph")
public class SubGraphController {

    private final SubGraphService subGraphService;

    @Autowired
    public SubGraphController(SubGraphService subGraphService) {
        this.subGraphService = subGraphService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getSubGraph(
            @RequestParam String nodeType,
            @RequestParam(required = false) String nodeIdProperty,
            @RequestParam String nodeId,
            @RequestParam int depth,
            @RequestParam int limit,
            @RequestParam(required = false, defaultValue = "false") boolean includeIsolated) {

        try {
            // 自动填充 nodeIdProperty
            if (nodeIdProperty == null) {
                Map<String, String> properties = subGraphService.getGraphNodeProperties(nodeType);
                nodeIdProperty = properties.get("nodeIdProperty");
            }

            SubGraphResult result = subGraphService.getSubGraph(nodeType, nodeIdProperty, nodeId, depth, limit, includeIsolated);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            // 返回明确的错误信息 JSON
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred.");
        }
    }

    @GetMapping("/properties")
    public ResponseEntity<?> getGraphNodeProperties(@RequestParam String nodeType) {
        try {
            Map<String, String> properties = subGraphService.getGraphNodeProperties(nodeType);
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
