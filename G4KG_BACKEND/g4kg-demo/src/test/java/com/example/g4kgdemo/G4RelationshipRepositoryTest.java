package com.example.g4kgdemo.repository.basic;

import com.example.g4kgdemo.dto.G4RelationshipSearchResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class G4RelationshipRepositoryTest {

    @Qualifier("g4RelationshipRepository")
    @Autowired
    private G4RelationshipRepository repository;

    @Test
    public void testFindRelatedG4WithPathAndRelations() {
        // 设置查询参数
        String nodeType = "GO";
        String nodeIdProperty = "GoId";
        String nodeNameProperty = "GoTermName";
        String nodeId = "12";
        String nodeName = null;
        Integer threshold = null;
        int hops = 2;
        int skip = 0; // 分页起始位置
        int limit = 10; // 每页记录数

        // 调用查询方法
        List<G4RelationshipSearchResult> results = repository.findRelatedG4WithPathAndRelations(
                nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName, threshold, hops, skip, limit);

        // 验证返回结果
        assertNotNull(results, "查询结果应不为空");
        assertFalse(results.isEmpty(), "查询结果应至少包含一个项");

        // 输出格式化的结果
        for (G4RelationshipSearchResult result : results) {
            assertNotNull(result.getG4(), "G4节点信息应不为空");
            System.out.println("G4 Details: " + result.getFormattedG4Details());
            System.out.println("Path Details: " + result.getFormattedPathDetails());
            System.out.println("Relation Names: " + result.getRelationNames());
        }

        // 测试分页效果，下一页数据
        skip += limit;
        List<G4RelationshipSearchResult> nextPageResults = repository.findRelatedG4WithPathAndRelations(
                nodeType, nodeIdProperty, nodeNameProperty, nodeId, nodeName, threshold, hops, skip, limit);

        assertNotNull(nextPageResults, "下一页查询结果应不为空");
        System.out.println("Next Page Results Count: " + nextPageResults.size());
    }
}
