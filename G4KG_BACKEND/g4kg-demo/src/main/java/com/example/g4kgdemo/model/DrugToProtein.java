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
public class DrugToProtein {
    @RelationshipId
    private Long id;

    @Property("RelationName")
    private String relationName;

    @Property("Action")
    private String action;

    @TargetNode
    private Protein protein;

    // 构造函数、getter和setter方法
    public DrugToProtein() {}

    public DrugToProtein(String relationName, String action) {
        this.relationName = relationName;
        this.action = action;
    }

}