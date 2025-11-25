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
@Node("Disease")
public class Disease {
    @Id @GeneratedValue
    private Long id;

    @Property("DiseaseName")
    private String diseaseName;

    @Property("DiseaseSemanticType")
    private String diseaseSemanticType;

    @Property("DiseaseId")
    private String diseaseId;

    @Property("Description")
    private String description;

    @Property("Synonyms")
    private String synonyms;

    @Relationship(type = "DrugToDisease", direction = Direction.INCOMING)
    private List<DrugToDisease> drugToDiseases = new ArrayList<>();

    @Relationship(type = "GeneToDisease", direction = Direction.INCOMING)
    private List<GeneToDisease> geneToDiseases = new ArrayList<>();

    @Relationship(type = "G4ToDisease", direction = Direction.INCOMING)
    private List<G4ToDisease> g4ToDiseases = new ArrayList<>();

    // 构造函数、getter和setter方法
    public Disease() {}

    public Disease(String diseaseName, String diseaseSemanticType, String diseaseId, String description, String synonyms) {
        this.diseaseName = diseaseName;
        this.diseaseSemanticType = diseaseSemanticType;
        this.diseaseId = diseaseId;
        this.description = description;
        this.synonyms = synonyms;
    }

}