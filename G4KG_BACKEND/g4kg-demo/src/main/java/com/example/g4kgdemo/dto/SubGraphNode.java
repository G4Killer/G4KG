package com.example.g4kgdemo.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SubGraphNode {
    private final long id;
    private final List<String> labels;
    private final Map<String, Object> properties;

    public SubGraphNode(long id, List<String> labels, Map<String, Object> properties) {
        this.id = id;
        this.labels = labels;
        this.properties = properties;
    }

    public long getId() {
        return id;
    }

    public List<String> getLabels() {
        return labels;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "SubGraphNode{" +
                "id=" + id +
                ", labels=" + labels +
                ", properties=" + properties +
                '}';
    }
}

