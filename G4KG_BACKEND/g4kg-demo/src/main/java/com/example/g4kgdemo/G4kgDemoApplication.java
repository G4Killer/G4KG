package com.example.g4kgdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EntityScan("com.example.g4kgdemo.model")  // 扫描实体的包路径
@EnableNeo4jRepositories("com.example.g4kgdemo.repository")  // 扫描 Neo4j 仓库的包路径
public class G4kgDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(G4kgDemoApplication.class, args);
    }
}

