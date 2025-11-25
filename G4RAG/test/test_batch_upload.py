# file: src/test_batch_upload.py

import os
from src.weaviate_engine import WeaviateEngine
from src.embedding import AliyunEmbedding
from src.batch_upload import batch_upload_txt_files

def test_batch_upload():
    print("========== 测试开始 ==========")

    # 配置 Weaviate 服务器地址和集合名称
    weaviate_host = "10.201.1.80"  # 替换为你的 Weaviate 服务器地址
    class_name = "KGDocument"
    print(f"初始化 WeaviateEngine，服务器地址: {weaviate_host}, 集合名称: {class_name}")
    # 注意：这里传入 API Key，用于认证
    engine = WeaviateEngine(host=weaviate_host, class_name=class_name, api_key="test-secret-key")

    # 初始化阿里云 Embedding 模型
    print("初始化阿里云 Embedding 模型...")
    embedding_model = AliyunEmbedding(api_key=os.getenv("DASHSCOPE_API_KEY"))

    # 指定存放测试 TXT 文件的目录
    data_dir = os.path.join(os.getcwd(), "data_test")
    print(f"开始批量上传目录：{data_dir} 下的 TXT 文件")

    # 调用批量上传函数
    batch_upload_txt_files(data_dir, engine, embedding_model)

    print("批量上传结束。")
    print("========== 测试结束 ==========")

if __name__ == "__main__":
    test_batch_upload()
