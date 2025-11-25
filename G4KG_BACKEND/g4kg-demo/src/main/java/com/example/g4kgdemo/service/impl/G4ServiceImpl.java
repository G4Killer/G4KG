package com.example.g4kgdemo.service.impl;

import com.example.g4kgdemo.dto.G4AttributeSearchResult;
import com.example.g4kgdemo.dto.PaginatedResult;
import com.example.g4kgdemo.model.G4;
import com.example.g4kgdemo.repository.basic.G4Repository;
import com.example.g4kgdemo.service.G4Service;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class G4ServiceImpl implements G4Service {

    private final G4Repository g4Repository;

    public G4ServiceImpl(G4Repository g4Repository) {
        this.g4Repository = g4Repository;
    }

    @Override
    public PaginatedResult<G4AttributeSearchResult> findByCombinedFilters(String g4Id, String chr, String location, String sampleName,
                                                                          String confidenceLevel, String score, int skip, int limit) {
        try {
            // 打印输入参数
            System.out.println("Service Layer Received Parameters:");
            System.out.println("g4Id: " + g4Id);
            System.out.println("chr: " + chr);
            System.out.println("location: " + location);
            System.out.println("sampleName: " + sampleName);
            System.out.println("confidenceLevel: " + confidenceLevel);
            System.out.println("score: " + score);
            System.out.println("skip: " + skip);
            System.out.println("limit: " + limit);

            // 查询符合条件的 G4 数据
            List<G4> g4List = g4Repository.findByCombinedFilters(g4Id, chr, location, sampleName, confidenceLevel, score, skip, limit);

            // 打印查询结果
            System.out.println("Repository Returned Records:");
            if (g4List.isEmpty()) {
                System.out.println("No records found.");
            } else {
                g4List.forEach(record -> System.out.println("Record: " + record));
            }

            // 获取符合条件的 G4 数据的总数
            long totalElements = g4Repository.countByCombinedFilters(g4Id, chr, location, sampleName, confidenceLevel, score);

            // 打印总记录数
            System.out.println("Total Records Count: " + totalElements);

            // 计算总页数
            int totalPages = (int) Math.ceil((double) totalElements / limit);

            // 如果没有符合条件的数据，返回空的分页结果
            if (g4List.isEmpty()) {
                return new PaginatedResult<>(new ArrayList<>(), totalElements, totalPages, 1); // 空数据时，页码从 1 开始
            }

            // 计算当前页码 (注意避免页码超出范围)
            int currentPage = (skip / limit) + 1; // 页码从 1 开始

            // 如果当前页码大于总页数，将当前页码设置为最后一页
            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            // 将 G4 节点数据转化为 DTO 列表
            List<G4AttributeSearchResult> results = g4List.stream()
                    .map(G4AttributeSearchResult::new)
                    .collect(Collectors.toList());

            // 返回封装了分页信息的结果
            return new PaginatedResult<>(results, totalElements, totalPages, currentPage);

        } catch (DataAccessException e) {
            // 捕获并打印数据访问异常
            System.err.println("Error accessing the database: " + e.getMessage());
            e.printStackTrace();

            // 抛出运行时异常
            throw new RuntimeException("Error accessing the database. Please try again later.", e);
        }
    }


    @Override
    public long countByCombinedFilters(String g4Id, String chr, String location, String sampleName, String confidenceLevel, String score) {
        try {
            return g4Repository.countByCombinedFilters(g4Id, chr, location, sampleName, confidenceLevel, score);
        } catch (DataAccessException e) {
            // 处理数据访问异常
            throw new RuntimeException("Error accessing the database while counting the records.", e);
        }
    }

    @Override
    public List<String> findDistinctSampleNames() {
        try {
            return g4Repository.findDistinctSampleNames();
        } catch (DataAccessException e) {
            // 处理数据访问异常
            throw new RuntimeException("Error accessing the database while fetching distinct sample names.", e);
        }
    }

    @Override
    public List<String> findDistinctConfidenceLevels() {
        try {
            return g4Repository.findDistinctConfidenceLevels();
        } catch (DataAccessException e) {
            // 处理数据访问异常
            throw new RuntimeException("Error accessing the database while fetching distinct confidence levels.", e);
        }
    }
}
