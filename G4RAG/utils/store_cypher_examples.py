import weaviate
from weaviate.auth import AuthApiKey
from langchain_weaviate.vectorstores import WeaviateVectorStore
from prompts.prompt_examples import cypher_examples
from src.embedding import AliyunEmbedding

# 连接 Weaviate 客户端
client = weaviate.connect_to_custom(
    http_host="10.201.1.80",
    http_port=8080,
    http_secure=False,
    grpc_host="10.201.1.80",
    grpc_port=50051,
    grpc_secure=False,
    auth_credentials=AuthApiKey("test-secret-key")
)
print("✅ Weaviate 是否就绪：", client.is_ready())

# 初始化 OpenAI 嵌入（你可以替换为自己的 AliyunEmbedding）
embedding_model = AliyunEmbedding(dimensions=1024)

# 初始化 Weaviate VectorStore
vectorstore = WeaviateVectorStore(
    client=client,
    index_name="CypherExamples",
    text_key="text",
    attributes=[],
    embedding=embedding_model
)

# **存入 Weaviate**
texts = [f"Question: {ex['question']}\nCypher: {ex['cypher']}" for ex in cypher_examples]

# 🚀 **按批次存入 Weaviate，确保 batch_size ≤ 10**
BATCH_SIZE = 10
for i in range(0, len(texts), BATCH_SIZE):
    batch = texts[i:i + BATCH_SIZE]
    vectorstore.add_texts(batch)  # 仅传入 batch_size ≤ 10 的数据

client.close()

print("✅ 所有 Cypher 示例已存入 Weaviate！")
