package com.example.g4kgdemo.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
public class GraphEntity {

    @Id
    private Long id; // 唯一标识字段，Spring Data Neo4j 要求每个实体类都有 ID

    public GraphEntity(Long id) {
        this.id = id;
    }

    public GraphEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
