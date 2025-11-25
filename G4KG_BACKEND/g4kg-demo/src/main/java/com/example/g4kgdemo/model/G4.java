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
@Node("G4")
public class G4 {
    @Id @GeneratedValue
    private Long id;

    @Property("Chr")
    private String chr;

    @Property("Location")
    private String location;

    @Property("G4Id")
    private String g4Id;

    @Property("Strand")
    private String strand;

    @Property("Score")
    private String score;

    @Property("ConfidenceLevel")
    private String confidenceLevel;

    @Property("Sequence")
    private String sequence;

    @Property("SampleName")
    private String sampleName;

    @Property("CellLine")
    private String cellLine;

    @Property("Source")
    private String source;

    @Relationship(type = "G4ToGene", direction = Direction.OUTGOING)
    private List<G4ToGene> g4ToGenes = new ArrayList<>();

    @Relationship(type = "G4ToDisease", direction = Direction.OUTGOING)
    private List<G4ToDisease> g4ToDiseases = new ArrayList<>();

    // 构造函数、getter和setter方法
    public G4() {}

    public G4(String chr, String location, String g4Id, String strand, String score, String confidenceLevel, String sequence, String sampleName, String cellLine, String source) {
        this.chr = chr;
        this.location = location;
        this.g4Id = g4Id;
        this.strand = strand;
        this.score = score;
        this.confidenceLevel = confidenceLevel;
        this.sequence = sequence;
        this.sampleName = sampleName;
        this.cellLine = cellLine;
        this.source = source;
    }

}