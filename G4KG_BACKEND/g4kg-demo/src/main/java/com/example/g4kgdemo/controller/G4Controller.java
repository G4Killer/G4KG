package com.example.g4kgdemo.controller;

import com.example.g4kgdemo.dto.G4AttributeSearchResult;
import com.example.g4kgdemo.dto.PaginatedResult;
import com.example.g4kgdemo.service.G4Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/g4")
public class G4Controller {

    private final G4Service g4Service;

    // 每页固定显示 10 条记录
    private static final int PAGE_SIZE = 10;

    @Autowired
    public G4Controller(G4Service g4Service) {
        this.g4Service = g4Service;
    }

    // 1. 分页查询接口
    @GetMapping("/search")
    public ResponseEntity<PaginatedResult<G4AttributeSearchResult>> searchG4Records(
            @RequestParam(required = false) String g4Id,
            @RequestParam(required = false) String chr,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sampleName,
            @RequestParam(required = false) String confidenceLevel,
            @RequestParam(required = false) String score,
            @RequestParam int page) { // 用户传入页码

        System.out.println("Received Parameters: ");
        System.out.println("g4Id: " + g4Id);
        System.out.println("chr: " + chr);
        System.out.println("location: " + location);
        System.out.println("sampleName: " + sampleName);
        System.out.println("confidenceLevel: " + confidenceLevel);
        System.out.println("score: " + score);
        System.out.println("page: " + page);

        // 处理页码错误
        if (page < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PaginatedResult<>(List.of(), 0L, 0, 1)); // 返回空数据并提示错误，当前页是 1
        }

        try {
            // 计算 skip，从 0 开始
            int skip = (page - 1) * PAGE_SIZE;

            // 调用 Service 层的分页查询方法
            PaginatedResult<G4AttributeSearchResult> result = g4Service.findByCombinedFilters(
                    g4Id, chr, location, sampleName, confidenceLevel, score, skip, PAGE_SIZE
            );

            // 获取总页数
            int totalPages = result.getTotalPages();


            // 如果请求的页码超过总页数，则返回最后一页的数据
            if (page > totalPages) {
                page = totalPages; // 将页码调整为最后一页
            }

            // 确保返回的页码是从 1 开始的
            int currentPage = Math.max(1, page); // 确保页码至少为 1

            // 打印每个 G4AttributeSearchResult 的内容
/*            result.getContent().forEach(item -> {
                System.out.println("Item: " + item.toString());  // 输出详细信息
            });
*/
            // 返回查询结果，页码从 1 开始
            return ResponseEntity.ok(new PaginatedResult<>(result.getContent(), result.getTotalElements(), totalPages, currentPage));

        } catch (IllegalArgumentException e) {
            // 捕获 Service 层的分页参数错误（如 skip 或 limit 不合法）
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PaginatedResult<>(List.of(), 0L, 0, 1)); // 空数据并返回当前页为 1
        } catch (RuntimeException e) {
            // 捕获数据库等运行时异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaginatedResult<>(List.of(), 0L, 0, 1)); // 空数据并返回当前页为 1
        }
    }

    // 2. 统计符合条件的记录数量
    @GetMapping("/count")
    public ResponseEntity<Long> countG4Records(
            @RequestParam(required = false) String g4Id,
            @RequestParam(required = false) String chr,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sampleName,
            @RequestParam(required = false) String confidenceLevel,
            @RequestParam(required = false) String score) {

        try {
            long count = g4Service.countByCombinedFilters(g4Id, chr, location, sampleName, confidenceLevel, score);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            // 捕获 Service 层的数据库异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(0L); // 如果发生异常，返回 0
        }
    }

    // 3. 获取所有唯一的 SampleName
    @GetMapping("/distinct/sample-names")
    public ResponseEntity<List<String>> getDistinctSampleNames() {
        try {
            List<String> sampleNames = g4Service.findDistinctSampleNames();
            return ResponseEntity.ok(sampleNames);
        } catch (RuntimeException e) {
            // 捕获 Service 层的数据库异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of()); // 如果发生异常，返回空列表
        }
    }

    // 4. 获取所有唯一的 ConfidenceLevel
    @GetMapping("/distinct/confidence-levels")
    public ResponseEntity<List<String>> getDistinctConfidenceLevels() {
        try {
            List<String> confidenceLevels = g4Service.findDistinctConfidenceLevels();
            return ResponseEntity.ok(confidenceLevels);
        } catch (RuntimeException e) {
            // 捕获 Service 层的数据库异常
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of()); // 如果发生异常，返回空列表
        }
    }
}
