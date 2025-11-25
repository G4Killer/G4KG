package com.example.g4kgdemo;

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
public class G4ControllerTest {

    @Autowired
    private MockMvc mockMvc;  // 用于模拟 HTTP 请求

    @Test
    public void testSearchG4Records() throws Exception {
        mockMvc.perform(get("/api/g4/search")
                        .param("chr", "chr1")
                        .param("page", "1"))
                .andDo(print())  // 打印完整的请求和响应
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(1));
    }



    @Test
    public void testSearchG4Records_withInvalidPage() throws Exception {
        // 请求一个超出总页数的页码
        mockMvc.perform(get("/api/g4/search")
                        .param("sampleName", "A549_G4chip")  // 查询条件
                        .param("page", "1000000"))  //
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(17927)) // 确保返回的是最后一页
                .andExpect(jsonPath("$.totalPages").value(17927)) // 确保总页数是 17927
                .andExpect(jsonPath("$.content").isArray());

    }


    @Test
    public void testSearchG4Records_withNegativePage() throws Exception {
        // 测试传递一个负数的 page 值，应该返回 HTTP 400 错误
        mockMvc.perform(get("/api/g4/search")
                        .param("sampleName", "A549_G4chip")
                        .param("page", "-1"))  // 当前页为负数
                .andExpect(status().isBadRequest());  // 返回 HTTP 400 错误
    }

    @Test
    public void testSearchG4Records_withEmptyResults() throws Exception {
        // 测试没有符合条件的记录
        mockMvc.perform(get("/api/g4/search")
                        .param("sampleName", "InvalidSample")  // 不存在的 sampleName
                        .param("page", "1"))  // 当前页为 1
                .andExpect(status().isOk())  // HTTP 200 状态
                .andExpect(jsonPath("$.content.length()").value(0))  // 返回 0 条记录
                .andExpect(jsonPath("$.totalElements").value(0))  // 总记录数为 0
                .andExpect(jsonPath("$.totalPages").value(0))  // 总页数为 0
                .andExpect(jsonPath("$.currentPage").value(1));  // 当前页为 1
    }

    @Test
    public void testSearchG4Records_withInvalidSampleName() throws Exception {
        // 测试查询时传递无效的 sampleName 参数
        mockMvc.perform(get("/api/g4/search")
                        .param("sampleName", "NonExistentSample")  // 不存在的 sampleName
                        .param("page", "1"))  // 当前页为 1
                .andExpect(status().isOk())  // HTTP 200 状态
                .andExpect(jsonPath("$.content.length()").value(0))  // 返回 0 条记录
                .andExpect(jsonPath("$.totalElements").value(0))  // 总记录数为 0
                .andExpect(jsonPath("$.totalPages").value(0))  // 总页数为 0
                .andExpect(jsonPath("$.currentPage").value(1));  // 当前页为 1
    }
}
