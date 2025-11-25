package com.example.g4kgdemo.dto;

import com.example.g4kgdemo.model.G4;
import lombok.Getter;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.value.MapValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class G4RelationshipSearchResult {

    private final G4 g4; // 原始 G4 对象
    private final List<Object> path; // 原始路径节点列表
    private final List<String> relationNames;

    // 构造方法
    public G4RelationshipSearchResult(G4 g4, List<Object> path, List<String> relationNames) {
        this.g4 = g4;
        this.path = path;
        this.relationNames = relationNames;
    }

    // 获取格式化后的 G4 详细信息
    public String getFormattedG4Details() {
        return formatG4Details(this.g4);
    }

    // 获取格式化后的路径详情，仅包含节点 ID 信息
    public List<String> getFormattedPathDetails() {
        return path.stream()
                .map(this::formatNodeId)
                .collect(Collectors.toList());
    }

    // 格式化 G4 节点的详细信息
    private String formatG4Details(G4 g4) {
        return g4 == null ? "No G4 Data" :
                String.format("G4{G4Id='%s', Chr='%s', Location='%s', SampleName='%s', ConfidenceLevel='%s', Score='%s', Strand='%s', Sequence='%s', CellLine='%s', Source='%s'}",
                        g4.getG4Id(), g4.getChr(), g4.getLocation(), g4.getSampleName(),
                        g4.getConfidenceLevel(), g4.getScore(), g4.getStrand(), g4.getSequence(),
                        g4.getCellLine(), g4.getSource());
    }

    // 格式化节点 ID 信息，仅提取 ID
    private String formatNodeId(Object node) {
        // 检查是否是 MapValue 类型
        if (node instanceof MapValue) {
            Map<String, Object> nodeMap = ((MapValue) node).asMap();
            return formatNodeIdFromMap(nodeMap);
        } else if (node instanceof Map) {
            // 若已经是 Map 类型则直接处理
            return formatNodeIdFromMap((Map<String, Object>) node);
        } else {
            // Debugging output for unexpected node types
            System.out.println("Node class type: " + node.getClass());
            return "Unknown Node Type: " + node.getClass().getSimpleName() + " - " + node.toString();
        }
    }

    // 从 Map 中提取 ID 信息
    private String formatNodeIdFromMap(Map<String, Object> nodeMap) {
        if (nodeMap.containsKey("DiseaseId")) {
            return String.format("Disease{DiseaseId='%s'}", nodeMap.get("DiseaseId"));
        } else if (nodeMap.containsKey("GeneId")) {
            return String.format("Gene{GeneId='%s'}", nodeMap.get("GeneId"));
        } else if (nodeMap.containsKey("G4Id")) {
            return String.format("G4{G4Id='%s'}", nodeMap.get("G4Id"));
        } else if (nodeMap.containsKey("ProteinId")) {
            return String.format("Protein{ProteinId='%s'}", nodeMap.get("ProteinId"));
        } else if (nodeMap.containsKey("PathwayId")) {
            return String.format("Pathway{PathwayId='%s'}", nodeMap.get("PathwayId"));
        } else if (nodeMap.containsKey("GoId")) {
            return String.format("GO{GoId='%s'}", nodeMap.get("GoId"));
        }
        return "Unknown Node Type";
    }

    // 覆盖 toString 方法，调用格式化后的路径
    @Override
    public String toString() {
        return "G4RelationshipSearchResult{" +
                "g4=" + getFormattedG4Details() +
                ", formattedPath=" + getFormattedPathDetails() +
                ", relationNames=" + relationNames +
                '}';
    }
}
