from dataclasses import dataclass, field
from typing import List, Dict, TypedDict, Literal
from langchain_core.messages import AnyMessage

class SubRouter(TypedDict):
    """Classify user query."""
    logic: str
    type: Literal["attribute_query", "relationship_query"]

@dataclass(kw_only=True)
class SubgraphState:
    """子图状态，包含查询结果、答案来源、实体识别和关系识别结果"""

    # 子图字段
    sub_router: SubRouter = field(default_factory=lambda: SubRouter(type="attribute_query", logic=""))
    """The router's classification of the user's query."""
    question: str
    """存储用户问题"""
    entities: List[str] = field(default_factory=list)
    """存储从用户查询中识别出来的实体信息，例如基因、疾病、药物等"""
    relationships: List[str] = field(default_factory=list)
    """存储从查询中识别出的实体之间的关系，可能是基因与疾病之间的关系等"""
    entities_refined: List[str] = field(default_factory=list)
    """存储精炼过后的实体信息"""
    relationships_refined: List[str] = field(default_factory=list)
    """存储精炼过后的关系信息"""
    schema: str = field(default_factory=lambda: """
    ### Schema:
    1. **Drug**: Medications used for diseases. Key attributes: `Name`, `DrugId`. Relationships: `DrugToProtein`, `DrugToDisease`, `DrugToDrug`.
    2. **Gene**: Genes associated with diseases. Key attributes: `GeneSymbol`, `FullName`, `Synonyms`, `GeneType`, `GeneId`. Relationships: `GeneToDisease`, `GeneToPathway`, `GeneToProtein`, `GeneToGene`, `GeneToGO`.
    3. **Disease**: Various diseases. Key attributes: `DiseaseId`, `DiseaseName`, `DiseaseSemanticType`. Relationships: `DiseaseToDisease`.
    4. **Protein**: Proteins related to genes and diseases. Key attributes: `FullName`, `EntryName`, `ProteinId`. Relationships: `ProteinToPathway`, `ProteinToGene`, `ProteinToProtein`, `ProteinToGO`, `ProteinToG4`.
    5. **Pathway**: Biological pathways. Key attributes: `PathwayName`, `PathwayId`. Relationships: `PathwayToPathway`.
    6. **GO**: Gene Ontology terms. Key attributes: `GoId`, `GoTermName`, `GoTermType`. Relationships: `GOToGO`.
    7. **G4**: G-quadruplex structures. Key attributes: `G4Id`, `Strand`, `ConfidenceLevel`, `Score`, `SampleName`, `Chr`, `Location`. Relationships: `G4ToGene`, `G4ToDisease`.
    """)
    """The Knowledge Graph schema."""

    # 从主图继承的共享字段
    documents: List[Dict] = field(default_factory=list)
    """存储由查询链（chain）返回的文档，可能是从数据库或其他数据源中检索到的信息"""
    answer_source: List[str] = field(default_factory=list)
    """存储答案的来源标识，可以用来标记回答是通过哪种方式获取的（如图查询、向量查询等）"""
    messages: List[Dict] = field(default_factory=list)
    """用于记录子图日志信息"""
    cypher_query: str = field(default_factory=str)
    """存储编写好的cypher查询语句"""