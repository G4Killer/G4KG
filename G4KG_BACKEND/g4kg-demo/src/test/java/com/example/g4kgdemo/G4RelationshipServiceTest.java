package com.example.g4kgdemo.service;

import com.example.g4kgdemo.dto.G4RelationshipSearchResult;
import com.example.g4kgdemo.dto.PaginatedResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class G4RelationshipServiceTest {

    @Autowired
    private G4RelationshipService g4RelationshipService; // 正确的服务实例

    @Test
    public void testFindRelatedG4WithPathAndRelations() {
        String nodeType = "GO"; // 节点类型
        String nodeIdProperty = null; // 动态设置属性
        String nodeNameProperty = null; // 动态设置属性
        String nodeId = "12"; // 节点 ID
        String nodeName = null; // 节点名称
        Integer threshold = null; // 查询阈值
        int hops = 2; // 跳数
        int skip = 0; // 跳过的记录数
        int limit = 10; // 每页限制返回的记录数

        // 调用服务方法获取分页结果
        PaginatedResult<G4RelationshipSearchResult> paginatedResult = g4RelationshipService.findRelatedG4WithPathAndRelations(
                nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName, threshold, hops, skip, limit);

        // 断言分页结果不为空
        assertNotNull(paginatedResult, "分页结果应不为空");
        assertNotNull(paginatedResult.getContent(), "分页内容应不为空");
        assertFalse(paginatedResult.getContent().isEmpty(), "分页内容应至少包含一个项");

        // 断言总数为 null（未加载总数）
        assertNull(paginatedResult.getTotalElements(), "总记录数应为 null 表示未加载");
        assertNull(paginatedResult.getTotalPages(), "总页数应为 null 表示未加载");

        // 断言当前页正确
        assertEquals(0, paginatedResult.getCurrentPage(), "当前页应为 0");

        // 打印分页信息
        System.out.println("Current Page: " + paginatedResult.getCurrentPage());

        // 遍历并打印分页内容
        paginatedResult.getContent().forEach(result -> {
            System.out.println("G4 Details: " + result.getFormattedG4Details());
            System.out.println("Path Details: " + result.getFormattedPathDetails());
            System.out.println("Relation Names: " + result.getRelationNames());
        });
    }
}
