package com.example.g4kgdemo;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SubGraphControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetNodeProperties_Success() throws Exception {
        mockMvc.perform(get("/api/subgraph/properties")
                        .param("nodeType", "G4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeIdProperty").value("G4Id"));
    }

    @Test
    public void testGetSubGraph_Success() throws Exception {
        mockMvc.perform(get("/api/subgraph/get")
                        .param("nodeType", "G4")
                        .param("nodeId", "HG4_4435")
                        .param("depth", "2")
                        .param("limit", "5")
                        .param("includeIsolated", "false"))
                .andDo(print())
                .andExpect(status().isOk()) // 验证 HTTP 状态码 200
                .andExpect(jsonPath("$.nodes").isArray()) // 验证 nodes 是数组
                .andExpect(jsonPath("$.nodes[0].id").isNumber()) // 验证节点 ID 存在并是数字
                .andExpect(jsonPath("$.nodes[0].labels").isArray()) // 验证 labels 是数组
                .andExpect(jsonPath("$.nodes[0].properties").isMap()) // 验证 properties 是 Map
                .andExpect(jsonPath("$.relationships").isArray()) // 验证 relationships 是数组
                .andExpect(jsonPath("$.relationships[0].id").isNumber()) // 验证关系 ID 存在并是数字
                .andExpect(jsonPath("$.relationships[0].type").isString()) // 验证关系类型是字符串
                .andExpect(jsonPath("$.relationships[0].startNodeId").isNumber()) // 验证关系的起始节点 ID 是数字
                .andExpect(jsonPath("$.relationships[0].endNodeId").isNumber()) // 验证关系的结束节点 ID 是数字
                .andExpect(jsonPath("$.relationships[0].properties").isMap()); // 验证关系属性是 Map
    }

    @Test
    public void testGetSubGraph_InvalidDepth() throws Exception {
        mockMvc.perform(get("/api/subgraph/get")
                        .param("nodeType", "G4")
                        .param("nodeIdProperty", "G4Id")
                        .param("nodeId", "HG4_4435")
                        .param("depth", "0") // 无效深度
                        .param("limit", "50")
                        .param("includeIsolated", "false"))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 验证 HTTP 状态码 400
                .andExpect(jsonPath("$").value("Depth and limit must be greater than 0.")); // 验证错误信息
    }

    @Test
    public void testGetSubGraph_InvalidNodeType() throws Exception {
        mockMvc.perform(get("/api/subgraph/get")
                        .param("nodeType", "")
                        .param("nodeIdProperty", "G4Id")
                        .param("nodeId", "HG4_4435")
                        .param("depth", "2")
                        .param("limit", "50")
                        .param("includeIsolated", "false"))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 验证 HTTP 状态码 400
                .andExpect(jsonPath("$").value("Node type cannot be null or empty.")); // 验证错误信息
    }

    @Test
    public void testGetSubGraph_InvalidNodeId() throws Exception {
        mockMvc.perform(get("/api/subgraph/get")
                        .param("nodeType", "G4")
                        .param("nodeIdProperty", "G4Id")
                        .param("nodeId", "") // 无效节点 ID
                        .param("depth", "2")
                        .param("limit", "50")
                        .param("includeIsolated", "false"))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 验证 HTTP 状态码 400
                .andExpect(jsonPath("$").value("Node ID cannot be null or empty.")); // 验证错误信息
    }
}
