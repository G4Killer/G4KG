package com.example.g4kgdemo.controller;

import com.example.g4kgdemo.service.NodeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/node-details")
public class NodeDetailsController {

    private final NodeDetailsService nodeDetailsService;

    @Autowired
    public NodeDetailsController(NodeDetailsService nodeDetailsService) {
        this.nodeDetailsService = nodeDetailsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNodeDetails(
            @RequestParam String label,
            @RequestParam String idProperty,
            @RequestParam String idValue) {
        try {
            Map<String, Object> nodeDetails = nodeDetailsService.getNodeDetailsById(label, idProperty, idValue);

            if (nodeDetails == null || nodeDetails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Node details not found"));
            }

            return ResponseEntity.ok(nodeDetails);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred"));
        }
    }
}


