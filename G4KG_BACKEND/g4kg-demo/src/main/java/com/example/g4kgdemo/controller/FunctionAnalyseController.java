package com.example.g4kgdemo.controller;

import com.example.g4kgdemo.dto.*;
import com.example.g4kgdemo.service.FunctionAnalyseService;
import com.example.g4kgdemo.service.G4RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/function-analyse")
public class FunctionAnalyseController {

    private final FunctionAnalyseService functionAnalyseService;

    @Autowired
    private G4RelationshipService g4RelationshipService;

    @Autowired
    public FunctionAnalyseController(FunctionAnalyseService functionAnalyseService) {
        this.functionAnalyseService = functionAnalyseService;
    }

    // 1. 分析 GO 功能分布
    @PostMapping("/analyze-go")
    public ResponseEntity<?> analyzeGOFunctionDistribution(@RequestBody Map<String, Object> request) {
        try {
            // 验证是否包含 useRelationship 参数
            if (!request.containsKey("useRelationship")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing required parameter: useRelationship.");
            }

            boolean useRelationship = (boolean) request.get("useRelationship");

            if (useRelationship) {
                // 验证关系查询模式下的必要参数
                if (!request.containsKey("nodeType") || (!request.containsKey("nodeId") && !request.containsKey("nodeName"))) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Missing required parameters: nodeType and either nodeId or nodeName for relationship-based search.");
                }

                String nodeType = (String) request.get("nodeType");
                String nodeId = (String) request.get("nodeId");
                String nodeName = (String) request.get("nodeName");

                // 验证节点是否存在
                boolean nodeExists = g4RelationshipService.validateNodeExistence(nodeType, nodeId, nodeName);
                if (!nodeExists) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT)
                            .body("Node does not exist: " + (nodeId != null ? nodeId : nodeName));
                }
            } else {
                // 验证属性查询模式下的必要参数
                if (!request.containsKey("g4Id") && !request.containsKey("sampleName")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Missing required parameters: g4Id or sampleName for attribute-based search.");
                }
            }

            // 获取 G4 集合的限制数量
            int g4Limit = (int) request.getOrDefault("g4Limit", 100);

            // 动态获取 G4 ID 集合
            List<String> g4Ids = useRelationship
                    ? functionAnalyseService.getG4Ids(
                    (String) request.get("nodeType"), null, null,
                    (String) request.get("nodeId"), (String) request.get("nodeName"), null, null, null,
                    null, null, null, (Integer) request.getOrDefault("threshold", 1),
                    (int) request.getOrDefault("hops", 1), 0, g4Limit, true)
                    : functionAnalyseService.getG4Ids(
                    null, null, null, null, null,
                    (String) request.get("g4Id"), (String) request.get("chr"),
                    (String) request.get("location"), (String) request.get("sampleName"),
                    (String) request.get("confidenceLevel"), (String) request.get("score"),
                    null, 0, 0, g4Limit, false);

            // 验证 G4 ID 集合
            if (g4Ids.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No G4 IDs found for the given criteria.");
            }

            // 分析结果的限制数量
            int resultLimit = (int) request.getOrDefault("resultLimit", 10);

            // 调用分析方法
            List<GOFunctionDistributionResult> results =
                    functionAnalyseService.analyzeGOFunctionDistribution(g4Ids, resultLimit);

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }



    // 2. 分析 Pathway 功能分布
    @PostMapping("/analyze-pathway")
    public ResponseEntity<?> analyzePathwayDistribution(@RequestBody Map<String, Object> request) {
        try {
            // 验证是否包含 useRelationship 参数
            if (!request.containsKey("useRelationship")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing required parameter: useRelationship.");
            }

            boolean useRelationship = (boolean) request.get("useRelationship");

            if (useRelationship) {
                // 验证关系查询模式下的必要参数
                if (!request.containsKey("nodeType") || (!request.containsKey("nodeId") && !request.containsKey("nodeName"))) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Missing required parameters: nodeType and either nodeId or nodeName for relationship-based search.");
                }

                String nodeType = (String) request.get("nodeType");
                String nodeId = (String) request.get("nodeId");
                String nodeName = (String) request.get("nodeName");

                // 验证节点是否存在
                boolean nodeExists = g4RelationshipService.validateNodeExistence(nodeType, nodeId, nodeName);
                if (!nodeExists) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT)
                            .body("Node does not exist: " + (nodeId != null ? nodeId : nodeName));
                }
            } else {
                // 验证属性查询模式下的必要参数
                if (!request.containsKey("g4Id") && !request.containsKey("sampleName")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Missing required parameters: g4Id or sampleName for attribute-based search.");
                }
            }

            // 获取 G4 集合的限制数量
            int g4Limit = (int) request.getOrDefault("g4Limit", 100);

            // 动态获取 G4 ID 集合
            List<String> g4Ids = useRelationship
                    ? functionAnalyseService.getG4Ids(
                    (String) request.get("nodeType"),
                    null, null,
                    (String) request.get("nodeId"),
                    (String) request.get("nodeName"),
                    null, null, null, null, null, null,
                    (Integer) request.getOrDefault("threshold", 1),
                    (int) request.getOrDefault("hops", 1),
                    0, g4Limit, true)
                    : functionAnalyseService.getG4Ids(
                    null, null, null, null, null,
                    (String) request.get("g4Id"),
                    (String) request.get("chr"),
                    (String) request.get("location"),
                    (String) request.get("sampleName"),
                    (String) request.get("confidenceLevel"),
                    (String) request.get("score"),
                    null, 0, 0, g4Limit, false);

            // 验证 G4 ID 集合
            if (g4Ids.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No G4 IDs found for the given criteria.");
            }

            // 分析结果的限制数量
            int resultLimit = (int) request.getOrDefault("resultLimit", 10);

            // 调用 Service 进行 Pathway 分析
            List<PathwayDistributionResult> results =
                    functionAnalyseService.analyzePathwayDistribution(g4Ids, resultLimit);

            return ResponseEntity.ok(results);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }


    // 3. G4功能富集网络构建
    @PostMapping("/build-enrichment-network")
    public ResponseEntity<?> buildG4FunctionEnrichmentNetwork(@RequestBody Map<String, Object> request) {
        try {
            // 验证是否包含 useRelationship 参数
            if (!request.containsKey("useRelationship")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Missing required parameter: useRelationship.");
            }

            boolean useRelationship = (boolean) request.get("useRelationship");

            if (useRelationship) {
                // 验证关系查询模式下的必要参数
                if (!request.containsKey("nodeType") || (!request.containsKey("nodeId") && !request.containsKey("nodeName"))) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Missing required parameters: nodeType and either nodeId or nodeName for relationship-based search.");
                }

                String nodeType = (String) request.get("nodeType");
                String nodeId = (String) request.get("nodeId");
                String nodeName = (String) request.get("nodeName");

                // 验证节点是否存在
                boolean nodeExists = g4RelationshipService.validateNodeExistence(nodeType, nodeId, nodeName);
                if (!nodeExists) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT)
                            .body("Node does not exist: " + (nodeId != null ? nodeId : nodeName));
                }
            } else {
                // 验证属性查询模式下的必要参数
                if (!request.containsKey("g4Id") && !request.containsKey("sampleName")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Missing required parameters: g4Id or sampleName for attribute-based search.");
                }
            }

            // 获取 G4 集合的限制数量
            int g4Limit = (int) request.getOrDefault("g4Limit", 100);

            // 动态获取 G4 ID 集合
            List<String> g4Ids = useRelationship
                    ? functionAnalyseService.getG4Ids(
                    (String) request.get("nodeType"),
                    null, null,
                    (String) request.get("nodeId"),
                    (String) request.get("nodeName"),
                    null, null, null, null, null, null,
                    (Integer) request.getOrDefault("threshold", 1),
                    (int) request.getOrDefault("hops", 1),
                    0, g4Limit, true)
                    : functionAnalyseService.getG4Ids(
                    null, null, null, null, null,
                    (String) request.get("g4Id"),
                    (String) request.get("chr"),
                    (String) request.get("location"),
                    (String) request.get("sampleName"),
                    (String) request.get("confidenceLevel"),
                    (String) request.get("score"),
                    null, 0, 0, g4Limit, false);

            // 验证 G4 ID 集合
            if (g4Ids.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No G4 IDs found for the given criteria.");
            }



            // 调用 Service 构建功能富集分析网络
            G4FunctionEnrichmentNetwork network =
                    functionAnalyseService.buildG4FunctionEnrichmentNetwork(g4Ids);

            // 只返回 nodes 和 relationships 数据
            Map<String, Object> response = new HashMap<>();
            response.put("nodes", network.getNodes());
            response.put("relationships", network.getRelationships());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }


    // 4. 获取 G4 ID 集合
    @GetMapping("/get-g4-ids")
    public ResponseEntity<?> getG4Ids(
            @RequestParam boolean useRelationship,
            @RequestParam(required = false) String g4Id,
            @RequestParam(required = false) String chr,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sampleName,
            @RequestParam(required = false) String confidenceLevel,
            @RequestParam(required = false) String score,
            @RequestParam(required = false) String nodeType,
            @RequestParam(required = false) String nodeIdProperty,
            @RequestParam(required = false) String nodeNameProperty,
            @RequestParam(required = false) String nodeId,
            @RequestParam(required = false) String nodeName,
            @RequestParam(required = false) Integer threshold,
            @RequestParam(required = false, defaultValue = "1") int hops,
            @RequestParam(required = false, defaultValue = "100") int limit) {
        try {
            List<String> g4Ids;
            if (useRelationship) {
                // 关系查询方式
                g4Ids = functionAnalyseService.getG4Ids(
                        nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName,
                        null, null, null, null, null, null, threshold, hops, 0, limit, true);
            } else {
                // 属性查询方式
                g4Ids = functionAnalyseService.getG4Ids(
                        null, null, null, null, null,
                        g4Id, chr, location, sampleName, confidenceLevel, score,
                        null, 0, 0, limit, false);
            }

            if (g4Ids.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No G4 IDs found for the given criteria.");
            }

            return ResponseEntity.ok(g4Ids);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
