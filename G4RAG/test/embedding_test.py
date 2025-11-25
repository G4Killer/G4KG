import os
from src.embedding import AliyunEmbedding

def main():
    # 初始化 AliyunEmbedding 模型
    # 如果环境变量已设置，可不传 api_key；否则请在此处传入你的 API Key
    embedding_model = AliyunEmbedding(api_key=os.getenv("DASHSCOPE_API_KEY"))

    # 测试单个文本的嵌入
    test_text = "衣服的质量杠杠的，很漂亮，不枉我等了这么久啊，喜欢，以后还来这里买"
    try:
        embedding = embedding_model.embed_query(test_text)
        print("单个文本嵌入结果:")
        print(embedding)
    except Exception as e:
        print("调用 embed_query 出现异常:", e)

    # 测试多个文本的嵌入
    test_texts = [
        "衣服的质量杠杠的，很漂亮，不枉我等了这么久啊，喜欢，以后还来这里买",
        "这家店的服务态度非常好，下次还会光顾。"
    ]
    try:
        embeddings = embedding_model.embed_documents(test_texts)
        print("\n多个文本嵌入结果:")
        for i, emb in enumerate(embeddings):
            print(f"文本 {i + 1}:", emb)
    except Exception as e:
        print("调用 embed_documents 出现异常:", e)

    # 测试文件嵌入
    # 定义待测试的文件路径，使用原始字符串以防止转义字符问题
    file_path = r"C:\Work\Pycharm Code\G4RAG\data\drug_to_disease_text.txt"

    try:
        # 读取文件内容，每一行作为一个待嵌入的文本
        with open(file_path, 'r', encoding='utf-8') as f:
            texts = f.readlines()
        texts = [line.strip() for line in texts if line.strip()]

        # 生成嵌入向量
        embeddings = embedding_model.embed_documents(texts)
        print("\n文件中每行文本的嵌入结果:")
        for i, emb in enumerate(embeddings):
            print(f"文本 {i + 1}:", emb)
    except Exception as e:
        print("调用文件嵌入出现异常:", e)

if __name__ == "__main__":
    main()
