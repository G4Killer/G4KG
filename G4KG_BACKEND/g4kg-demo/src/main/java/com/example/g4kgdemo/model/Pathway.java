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
@Node("Pathway")
public class Pathway {
    @Id @GeneratedValue
    private Long id;

    @Property("PathwayName")
    private String pathwayName;

    @Property("PathwayId")
    private String pathwayId;

    @Relationship(type = "GeneToPathway", direction = Direction.INCOMING)
    private List<GeneToPathway> geneToPathways = new ArrayList<>();

    @Relationship(type = "ProteinToPathway", direction = Direction.INCOMING)
    private List<ProteinToPathway> proteinToPathways = new ArrayList<>();

    @Relationship(type = "PathwayToPathway", direction = Direction.OUTGOING)
    private List<PathwayToPathway> pathwayToPathways = new ArrayList<>();

    // 构造函数、getter和setter方法
    public Pathway() {}

    public Pathway(String pathwayName, String pathwayId) {
        this.pathwayName = pathwayName;
        this.pathwayId = pathwayId;
    }

}