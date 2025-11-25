package com.example.g4kgdemo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Setter
@Getter
@RelationshipProperties
public class ProteinToGene {
    @RelationshipId
    private Long id;

    @Property("RelationName")
    private String relationName;

    @Property("Tissue")
    private String tissue;

    @TargetNode
    private Gene gene;

    // 构造函数、getter和setter方法
    public ProteinToGene() {}

    public ProteinToGene(String relationName, String tissue) {
        this.relationName = relationName;
        this.tissue = tissue;
    }

}