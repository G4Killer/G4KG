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
public class FunctionAnalyseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试通过属性查找并分析 GO 功能分布
     */
    @Test
    public void testAnalyzeGOFunctionDistributionByAttribute() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "useRelationship", false,
                "sampleName", "HEK293T_Flavopiridol.CUT.Tag_DMSO",
                "g4Limit", 50,
                "resultLimit", 10
        );

        mockMvc.perform(post("/api/function-analyse/analyze-go")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].goName").exists())
                .andDo(result -> {
                    // 输出响应信息
                    System.out.println("Response: " + result.getResponse().getContentAsString());
                });
    }

    /**
     * 测试通过关系查找并分析 Pathway 功能分布
     */
    @Test
    public void testAnalyzePathwayDistributionByRelationship() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "useRelationship", true,
                "nodeType", "GO",
                "nodeId", "12",
                "hops", 2,
                "g4Limit", 50,
                "resultLimit", 10
        );

        mockMvc.perform(post("/api/function-analyse/analyze-pathway")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].pathwayName").exists())
                .andDo(result -> {
                    // 输出响应信息
                    System.out.println("Response: " + result.getResponse().getContentAsString());
                });
    }

    /**
     * 测试构建功能富集分析网络
     */
    @Test
    public void testBuildG4FunctionEnrichmentNetwork() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "useRelationship", false,
                "sampleName", "HEK293T_Flavopiridol.CUT.Tag_DMSO",
                "g4Limit", 50
        );

        mockMvc.perform(post("/api/function-analyse/build-enrichment-network")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$.relationships.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$.nodes[0].id").exists())
                .andDo(result -> {
                    // 输出响应信息
                    System.out.println("Response: " + result.getResponse().getContentAsString());
                });
    }

    /**
     * 测试错误：缺少必要参数
     */
    @Test
    public void testMissingParameters() throws Exception {
        // 构造测试用例：缺少必要的参数
        Map<String, Object> requestBody = Map.of(
                "useRelationship", false // 仅设置 useRelationship
                // 缺少其他必要参数
        );

        mockMvc.perform(post("/api/function-analyse/analyze-go")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest()) // 验证返回状态为 400 Bad Request
                .andDo(result -> {
                    // 输出详细的错误响应信息
                    System.out.println("Error Response: " + result.getResponse().getContentAsString());
                });
    }

    /**
     * 测试错误：无效参数值
     */
    @Test
    public void testInvalidParameters() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "useRelationship", true,
                "nodeType", "InvalidNodeType", // 无效的 nodeType
                "g4Limit", -1, // 无效的 g4Limit
                "resultLimit", 10
        );

        mockMvc.perform(post("/api/function-analyse/analyze-pathway")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    // 输出响应信息
                    System.out.println("Error Response: " + result.getResponse().getContentAsString());
                });
    }

    /**
     * 测试错误：返回空结果
     */
    @Test
    public void testAnalyzePathwayWithNonExistingNode() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "useRelationship", true,
                "nodeType", "GO",
                "nodeId", "NonExistingNode", // 不存在的节点 ID
                "hops", 2,
                "g4Limit", 50,
                "resultLimit", 10
        );

        mockMvc.perform(post("/api/function-analyse/analyze-pathway")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNoContent())
                .andDo(result -> {
                    System.out.println("Node NonExist Response: " + result.getResponse().getContentAsString());
                });
    }
    /**
     * 测试错误：返回空结果
     */
    @Test
    public void testAnalyzePathwayWithNonExistingG4Attributes() throws Exception {
        // 模拟通过属性查询但 G4 数据不存在的情况
        Map<String, Object> requestBody = Map.of(
                "useRelationship", false,
                "g4Id", "NonExistingG4Id", // 不存在的 G4 ID
                "g4Limit", 50,
                "resultLimit", 10
        );

        mockMvc.perform(post("/api/function-analyse/analyze-pathway")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNoContent()) // 验证返回状态码为 204 No Content
                .andDo(result -> {
                    // 输出响应信息
                    System.out.println("Non-existing G4 Response: " + result.getResponse().getContentAsString());
                });
    }

}
