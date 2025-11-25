package com.example.g4kgdemo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Node("GO")
public class GO {
    @Id @GeneratedValue
    private Long id;

    @Property("GoTermName")
    private String goTermName;

    @Property("GoTermType")
    private String goTermType;

    @Property("GoId")
    private String goId;

    @Relationship(type = "GeneToGo", direction = Direction.INCOMING)
    private List<GeneToGO> geneToGos = new ArrayList<>();

    @Relationship(type = "ProteinToGO", direction = Direction.INCOMING)
    private List<ProteinToGO> proteinToGos = new ArrayList<>();

    @Relationship(type = "GOToGO", direction = Direction.OUTGOING)
    private List<GOToGO> goToGos = new ArrayList<>();

    // 构造函数、getter和setter方法
    public GO() {}

    public GO(String goTermName, String goTermType, String goId) {  // 修改构造函数
        this.goTermName = goTermName;
        this.goTermType = goTermType;
        this.goId = goId;
    }
}
