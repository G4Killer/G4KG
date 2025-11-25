package com.example.g4kgdemo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FunctionEnrichmentResult {
    private String g4Id;          // G4节点ID
    private String geneId;        // 基因ID
    private String geneSymbol;    // 基因符号
    private String goId;          // GO节点ID
    private String goName;        // GO节点名称
    private String pathwayId;     // Pathway节点ID
    private String pathwayName;   // Pathway节点名称

    // 带参数的构造函数，用于确保所有字段都映射正确
    public FunctionEnrichmentResult(String g4Id, String geneId, String geneSymbol, String goId, String goName, String pathwayId, String pathwayName) {
        this.g4Id = g4Id;
        this.geneId = geneId;
        this.geneSymbol = geneSymbol;
        this.goId = goId;
        this.goName = goName;
        this.pathwayId = pathwayId;
        this.pathwayName = pathwayName;
    }
}
