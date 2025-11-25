package com.example.g4kgdemo.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PaginatedResult<T> {
    private List<T> content;
    private Long totalElements; // 使用包装类型，允许为 null 表示未计算
    private Integer totalPages; // 使用包装类型，允许为 null 表示未计算
    private int currentPage;

    // 完整构造函数
    public PaginatedResult(List<T> content, Long totalElements, Integer totalPages, int currentPage) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    // 简化构造函数，用于无需总数的场景
    public PaginatedResult(List<T> content, int currentPage) {
        this(content, null, null, currentPage);
    }
}
