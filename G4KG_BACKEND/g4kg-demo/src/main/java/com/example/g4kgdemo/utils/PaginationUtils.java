package com.example.g4kgdemo.utils;

import com.example.g4kgdemo.dto.PaginatedResult;

import java.util.Collections;
import java.util.List;

public class PaginationUtils {

    // 通用的分页结果解包方法
    public static <T> List<T> extractContent(PaginatedResult<T> paginatedResult) {
        if (paginatedResult == null || paginatedResult.getContent() == null) {
            return Collections.emptyList();
        }
        return paginatedResult.getContent();
    }
}
