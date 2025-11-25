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
@Node("Protein")
public class Protein {
    @Id @GeneratedValue
    private Long id;

    @Property("FullName")
    private String fullName;

    @Property("EntryName")
    private String entryName;

    @Property("ProteinId")
    private String proteinId;

    @Relationship(type = "DrugToProtein", direction = Direction.INCOMING)
    private List<DrugToProtein> drugToProteins = new ArrayList<>();

    @Relationship(type = "GeneToProtein", direction = Direction.INCOMING)
    private List<GeneToProtein> geneToProteins = new ArrayList<>();

    @Relationship(type = "ProteinToPathway", direction = Direction.OUTGOING)
    private List<ProteinToPathway> proteinToPathways = new ArrayList<>();

    @Relationship(type = "ProteinToGO", direction = Direction.OUTGOING)
    private List<ProteinToGO> proteinToGos = new ArrayList<>();

    @Relationship(type = "ProteinToProtein", direction = Direction.OUTGOING)
    private List<ProteinToProtein> proteinToProteins = new ArrayList<>();

    @Relationship(type = "ProteinToG4", direction = Direction.OUTGOING)
    private List<ProteinToG4> proteinToG4s = new ArrayList<>();

    @Relationship(type = "ProteinToGene", direction = Direction.OUTGOING)
    private List<ProteinToGene> proteinToGenes = new ArrayList<>();

    // 构造函数、getter和setter方法
    public Protein() {}

    public Protein(String fullName, String entryName, String proteinId) {
        this.fullName = fullName;
        this.entryName = entryName;
        this.proteinId = proteinId;
    }

}