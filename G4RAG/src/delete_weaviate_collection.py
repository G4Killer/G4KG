import weaviate
from weaviate.auth import AuthApiKey

# **连接 Weaviate**
client = weaviate.connect_to_custom(
    http_host="10.201.1.80",
    http_port=8080,
    http_secure=False,
    grpc_host="10.201.1.80",
    grpc_port=50051,
    grpc_secure=False,
    auth_credentials=AuthApiKey("test-secret-key")
)

client.collections.delete("CypherExamples")
# **关闭 Weaviate 连接**
client.close()
print("🔒 Weaviate 连接已关闭。")
