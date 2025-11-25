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
public class GeneToDisease {
    @RelationshipId
    private Long id;

    @Property("RelationName")
    private String relationName;

    @Property("DSI")
    private String DSI;

    @Property("DPI")
    private String DPI;

    @TargetNode
    private Disease disease;

    // 构造函数、getter和setter方法
    public GeneToDisease() {}

    public GeneToDisease(String relationName, String DSI, String DPI) {
        this.relationName = relationName;
        this.DSI = DSI;
        this.DPI = DPI;
    }

}