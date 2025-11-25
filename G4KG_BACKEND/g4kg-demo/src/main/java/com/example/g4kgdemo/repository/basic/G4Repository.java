package com.example.g4kgdemo.repository.basic;

import com.example.g4kgdemo.model.G4;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface G4Repository {

    // 组合查询，分页通过 Cypher 的 skip 和 limit 控制
    @Query("MATCH (g:G4) " +
            "WHERE " +
            "  ($g4Id IS NULL OR $g4Id = '' OR g.G4Id = $g4Id) " +
            "  AND ($chr IS NULL OR $chr = '' OR g.Chr = $chr) " +
            "  AND ($location IS NULL OR $location = '' OR g.Location = $location) " +
            "  AND ($sampleName IS NULL OR $sampleName = '' OR g.SampleName = $sampleName) " +
            "  AND ($confidenceLevel IS NULL OR $confidenceLevel = '' OR g.ConfidenceLevel = $confidenceLevel) " +
            "  AND ($score IS NULL OR $score = '' OR g.Score >= $score) " +
            "RETURN g SKIP $skip LIMIT $limit")
    List<G4> findByCombinedFilters(@Param("g4Id") String g4Id,
                                   @Param("chr") String chr,
                                   @Param("location") String location,
                                   @Param("sampleName") String sampleName,
                                   @Param("confidenceLevel") String confidenceLevel,
                                   @Param("score") String score,
                                   @Param("skip") int skip,
                                   @Param("limit") int limit);

    // 统计符合条件的 G4 数量
    @Query("MATCH (g:G4) " +
            "WHERE " +
            "  ($g4Id IS NULL OR $g4Id = '' OR g.G4Id = $g4Id) " +
            "  AND ($chr IS NULL OR $chr = '' OR g.Chr = $chr) " +
            "  AND ($location IS NULL OR $location = '' OR g.Location = $location) " +
            "  AND ($sampleName IS NULL OR $sampleName = '' OR g.SampleName = $sampleName) " +
            "  AND ($confidenceLevel IS NULL OR $confidenceLevel = '' OR g.ConfidenceLevel = $confidenceLevel) " +
            "  AND ($score IS NULL OR $score = '' OR g.Score >= $score) " +
            "RETURN COUNT(g)")
    long countByCombinedFilters(@Param("g4Id") String g4Id,
                                @Param("chr") String chr,
                                @Param("location") String location,
                                @Param("sampleName") String sampleName,
                                @Param("confidenceLevel") String confidenceLevel,
                                @Param("score") String score);


    // 获取前端下拉框所需的所有唯一 SampleName
    @Query("MATCH (g:G4) RETURN DISTINCT g.SampleName")
    List<String> findDistinctSampleNames();

    // 获取前端下拉框所需的所有唯一 ConfidenceLevel
    @Query("MATCH (g:G4) RETURN DISTINCT g.ConfidenceLevel")
    List<String> findDistinctConfidenceLevels();

}