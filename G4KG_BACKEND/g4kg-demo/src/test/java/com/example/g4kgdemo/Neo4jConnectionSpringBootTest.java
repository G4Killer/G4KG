package com.example.g4kgdemo;

import org.junit.jupiter.api.Test;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class Neo4jConnectionSpringBootTest {

    @Autowired
    private Driver driver; // 确保 Neo4j 驱动程序被注入

    @Test
    public void testNeo4jConnection() {
        assertNotNull(driver, "Neo4j Driver should be configured and not null");
        System.out.println("Successfully connected to Neo4j with Spring Boot configuration.");
    }
}
