import weaviate
from weaviate.auth import AuthApiKey
from weaviate.classes.query import Filter

# 连接 Weaviate
client = weaviate.connect_to_custom(
    http_host="10.201.1.80",
    http_port=8080,
    http_secure=False,
    grpc_host="10.201.1.80",
    grpc_port=50051,
    grpc_secure=False,
    auth_credentials=AuthApiKey("test-secret-key")
)

# 获取 Weaviate Collection
collection = client.collections.get("KGDocument")

# ✅ 统计 nodeType 为 Gene, GO, Pathway, Protein 的数据量
node_types = ["Disease", "Drug", "G4", "Gene", "GO", "Pathway", "Protein"]
counts = {}

for node_type in node_types:
    response = collection.aggregate.over_all(
        filters=Filter.by_property("nodeType").equal(node_type)  # ✅ 过滤 nodeType
    )
    counts[node_type] = response.total_count  # ✅ 获取匹配数据的总数

# ✅ 输出统计结果
print("📊 数据统计结果：")
for node_type, count in counts.items():
    print(f"🔹 {node_type}: {count} 条数据")

# 关闭 Weaviate 连接
client.close()
