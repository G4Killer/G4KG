# embedding.py
import os
import numpy as np
from typing import List
from langchain.embeddings.base import Embeddings
from openai import OpenAI

class AliyunEmbedding(Embeddings):
    def __init__(self,
                 api_key: str = os.getenv("DASHSCOPE_API_KEY"),
                 base_url: str = "https://dashscope.aliyuncs.com/compatible-mode/v1",
                 model: str = "text-embedding-v3",
                 dimensions: int = 1024):
        """
        初始化阿里云嵌入模型包装器
        """
        self.api_key = api_key or os.getenv("DASHSCOPE_API_KEY")
        self.base_url = base_url
        self.model = model
        self.dimensions = dimensions

        # 使用 OpenAI 客户端（阿里云的 Dashscope 服务支持 OpenAI 兼容接口）
        self.client = OpenAI(
            api_key=self.api_key,
            base_url=self.base_url
        )

    def generate_embeddings(self, texts: List[str]) -> np.ndarray:
        """
        对多个文本生成嵌入向量，返回 numpy 数组
        """
        response = self.client.embeddings.create(
            model=self.model,
            input=texts,
            dimensions=self.dimensions,
            encoding_format="float"
        )
        # 假设返回结果中每个数据的嵌入存放在 data.embedding 字段中
        embeddings = [data.embedding for data in response.data]
        return np.array(embeddings)

    def embed_query(self, text: str) -> List[float]:
        """
        对单个查询文本生成嵌入，返回 List[float]
        """
        return self.generate_embeddings([text])[0].tolist()

    def embed_documents(self, texts: List[str]) -> List[List[float]]:
        """
        对多个文档生成嵌入，返回 List[List[float]]
        """
        return self.generate_embeddings(texts).tolist()
