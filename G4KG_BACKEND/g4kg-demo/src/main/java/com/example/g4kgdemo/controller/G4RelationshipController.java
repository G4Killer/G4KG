package com.example.g4kgdemo.controller;

import com.example.g4kgdemo.dto.G4RelationshipSearchResult;
import com.example.g4kgdemo.dto.PaginatedResult;
import com.example.g4kgdemo.service.G4RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/g4-relationship")
public class G4RelationshipController {

    private final G4RelationshipService g4RelationshipService;

    // 每页固定显示 10 条记录
    private static final int PAGE_SIZE = 10;

    @Autowired
    public G4RelationshipController(G4RelationshipService g4RelationshipService) {
        this.g4RelationshipService = g4RelationshipService;
    }

    // 新增 /properties 接口
    @GetMapping("/properties")
    public ResponseEntity<Map<String, String>> getNodeProperties(@RequestParam String nodeType) {
        try {
            // 调用 service 层方法获取 nodeIdProperty 和 nodeNameProperty
            Map<String, String> properties = g4RelationshipService.getNodeProperties(nodeType);
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            // 如果 nodeType 无效，返回错误信息
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            // 捕获其他未知异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unexpected error occurred."
            ));
        }
    }

    // 分页查询接口，返回格式化后的分页结构
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchG4Relationships(
            @RequestParam(required = false) String nodeType,
            @RequestParam(required = false) String nodeIdProperty,
            @RequestParam(required = false) String nodeNameProperty,
            @RequestParam(required = false) String nodeId,
            @RequestParam(required = false) String nodeName,
            @RequestParam(required = false) Integer threshold,
            @RequestParam(required = false, defaultValue = "1") Integer hops,
            @RequestParam(required = false, defaultValue = "1")int page) { // 用户传入页码

        // 处理页码错误，确保页码从 1 开始
        if (page < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Page number must be >= 1",
                    "currentPage", page,
                    "content", List.of()
            ));
        }

        try {
            // 如果 nodeType 为空，返回错误
            if (nodeType == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "error", "NodeType is required",
                        "currentPage", page,
                        "content", List.of()
                ));
            }

            // 如果 nodeId 和 nodeName 都为空，直接返回错误
            if (nodeId == null && nodeName == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                        "error", "Either nodeId or nodeName must be provided",
                        "currentPage", page,
                        "content", List.of()
                ));
            }

            // 验证节点是否存在
            if (!g4RelationshipService.validateNodeExistence(nodeType, nodeId, nodeName)) {
                return ResponseEntity.ok(Map.of(
                        "currentPage", page,
                        "content", List.of()
                ));
            }

            // 计算 skip，从 0 开始
            int skip = (page - 1) * PAGE_SIZE;

            // 如果 nodeIdProperty 或 nodeNameProperty 为 null，从 getNodeProperties 获取默认值
            Map<String, String> properties = g4RelationshipService.getNodeProperties(nodeType);
            if (nodeIdProperty == null) {
                nodeIdProperty = properties.get("nodeIdProperty");
            }
            if (nodeNameProperty == null) {
                nodeNameProperty = properties.get("nodeNameProperty");
            }

            // 调用 Service 层的分页查询方法
            PaginatedResult<G4RelationshipSearchResult> result = g4RelationshipService.findRelatedG4WithPathAndRelations(
                    nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName, threshold, hops, skip, PAGE_SIZE
            );

            // 格式化输出结果
            List<Map<String, Object>> formattedResults = result.getContent().stream()
                    .map(r -> Map.of(
                            "relationNames", r.getRelationNames(),
                            "formattedPathDetails", r.getFormattedPathDetails(),
                            "formattedG4Details", r.getFormattedG4Details()
                    ))
                    .collect(Collectors.toList());

            // 构造分页返回结构
            Map<String, Object> response = Map.of(
                    "currentPage", page,
                    "content", formattedResults
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 处理非法参数异常
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", e.getMessage(),
                    "currentPage", page,
                    "content", List.of()
            ));
        } catch (RuntimeException e) {
            // 捕获数据库等运行时异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unexpected error occurred. Please try again later.",
                    "currentPage", page,
                    "content", List.of()
            ));
        } catch (Exception e) {
            // 捕获其他未知异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unexpected error occurred.",
                    "currentPage", page,
                    "content", List.of()
            ));
        }
    }
}
