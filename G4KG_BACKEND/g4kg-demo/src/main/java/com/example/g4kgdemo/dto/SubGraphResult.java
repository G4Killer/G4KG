package com.example.g4kgdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor // 添加无参构造器
@AllArgsConstructor
public class SubGraphResult {
    private List<SubGraphNode> nodes;
    private List<SubGraphRelation> relationships;
}
