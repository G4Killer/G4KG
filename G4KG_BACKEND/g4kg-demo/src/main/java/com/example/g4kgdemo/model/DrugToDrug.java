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
public class DrugToDrug {
    @RelationshipId
    private Long id;

    @Property("Description")
    private String description;

    @Property("RelationName")
    private String relationName;

    @TargetNode
    private Drug drug;

    // 构造函数、getter和setter方法
    public DrugToDrug() {}

    public DrugToDrug(String description, String relationName) {
        this.description = description;
        this.relationName = relationName;
    }

}