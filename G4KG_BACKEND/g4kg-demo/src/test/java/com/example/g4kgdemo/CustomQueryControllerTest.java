package com.example.g4kgdemo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试同步查询接口 - 有效查询
     */
    @Test
    public void testExecuteCustomQuery_ValidQuery() throws Exception {
        Map<String, String> requestBody = Map.of(
                "query", "MATCH (g:G4)-[:G4ToDisease]->(d:Disease) RETURN g.G4Id AS g4Id, d.DiseaseName AS diseaseName LIMIT 5"
        );

        mockMvc.perform(post("/api/custom-query/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)))
                .andDo(result -> {
                    System.out.println("Valid Query Response: " + result.getResponse().getContentAsString());
                });
    }

    /**
     * 测试同步查询接口 - 缺失参数
     */
    @Test
    public void testExecuteCustomQuery_MissingQuery() throws Exception {
        Map<String, String> requestBody = Map.of(); // 没有 query 参数

        mockMvc.perform(post("/api/custom-query/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    System.out.println("Missing Query Response: " + result.getResponse().getContentAsString());
                });
    }

    /**
     * 测试同步查询接口 - 非法写操作查询
     */
    @Test
    public void testExecuteCustomQuery_InvalidWriteQuery() throws Exception {
        Map<String, String> requestBody = Map.of(
                "query", "CREATE (n:TestNode {name: 'test'}) RETURN n"
        );

        mockMvc.perform(post("/api/custom-query/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    System.out.println("Invalid Write Query Response: " + result.getResponse().getContentAsString());
                });
    }

    /**
     * 测试异步查询接口 - 有效查询
     */
    @Test
    public void testExecuteCustomQueryAsync_ValidQuery() throws Exception {
        Map<String, String> requestBody = Map.of(
                "query", "MATCH (g:G4)-[:G4ToDisease]->(d:Disease) RETURN g.G4Id AS g4Id, d.DiseaseName AS diseaseName LIMIT 5"
        );

        mockMvc.perform(post("/api/custom-query/execute-async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)))
                .andDo(result -> {
                    System.out.println("Valid Async Query Response: " + result.getResponse().getContentAsString());
                });
    }

    /**
     * 测试异步查询接口 - 非法查询
     */
    @Test
    public void testExecuteCustomQueryAsync_InvalidQuery() throws Exception {
        Map<String, String> requestBody = Map.of(
                "query", "DELETE n WHERE n.name = 'test'"
        );

        mockMvc.perform(post("/api/custom-query/execute-async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    System.out.println("Invalid Async Query Response: " + result.getResponse().getContentAsString());
                });
    }

    /**
     * 测试异步查询接口 - 缺失参数
     */
    @Test
    public void testExecuteCustomQueryAsync_MissingQuery() throws Exception {
        Map<String, String> requestBody = Map.of(); // 没有 query 参数

        mockMvc.perform(post("/api/custom-query/execute-async")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    System.out.println("Missing Async Query Response: " + result.getResponse().getContentAsString());
                });
    }
}
