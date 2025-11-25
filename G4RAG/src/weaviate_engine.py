import weaviate

from weaviate.auth import AuthApiKey

def check_collection_exists(client: weaviate.WeaviateClient, collection_name: str) -> bool:
    """
    检查集合是否存在
    :param client: Weaviate 客户端
    :param collection_name: 集合名称
    :return: True 或 False
    """
    try:
        collections = client.collections.list_all()
        return collection_name in collections
    except Exception as e:
        print(f"检查集合异常: {e}")
        return False

def create_collection(client: weaviate.WeaviateClient, collection_name: str):
    """
    创建集合，并添加 NodeType 属性
    """
    collection_obj = {
        "class": collection_name,
        "description": "存储知识图谱拼接后的文本及其向量",
        "vectorizer": "none",  # 假设你会上传自己的向量
        "properties": [
            {
                "name": "text",
                "description": "用于向量化计算的文本",
                "dataType": ["text"],
                "tokenization": "word",
                "indexFilterable": True,
                "indexSearchable": True
            },
            {
                "name": "NodeType",  # ✅ 新增 NodeType 字段
                "description": "存储节点类型，如 Disease, Gene, Protein",
                "dataType": ["string"],  # 存储文本类型
                "indexFilterable": True,  # 允许按 NodeType 筛选
                "indexSearchable": True   # 允许按 NodeType 进行查询
            }
        ]
    }
    try:
        client.collections.create_from_dict(collection_obj)
        print(f"创建集合 '{collection_name}' 成功，并已添加 NodeType 字段。")
    except weaviate.exceptions.UnexpectedStatusCodeException as e:
        print(f"创建集合异常: {e}")


class WeaviateEngine:
    def __init__(self,
                 host: str = "10.201.1.80",
                 http_port: int = 8080,
                 class_name: str = "KGDocument_512",
                 api_key: str = "test-secret-key"):
        """
        初始化 Weaviate 客户端，并检查/创建集合（class）

        :param host: Weaviate 服务器地址（不包含协议前缀）
        :param http_port: HTTP 端口号
        :param class_name: 数据集合名称，默认 "KGDocument"
        :param api_key: 用于认证的 API Key（必须提供）
        """
        self.client = weaviate.connect_to_custom(
            skip_init_checks=False,
            http_host=host,
            http_port=http_port,
            http_secure=False,
            grpc_host="10.201.1.80",    # 根据实际情况调整
            grpc_port=50051,          # 根据实际情况调整
            grpc_secure=False,        # 根据实际情况调整
            auth_credentials=AuthApiKey(api_key)
        )
        self.class_name = class_name

        if not check_collection_exists(self.client, self.class_name):
            create_collection(self.client, self.class_name)
        else:
            print(f"集合 {self.class_name} 已存在。")

    def add_document(self, text: str, vector: list, node_type: str):
        """
        将单条数据写入 Weaviate，并存储 NodeType
        """
        try:
            collection = self.client.collections.get(self.class_name)
            properties = {
                "text": text,
                "NodeType": node_type  # ✅ 添加 NodeType
            }
            uuid = collection.data.insert(properties=properties, vector=vector)
            print(f"文档添加成功，uuid: {uuid}, NodeType: {node_type}")
        except Exception as e:
            print("添加文档异常:", e)
            raise
