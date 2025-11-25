package com.example.g4kgdemo.dto;

import lombok.Data;

@Data
public class GOFunctionDistributionResult {
    private String goId;       // GO节点ID
    private String goName;     // GO节点名称
    private Long g4Count;      // 关联的G4计数

    public GOFunctionDistributionResult(String goId, String goName, Long g4Count) {
        this.goId = goId;
        this.goName = goName;
        this.g4Count = g4Count;
    }
}
