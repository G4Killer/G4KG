from typing import Dict, Any, List
from langchain_core.example_selectors import SemanticSimilarityExampleSelector
from langchain_core.documents import Document


class MyExampleSelector(SemanticSimilarityExampleSelector):
    """
    自定义的 ExampleSelector，用 doc.page_content 来填充示例字典。
    这样就不会只返回 metadata 导致 `[{}, {}, {}]`，而是包含真正的文本内容。
    """

    def select_examples(self, input_variables: Dict[str, Any]) -> List[dict]:
        # 1. 获取查询文本，默认为从 input_variables["question"] 中获取
        query = input_variables.get("question", "")

        # 2. 调用 vectorstore 做相似度搜索
        docs = self.vectorstore.similarity_search(query, k=self.k)

        # 3. 返回示例列表，每个示例包含 "page_content"
        results = []
        for doc in docs:
            # 在这里，doc.page_content 就是 "Question: ...\nCypher: ..." 的文本
            results.append({"page_content": doc.page_content})

        return results
