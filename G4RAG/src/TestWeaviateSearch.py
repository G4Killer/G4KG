import numpy as np
from utils.vectorstore_config import get_vectorstore_and_client
from src.embedding import AliyunEmbedding

# ✅ 获取 WeaviateVectorStore 和 client
vectorstore, client = get_vectorstore_and_client(index_name="KGDocument")

# ✅ 初始化嵌入模型
embedding_model = AliyunEmbedding()

# ✅ 需要测试的查询
query_text ="What is the pathway for the regulation of the pyruvate dehydrogenase (PDH) complex?"
# ✅ 计算查询向量
query_vector = embedding_model.embed_query(query_text)

# ✅ 确保向量正确生成
print("Query Vector Shape:", np.array(query_vector).shape)

# ✅ 使用 similarity_search_by_vector 进行向量搜索
response = vectorstore.similarity_search_with_score(query_text, k=5)
for idx, (doc, score) in enumerate(response):
    print(f"\n🔎 **Result {idx+1}**")
    print("Text:", doc.page_content)
    print("Score:", score)  # 余弦距离，越小越相似
    print("Metadata:", doc.metadata)

# ✅ 关闭 Weaviate 连接
client.close()
