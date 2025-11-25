package com.example.g4kgdemo.service;

import com.example.g4kgdemo.dto.G4AttributeSearchResult;
import com.example.g4kgdemo.dto.PaginatedResult;
import com.example.g4kgdemo.model.G4;

import java.util.List;

public interface G4Service {

    // 修改后的分页查询方法，支持传递 skip 和 limit
    PaginatedResult<G4AttributeSearchResult> findByCombinedFilters(String g4Id, String chr, String location, String sampleName,
                                                                   String confidenceLevel, String score, int skip, int limit);

    // 统计符合条件的 G4 数量
    long countByCombinedFilters(String g4Id, String chr, String location, String sampleName,
                                String confidenceLevel, String score);

    // 获取所有唯一的 SampleName
    List<String> findDistinctSampleNames();

    // 获取所有唯一的 ConfidenceLevel
    List<String> findDistinctConfidenceLevels();
}
