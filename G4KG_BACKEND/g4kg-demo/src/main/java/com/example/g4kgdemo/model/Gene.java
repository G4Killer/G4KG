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
@Node("Gene")
public class Gene {
    @Id @GeneratedValue
    private Long id;

    @Property("Description")
    private String description;

    @Property("GeneSymbol")
    private String geneSymbol;

    @Property("FullName")
    private String fullName;

    @Property("Synonyms")
    private String synonyms;

    @Property("GeneType")
    private String geneType;

    @Property("Chr")
    private String chr;

    @Property("Location")
    private String location;

    @Property("GeneId")
    private String geneId;

    @Relationship(type = "GeneToProtein", direction = Direction.OUTGOING)
    private List<GeneToProtein> geneToProteins = new ArrayList<>();

    @Relationship(type = "GeneToGO", direction = Direction.OUTGOING)
    private List<GeneToGO> geneToGos = new ArrayList<>();

    @Relationship(type = "GeneToDisease", direction = Direction.OUTGOING)
    private List<GeneToDisease> geneToDiseases = new ArrayList<>();

    @Relationship(type = "GeneToPathway", direction = Direction.OUTGOING)
    private List<GeneToPathway> geneToPathways = new ArrayList<>();

    @Relationship(type = "GeneToGene", direction = Direction.OUTGOING)
    private List<GeneToGene> geneToGenes = new ArrayList<>();

    @Relationship(type = "G4ToGene", direction = Direction.INCOMING)
    private List<G4ToGene> g4ToGenes = new ArrayList<>();

    // 构造函数、getter和setter方法
    public Gene() {}

    public Gene(String description, String geneSymbol, String fullName, String synonyms, String geneType, String chr, String location, String geneId) {
        this.description = description;
        this.geneSymbol = geneSymbol;
        this.fullName = fullName;
        this.synonyms = synonyms;
        this.geneType = geneType;
        this.chr = chr;
        this.location = location;
        this.geneId = geneId;
    }

}