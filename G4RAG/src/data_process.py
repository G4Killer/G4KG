input_file = r"C:\Work\Pycharm Code\G4RAG\data\Node\gene_text.txt"
output_file = r"C:\Work\Pycharm Code\G4RAG\data\Node\gene_text_filtered.txt"

# ✅ 读取原始文件并删除前 36050 行
with open(input_file, "r", encoding="utf-8") as infile:
    lines = infile.readlines()[36050:]  # 仅保留 36051 行及之后的数据

# ✅ 另存为新文件
with open(output_file, "w", encoding="utf-8") as outfile:
    outfile.writelines(lines)

print(f"✅ 处理完成，已保存到: {output_file}")
