from langchain.prompts.example_selector import SemanticSimilarityExampleSelector
from langchain.prompts import FewShotPromptTemplate, PromptTemplate

from utils.vectorstore_config import get_vectorstore_and_client

vectorstore, _ = get_vectorstore_and_client(index_name="CypherExamples")

example_selector = SemanticSimilarityExampleSelector(
    vectorstore=vectorstore,
    k=3,
)

query = "Which genes are associated with Breast Cancer?"
examples = example_selector.select_examples({"text": query})

print("🔍 Example Selector 原始输出：", examples)

# ✅ 强制解析数据，看看 `page_content` 是否存在
cleaned_examples = [{k: e[k] for k in ["page_content"] if k in e} for e in examples]

print("🔍 修正后 Example Selector 结果：", cleaned_examples)
