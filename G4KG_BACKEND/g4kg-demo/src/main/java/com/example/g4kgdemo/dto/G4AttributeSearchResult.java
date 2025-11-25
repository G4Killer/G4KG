package com.example.g4kgdemo.dto;

import com.example.g4kgdemo.model.G4;
import lombok.Getter;
@Getter
public class G4AttributeSearchResult {
    private final String g4Id;
    private final String chr;
    private final String location;
    private final String sampleName;
    private final String confidenceLevel;
    private final String score;
    private final String strand;
    private final String sequence;
    private final String cellLine;
    private final String source;

    // 构造方法，从G4实例转换为G4DTO
    public G4AttributeSearchResult(G4 g4) {
        this.g4Id = g4.getG4Id();
        this.chr = g4.getChr();
        this.location = g4.getLocation();
        this.sampleName = g4.getSampleName();
        this.confidenceLevel = g4.getConfidenceLevel();
        this.score = g4.getScore();
        this.strand = g4.getStrand();
        this.sequence = g4.getSequence();
        this.cellLine = g4.getCellLine();
        this.source = g4.getSource();
    }

    // 便于显示的toString方法
    @Override
    public String toString() {
        return "G4AttributeSearchResult{" +
                "g4Id='" + g4Id + '\'' +
                ", chr='" + chr + '\'' +
                ", location='" + location + '\'' +
                ", sampleName='" + sampleName + '\'' +
                ", confidenceLevel='" + confidenceLevel + '\'' +
                ", score='" + score + '\'' +
                ", strand='" + strand + '\'' +
                ", sequence='" + sequence + '\'' +
                ", cellLine='" + cellLine + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

    // 添加其他需要的 Getter 方法...
}
