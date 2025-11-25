package com.example.g4kgdemo.dto;

import lombok.Data;

@Data
public class PathwayDistributionResult {
    private String pathwayId;    // Pathway节点ID
    private String pathwayName;  // Pathway节点名称
    private Long g4Count;        // 关联的G4计数

    public PathwayDistributionResult(String pathwayId, String pathwayName, Long g4Count) {
        this.pathwayId = pathwayId;
        this.pathwayName = pathwayName;
        this.g4Count = g4Count;
    }
}
