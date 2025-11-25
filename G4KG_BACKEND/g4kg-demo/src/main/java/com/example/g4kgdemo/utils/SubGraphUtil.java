package com.example.g4kgdemo.utils;

import com.example.g4kgdemo.dto.SubGraphNode;
import com.example.g4kgdemo.dto.SubGraphRelation;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;

import java.util.ArrayList;
import java.util.List;

public class SubGraphUtil {

    public static List<SubGraphNode> parseNodes(List<Object> rawNodes) {
        if (rawNodes == null || rawNodes.isEmpty()) {
            return new ArrayList<>();
        }

        // 判断是否是单节点或多个节点
        if (rawNodes.size() == 1 && rawNodes.get(0) instanceof InternalNode) {
            return List.of(parseNode((InternalNode) rawNodes.get(0)));
        } else {
            List<SubGraphNode> nodes = new ArrayList<>();
            for (Object node : rawNodes) {
                if (node instanceof InternalNode) {
                    nodes.add(parseNode((InternalNode) node));
                } else {
                    throw new IllegalArgumentException("解析节点时遇到未知类型：" + node.getClass());
                }
            }
            return nodes;
        }
    }



    // 将单个 InternalNode 解析为 SubGraphNode
    private static SubGraphNode parseNode(InternalNode internalNode) {
        return new SubGraphNode(
                internalNode.id(),
                new ArrayList<>(internalNode.labels()),
                internalNode.asMap()
        );
    }

    // 解析关系数据
    public static List<SubGraphRelation> parseRelationships(List<Object> rawRelationships) {
        List<SubGraphRelation> relations = new ArrayList<>();
        if (rawRelationships != null) {
            for (Object rel : rawRelationships) {
                // 检查关系是否为null
                if (rel != null) {
                    // 如果关系是 InternalRelationship 类型，进行解析
                    if (rel instanceof InternalRelationship) {
                        SubGraphRelation subGraphRelation = parseRelationship((InternalRelationship) rel);
                        if (subGraphRelation != null) {  // 确保解析的关系不为 null
                            relations.add(subGraphRelation);
                        }
                    } else {
                        // 如果遇到未知类型，抛出异常
                        throw new IllegalArgumentException("解析关系时遇到未知类型：" + rel.getClass());
                    }
                }
            }
        }
        return relations;
    }

    // 将单个 InternalRelationship 解析为 SubGraphRelation
    private static SubGraphRelation parseRelationship(InternalRelationship relationship) {
        if (relationship == null) {
            // 如果关系为空，返回 null
            return null;
        }

        // 创建并返回 SubGraphRelation 对象
        return new SubGraphRelation(
                relationship.id(),
                relationship.type(),
                relationship.startNodeId(),
                relationship.endNodeId(),
                relationship.asMap()
        );
    }
}
