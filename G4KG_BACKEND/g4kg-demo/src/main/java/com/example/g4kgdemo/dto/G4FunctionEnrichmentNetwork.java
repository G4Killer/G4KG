package com.example.g4kgdemo.dto;

import java.util.ArrayList;
import java.util.List;

public class G4FunctionEnrichmentNetwork {
    private String g4Id;
    private List<SubGraphNode> g4;
    private List<SubGraphNode> genes;
    private List<SubGraphNode> goTerms;
    private List<SubGraphNode> pathways;
    private List<SubGraphRelation> geneRelations;   // Gene relationships
    private List<SubGraphRelation> goRelations;      // GO relationships
    private List<SubGraphRelation> pathwayRelations; // Pathway relationships

    // Getters and setters
    public String getG4Id() {
        return g4Id;
    }

    public void setG4Id(String g4Id) {
        this.g4Id = g4Id;
    }

    public List<SubGraphNode> getG4() {
        return g4;  // 返回 g4 字段
    }

    public void setG4(List<SubGraphNode> g4) {
        this.g4 = g4;  // 设置 g4 字段
    }

    public List<SubGraphNode> getGenes() {
        return genes;
    }

    public void setGenes(List<SubGraphNode> genes) {
        this.genes = genes;
    }

    public List<SubGraphNode> getGoTerms() {
        return goTerms;
    }

    public void setGoTerms(List<SubGraphNode> goTerms) {
        this.goTerms = goTerms;
    }

    public List<SubGraphNode> getPathways() {
        return pathways;
    }

    public void setPathways(List<SubGraphNode> pathways) {
        this.pathways = pathways;
    }

    public List<SubGraphRelation> getGeneRelations() {
        return geneRelations;
    }

    public void setGeneRelations(List<SubGraphRelation> geneRelations) {
        this.geneRelations = geneRelations;
    }

    public List<SubGraphRelation> getGoRelations() {
        return goRelations;
    }

    public void setGoRelations(List<SubGraphRelation> goRelations) {
        this.goRelations = goRelations;
    }

    public List<SubGraphRelation> getPathwayRelations() {
        return pathwayRelations;
    }

    public void setPathwayRelations(List<SubGraphRelation> pathwayRelations) {
        this.pathwayRelations = pathwayRelations;
    }

    // 新增 getNodes() 方法，返回所有节点
    public List<SubGraphNode> getNodes() {
        List<SubGraphNode> allNodes = new ArrayList<>();
        allNodes.addAll(g4 != null ? g4 : new ArrayList<>());         // 将 g4 节点添加到列表
        allNodes.addAll(genes != null ? genes : new ArrayList<>());      // 将 genes 节点添加到列表
        allNodes.addAll(goTerms != null ? goTerms : new ArrayList<>());    // 将 goTerms 节点添加到列表
        allNodes.addAll(pathways != null ? pathways : new ArrayList<>());   // 将 pathways 节点添加到列表
        return allNodes;
    }

    // 新增 getRelationships() 方法，返回所有关系
    public List<SubGraphRelation> getRelationships() {
        List<SubGraphRelation> allRelations = new ArrayList<>();
        allRelations.addAll(geneRelations != null ? geneRelations : new ArrayList<>());      // 将 geneRelations 添加到列表
        allRelations.addAll(goRelations != null ? goRelations : new ArrayList<>());        // 将 goRelations 添加到列表
        allRelations.addAll(pathwayRelations != null ? pathwayRelations : new ArrayList<>());   // 将 pathwayRelations 添加到列表
        return allRelations;
    }
}
