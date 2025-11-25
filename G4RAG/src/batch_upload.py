import os
import glob
from src.weaviate_engine import WeaviateEngine
from src.embedding import AliyunEmbedding

def get_node_type_from_filename(filename):
    """
    从文件名解析 NodeType，例如：
    - "disease_data.txt" -> NodeType: "Disease"
    - "gene_info.txt" -> NodeType: "Gene"
    - "protein_list.txt" -> NodeType: "Protein"
    """
    base_name = os.path.basename(filename).lower()  # 只取文件名并统一转小写

    if "disease" in base_name:
        return "Disease"
    elif "gene" in base_name:
        return "Gene"
    elif "protein" in base_name:
        return "Protein"
    elif "drug" in base_name:
        return "Drug"
    elif "g4" in base_name:
        return "G4"
    elif "go" in base_name:
        return "GO"
    elif "pathway" in base_name:
        return "Pathway"
    return "Unknown"

def batch_upload_txt_files(data_dir: str, engine: WeaviateEngine, embedding_model: AliyunEmbedding, batch_size: int = 10):
    txt_files = glob.glob(os.path.join(data_dir, "*.txt"))
    if not txt_files:
        print(f"没有在 {data_dir} 中找到任何 txt 文件。")
        return

    collection = engine.client.collections.get(engine.class_name)

    with collection.batch.dynamic() as batch:
        for txt_file in txt_files:
            node_type = get_node_type_from_filename(txt_file)  # ✅ 动态解析 NodeType
            print(f"\n开始处理文件: {txt_file}，NodeType: {node_type}")

            try:
                with open(txt_file, 'r', encoding='utf-8') as f:
                    lines = [line.strip() for line in f if line.strip()]
                if not lines:
                    print(f"文件 {txt_file} 中没有有效内容，跳过。")
                    continue

                # 对数据进行分批处理
                for i in range(0, len(lines), batch_size):
                    batch_lines = lines[i:i+batch_size]
                    embeddings = embedding_model.embed_documents(batch_lines)
                    for text, vector in zip(batch_lines, embeddings):
                        print(f"正在导入文本: {text[:30]}..., 向量维度: {len(vector)}, NodeType: {node_type}")
                        batch.add_object(
                            properties={
                                "text": text,
                                "NodeType": node_type  # ✅ 确保每条数据存入 NodeType
                            },
                            vector=vector
                        )
                        if batch.number_errors > 10:
                            print("批量导入因错误过多而停止。")
                            break
                print(f"文件 {txt_file} 处理完毕。")
            except Exception as e:
                print(f"处理文件 {txt_file} 时出现异常：{e}")
