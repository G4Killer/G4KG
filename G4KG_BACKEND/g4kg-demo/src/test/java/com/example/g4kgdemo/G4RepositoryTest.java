package com.example.g4kgdemo;

import com.example.g4kgdemo.model.G4;
import com.example.g4kgdemo.repository.basic.G4Repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
/*
@SpringBootTest
public class G4RepositoryTest {

    @Autowired
    private G4Repository g4Repository;

    @Test
    public void testFindByCombinedFilters() {
        // 测试使用具体的 `G4Id`, `Chr`, `Location`, `SampleName`, `ConfidenceLevel`, 和 `Score` 参数组合查询
        List<G4> results = g4Repository.findByCombinedFilters(null, null, null, "A549_G4chip", null, null, 10);
        assertNotNull(results);
        assertFalse(results.isEmpty(), "应该至少返回一个结果");

        System.out.println("findByCombinedFilters 结果：" + results);
    }

    @Test
    public void testFindDistinctSampleNames() {
        // 测试获取所有唯一的 SampleName 值
        List<String> sampleNames = g4Repository.findDistinctSampleNames();
        assertNotNull(sampleNames);
        assertFalse(sampleNames.isEmpty(), "应该至少返回一个 SampleName");

        System.out.println("唯一的 SampleName 值：" + sampleNames);
    }

    @Test
    public void testFindDistinctConfidenceLevels() {
        // 测试获取所有唯一的 ConfidenceLevel 值
        List<String> confidenceLevels = g4Repository.findDistinctConfidenceLevels();
        assertNotNull(confidenceLevels);
        assertFalse(confidenceLevels.isEmpty(), "应该至少返回一个 ConfidenceLevel");

        System.out.println("唯一的 ConfidenceLevel 值：" + confidenceLevels);
    }
}
*/