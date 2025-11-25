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
public class GOToGO {
    @RelationshipId
    private Long id;

    @Property("RelationName")
    private String relationName;

    @TargetNode
    private GO go;

    // 构造函数、getter和setter方法
    public GOToGO() {}

    public GOToGO(String relationName) {
        this.relationName = relationName;
    }

}