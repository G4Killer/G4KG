# file: src/run_batch_upload.py

import os
from src.weaviate_engine import WeaviateEngine
from src.embedding import AliyunEmbedding
from src.batch_upload import batch_upload_txt_files


def run_batch_upload():
    print("========== Weaviate 批量上传开始 ==========")

    # 配置 Weaviate 服务器地址和集合名称
    weaviate_host = "10.201.1.80"  # 替换为你的 Weaviate 服务器地址
    class_name = "KGDocument"
    print(f"初始化 WeaviateEngine，服务器地址: {weaviate_host}, 集合名称: {class_name}")

    # 传入 API Key 进行认证
    engine = WeaviateEngine(host=weaviate_host, class_name=class_name, api_key="test-secret-key")

    # 初始化阿里云 Embedding 模型
    print("初始化阿里云 Embedding 模型...")
    embedding_model = AliyunEmbedding(api_key=os.getenv("DASHSCOPE_API_KEY"))

    # 设定 `data/Node/` 目录为数据源
    data_dir = 'C:/Work/Pycharm Code/G4RAG/data/rest'

    if not os.path.exists(data_dir):
        print(f"错误: 目录 {data_dir} 不存在，请检查路径是否正确！")
        return

    print(f"开始批量上传目录：{data_dir} 下的 TXT 文件")

    # 调用批量上传函数
    batch_upload_txt_files(data_dir, engine, embedding_model)

    print("批量上传结束。")
    print("========== Weaviate 批量上传完成 ==========")


if __name__ == "__main__":
    run_batch_upload()
