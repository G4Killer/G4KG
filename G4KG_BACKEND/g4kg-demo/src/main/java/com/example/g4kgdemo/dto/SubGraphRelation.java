package com.example.g4kgdemo.dto;

import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
public class SubGraphRelation {
    @Getter
    private final long startNodeId;
    @Getter
    private final String type;
    @Getter
    private final long endNodeId;
    @Getter
    private final Map<String, Object> properties;
    private final Long id;

    public SubGraphRelation(Long id, String type, Long startNodeId, Long endNodeId, Map<String, Object> properties) {
        this.id = id;
        this.type = type;
        this.startNodeId = startNodeId;
        this.endNodeId = endNodeId;
        this.properties = properties;
    }


    @Override
    public String toString() {
        return "SubGraphRelation{" +
                "startNodeId=" + startNodeId +
                ", type='" + type + '\'' +
                ", endNodeId=" + endNodeId +
                ", properties=" + properties +
                '}';
    }
}

