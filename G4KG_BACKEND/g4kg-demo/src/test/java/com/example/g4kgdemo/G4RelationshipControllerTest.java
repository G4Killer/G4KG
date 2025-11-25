package com.example.g4kgdemo;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class G4RelationshipControllerTest {

    @Autowired
    private MockMvc mockMvc;  // 用于模拟 HTTP 请求

    // 测试查询成功的情况
    @Test
    public void testSearchG4Relationships() throws Exception {
        mockMvc.perform(get("/api/g4-relationship/search")
                        .param("nodeType", "Disease")  // 设置节点类型
                        .param("nodeName", "Cancer")  // 设置节点名称
                        .param("threshold", "90")
                        .param("hops", "1")  // 设置 hops 参数
                        .param("page", "1"))  // 设置页码
                .andDo(print())  // 打印 HTTP 响应
                .andDo(result -> {
                    // 打印当前页码和返回数据长度
                    String currentPage = JsonPath.read(result.getResponse().getContentAsString(), "$.currentPage").toString();
                    System.out.println("Current Page: " + currentPage);
                    String contentLength = JsonPath.read(result.getResponse().getContentAsString(), "$.content.length()").toString();
                    System.out.println("Content Length: " + contentLength);
                })
                .andExpect(status().isOk())  // HTTP 200 状态
                .andExpect(jsonPath("$.content.length()").value(10))  // 假设返回 10 条记录
                .andExpect(jsonPath("$.currentPage").value(1));  // 当前页为 1
    }


    // 测试查询无效的页码
    @Test
    public void testSearchG4Relationships_withInvalidPage() throws Exception {
        mockMvc.perform(get("/api/g4-relationship/search")
                        .param("nodeType", "GO")
                        .param("nodeId", "12")
                        .param("hops", "1")
                        .param("page", "1000000"))  // 请求超大页码
                .andDo(result -> {
                    // 打印返回的 currentPage 和内容
                    String currentPage = JsonPath.read(result.getResponse().getContentAsString(), "$.currentPage").toString();
                    System.out.println("Current Page (Invalid Page Test): " + currentPage);
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());  // 返回的数据为数组
    }

    // 测试传递负数页码，应该返回 400 错误
    @Test
    public void testSearchG4Relationships_withNegativePage() throws Exception {
        mockMvc.perform(get("/api/g4-relationship/search")
                        .param("nodeType", "GO")
                        .param("nodeId", "12")
                        .param("hops", "1")
                        .param("page", "-1"))  // 页码为负数
                .andDo(result -> {
                    // 打印返回的状态码和错误信息
                    String status = result.getResponse().getStatus() + "";
                    System.out.println("Response Status: " + status);
                })
                .andExpect(status().isBadRequest());  // HTTP 400 错误
    }

    // 测试没有符合条件的记录时返回的结果
    @Test
    public void testSearchG4Relationships_withEmptyResults() throws Exception {
        mockMvc.perform(get("/api/g4-relationship/search")
                        .param("nodeType", "GO")
                        .param("nodeId", "9999999")  // 不存在的节点 ID
                        .param("hops", "2")
                        .param("page", "1"))  // 页码为 1
                .andDo(result -> {
                    // 打印返回的结果内容长度
                    String contentLength = JsonPath.read(result.getResponse().getContentAsString(), "$.content.length()").toString();
                    System.out.println("Content Length (Empty Results Test): " + contentLength);
                })
                .andExpect(status().isOk())  // HTTP 200 状态
                .andExpect(jsonPath("$.content.length()").value(0))  // 结果为空
                .andExpect(jsonPath("$.currentPage").value(1));  // 当前页为 1
    }


    // 测试 threshold 为负值时的情况，应该返回 HTTP 400 错误
    @Test
    public void testSearchG4Relationships_withNegativeThreshold() throws Exception {
        mockMvc.perform(get("/api/g4-relationship/search")
                        .param("nodeType", "GO")
                        .param("nodeId", "12")
                        .param("hops", "1")
                        .param("threshold", "-5"))  // 设置非法的 threshold
                .andDo(result -> {
                    // 打印错误信息
                    String errorMessage = result.getResponse().getContentAsString();
                    System.out.println("Negative Threshold Error: " + errorMessage);
                })
                .andExpect(status().isBadRequest());  // HTTP 400 错误
    }
}
