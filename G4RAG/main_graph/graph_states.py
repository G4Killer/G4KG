from dataclasses import dataclass, field
from typing import Annotated, Literal, TypedDict, List
from langchain_core.messages import AnyMessage
from langgraph.graph import add_messages

# InputState 仅需要消息字段，因为不再涉及复杂的状态
@dataclass(kw_only=True)
class InputState:
    """Represents the input state for the agent."""
    messages: Annotated[list[AnyMessage], add_messages]

class Router(TypedDict):
    """Classify user query."""
    logic: str
    type: Literal["KG_query", "more-info", "general", "G4KG-General"]

# 简化后的 AgentState 只保留需要的字段
@dataclass(kw_only=True)
class AgentState(InputState):
    """State of the retrieval graph / agent."""
    router: Router = field(default_factory=lambda: Router(type="general", logic=""))
    """The router's classification of the user's query."""
    processed_query: str = field(default_factory=lambda: "")
    """The final processed user's question"""
    documents: list[dict] = field(default_factory=list)
    """Populated by the retriever. This is a list of documents that the agent can reference."""
    answer_source: list[str] = field(default_factory=list)
    """New field for path tracking."""
    cypher_query: str = field(default_factory=str)
    """存储编写好的cypher查询语句"""
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
