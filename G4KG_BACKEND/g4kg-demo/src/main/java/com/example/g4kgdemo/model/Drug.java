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
@Node("Drug")
public class Drug {
    @Id @GeneratedValue
    private Long id;

    @Property("Name")
    private String name;

    @Property("Groups")
    private String groups;

    @Property("DrugId")
    private String drugId;

    @Property("Description")
    private String description;

    @Relationship(type = "DrugToProtein", direction = Direction.OUTGOING)
    private List<DrugToProtein> drugToProteins = new ArrayList<>();

    @Relationship(type = "DrugToDrug", direction = Direction.OUTGOING)
    private List<DrugToDrug> drugToDrugs = new ArrayList<>();

    @Relationship(type = "DrugToDisease", direction = Direction.OUTGOING)
    private List<DrugToDisease> drugToDiseases = new ArrayList<>();

    // 构造函数、getter和setter方法
    public Drug() {}

    public Drug(String name, String groups, String drugId, String description) {
        this.name = name;
        this.groups = groups;
        this.drugId = drugId;
        this.description = description;
    }

}