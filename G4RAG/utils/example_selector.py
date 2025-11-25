from langchain.prompts import FewShotPromptTemplate, PromptTemplate
from langchain.prompts.example_selector import SemanticSimilarityExampleSelector
from langchain_weaviate.vectorstores import WeaviateVectorStore
from src.embedding import AliyunEmbedding
from utils.MyExampleSelector import MyExampleSelector
from utils.vectorstore_config import get_vectorstore_and_client

vectorstore, client = get_vectorstore_and_client(index_name="CypherExamples")

# **初始化 OpenAI 嵌入（你可以替换为 AliyunEmbedding）**
embedding_model = AliyunEmbedding()

# **实例化 `ExampleSelector`**
# example_selector = SemanticSimilarityExampleSelector(
#    vectorstore=vectorstore,
#    k=3
#)

example_selector = MyExampleSelector(
    vectorstore=vectorstore,
    k=3
)

# **示例模板**
example_prompt = PromptTemplate(
    input_variables=["page_content"],
    template="{page_content}"
)

# **创建 `FewShotPromptTemplate`，融合 `GENERATE_CYPHER_PROMPT`**
few_shot_prompt = FewShotPromptTemplate(
    example_selector=example_selector,
    example_prompt=example_prompt,
    prefix="""You are an expert in generating Cypher queries for a Knowledge Graph.

### **Knowledge Graph Schema**
{{schema}}

### **Refined Information**
- **Entities:** {{refined_entities}}
- **Relationships:** {{refined_relationships}}

### **Instructions**
- Generate a precise Cypher query to retrieve the requested knowledge.
- Ensure the Cypher syntax is valid.
- Return the appropriate entities or relationships based on the query intent.
- **Return only the most relevant TOP-5 results** using `LIMIT 5`.
- **Return only a valid Cypher statement** with no triple backticks, no code block formatting.

### **Reference Examples**
""",
    suffix="User Query: {{question}}\nGenerated Cypher:",
    input_variables=["schema", "question", "refined_entities", "refined_relationships"],
    template_format="jinja2"
)
