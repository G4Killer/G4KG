package com.example.g4kgdemo;

import com.example.g4kgdemo.dto.G4AttributeSearchResult;
import com.example.g4kgdemo.dto.PaginatedResult;
import com.example.g4kgdemo.service.G4Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class G4ServiceTest {

    @Autowired
    private G4Service g4Service;

    @Test
    public void testFindByCombinedFilters() {
        // 创建分页请求对象，第一页，每页显示 10 条数据
        int skip = 0;  // 跳过的条数
        int limit = 10;  // 每页显示的条数

        // 测试分页查询
        PaginatedResult<G4AttributeSearchResult> result = g4Service.findByCombinedFilters(
                null, null, null, "A549_G4chip", null, null, skip, limit);

        // 验证返回的结果
        assertNotNull(result, "分页查询结果不应为 null");
        assertNotNull(result.getContent(), "分页查询结果的内容不应为 null");
        assertTrue(result.getContent().size() <= limit, "分页查询返回的数据条数应小于或等于每页显示的条数");
        assertTrue(result.getTotalElements() >= 0, "总元素数应为非负数");
        assertTrue(result.getTotalPages() >= 0, "总页数应为非负数");
        assertEquals(0, result.getCurrentPage(), "当前页应该是第一页");

        // 输出查询结果
        result.getContent().forEach(System.out::println);
    }

    @Test
    public void testFindByCombinedFiltersWithInvalidPage() {
        // 请求一个超出范围的分页，比如 skip=10000，limit=10
        int skip = 100;
        int limit = 10;

        // 测试超出范围的分页查询，应该抛出 IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            g4Service.findByCombinedFilters(
                    null, null, null, "A549_G4chip", null, null, skip, limit);
        });
    }

    @Test
    public void testFindByCombinedFiltersWithNegativeLimit() {
        // 测试请求负数的 limit，应该抛出 IllegalArgumentException
        int skip = 0;
        int limit = -10;

        assertThrows(IllegalArgumentException.class, () -> {
            g4Service.findByCombinedFilters(
                    null, null, null, "A549_G4chip", null, null, skip, limit);
        });
    }

    @Test
    public void testFindByCombinedFiltersWithZeroLimit() {
        // 测试请求 limit 为 0，应该抛出 IllegalArgumentException
        int skip = 0;
        int limit = 0;

        assertThrows(IllegalArgumentException.class, () -> {
            g4Service.findByCombinedFilters(
                    null, null, null, "A549_G4chip", null, null, skip, limit);
        });
    }

    @Test
    public void testFindByCombinedFiltersWithEmptyResults() {
        // 请求没有符合条件的结果
        int skip = 0;
        int limit = 10;

        // 假设 "InvalidSample" 是一个不符合任何 G4 节点的 sampleName
        // 使用 assertThrows 来验证是否抛出了 RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            g4Service.findByCombinedFilters(
                    null, null, null, "InvalidSample", null, null, skip, limit);
        });

        // 验证异常消息
        assertEquals("No records found for the given search criteria.", exception.getMessage(),
                "异常消息应为 'No records found for the given search criteria.'");

        // 输出结果
        System.out.println("查询结果为空时的异常：" + exception.getMessage());
    }
}
