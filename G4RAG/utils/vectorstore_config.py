# vectorstore_config.py
import weaviate
from weaviate import WeaviateClient
from weaviate.auth import AuthApiKey
from langchain_weaviate import WeaviateVectorStore
from weaviate.config import AdditionalConfig, Timeout

from src.embedding import AliyunEmbedding
from utils.utils import config

# 从配置文件中加载 Weaviate 连接信息
WEAVIATE_HTTP_HOST = config["weaviate"]["http_host"]
WEAVIATE_HTTP_PORT = config["weaviate"]["http_port"]
WEAVIATE_HTTP_SECURE = config["weaviate"]["http_secure"]
WEAVIATE_GRPC_HOST = config["weaviate"]["grpc_host"]
WEAVIATE_GRPC_PORT = config["weaviate"]["grpc_port"]
WEAVIATE_GRPC_SECURE = config["weaviate"]["grpc_secure"]
WEAVIATE_API = config["weaviate"]["auth_api_key"]

# 定义全局变量来缓存 client 和 vectorstore
_global_clients = {}
_global_vectorstores = {}

def get_vectorstore_and_client(
    index_name: str = None,
    text_key: str = "text",
    embedding_model=None
) -> tuple[WeaviateVectorStore, WeaviateClient]:
    """
    获取一个 WeaviateVectorStore 实例，并支持自定义参数：
    - index_name: 使用的集合名（默认从配置文件读取）
    - text_key: 需要存储/检索的字段（默认为 "text"）
    - embedding_model: 允许传入不同的嵌入模型（默认使用 AliyunEmbedding）

    返回:
    - WeaviateVectorStore 实例
    - WeaviateClient 实例
    """

    global _global_clients, _global_vectorstores

    # 如果未提供 index_name，则默认从配置文件读取
    if index_name is None:
        index_name = config["vectorstore"]["index_name"]

    # 确保不同的 index_name 使用不同的 Weaviate 连接实例
    if index_name not in _global_clients:
        _global_clients[index_name] = weaviate.connect_to_custom(
            http_host=WEAVIATE_HTTP_HOST,
            http_port=WEAVIATE_HTTP_PORT,
            http_secure=WEAVIATE_HTTP_SECURE,
            grpc_host=WEAVIATE_GRPC_HOST,
            grpc_port=WEAVIATE_GRPC_PORT,
            grpc_secure=WEAVIATE_GRPC_SECURE,
            auth_credentials=AuthApiKey(WEAVIATE_API),
            additional_config=AdditionalConfig(
                timeout=Timeout(
                    init=60,        # 增加初始化超时时间
                    query=120,      # 增加查询超时时间到2分钟
                    insert=180,     # 增加插入超时时间
                    batch=180,      # 增加批处理超时时间
                    get=60,         # 增加获取数据超时时间
                    search=120,     # 增加搜索超时时间
                    startup=60      # 增加启动超时时间
                )
            )
        )
        print(f"✅ Connected to Weaviate - Index: {index_name}")

    client = _global_clients[index_name]

    # 如果 vectorstore 未创建，则初始化
    if index_name not in _global_vectorstores:
        if embedding_model is None:
            embedding_model = AliyunEmbedding()  # 默认使用 AliyunEmbedding

        _global_vectorstores[index_name] = WeaviateVectorStore(
            client=client,
            index_name=index_name,  # ✅ 这里确保可以动态指定集合
            text_key=text_key,  # ✅ 这里允许动态指定文本字段
            attributes=[],
            embedding=embedding_model
        )

    return _global_vectorstores[index_name], client


def close_weaviate():
    """
    统一关闭所有全局 Weaviate 连接；可在程序完全结束时调用。
    """
    global _global_clients, _global_vectorstores
    for index_name, client in _global_clients.items():
        if client is not None:
            print(f"🔻 Closing Weaviate connection for index: {index_name}")
            client.close()
    _global_clients.clear()
    _global_vectorstores.clear()
