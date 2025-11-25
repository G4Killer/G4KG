import os

from neo4j import GraphDatabase

# 配置 Neo4j 连接信息
NEO4J_URI = "bolt://10.201.1.80:7687"  # 修改为你的 Neo4j URI
NEO4J_USER = "neo4j"
NEO4J_PASSWORD = "12345678"  # 修改为你的 Neo4j 密码

# 创建一个 Neo4j 驱动实例
driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))

def extract_node_data():
    """从 Neo4j 中提取节点数据并拼接文本"""
    with driver.session() as session:
        # 查询节点数据
        query = """
        MATCH (d:Drug)
        RETURN d.Name AS Name, d.DrugId AS DrugId
        """
        drug_data = list(session.run(query))

        query = """
        MATCH (d:Disease)
        RETURN d.DiseaseId AS DiseaseId, d.DiseaseName AS DiseaseName, d.DiseaseSynonyms AS DiseaseSynonyms, d.DiseaseSemanticType AS DiseaseSemanticType
        """
        disease_data = list(session.run(query))

        query = """
        MATCH (g:Gene)
        RETURN g.GeneSymbol AS GeneSymbol, g.FullName AS FullName, g.Synonyms AS Synonyms, g.GeneType AS GeneType, g.GeneId AS GeneId
        """
        gene_data = list(session.run(query))

        query = """
        MATCH (go:GO)
        RETURN go.GoTermName AS GoTermName, go.GoTermType AS GoTermType, go.GoId AS GoId
        """
        go_data = list(session.run(query))

        query = """
        MATCH (p:Pathway)
        RETURN p.PathwayName AS PathwayName, p.PathwayId AS PathwayId
        """
        pathway_data = list(session.run(query))

        query = """
        MATCH (g4:G4)
        RETURN g4.G4Id AS G4Id, g4.Strand AS Strand, g4.Chr AS Chr, g4.Location AS Location, g4.SampleName AS SampleName
        """
        g4_data = list(session.run(query))

        query = """
        MATCH (p:Protein)
        RETURN p.FullName AS FullName, p.EntryName AS EntryName, p.ProteinId AS ProteinId
        """
        protein_data = list(session.run(query))

        # 返回提取的数据
        return drug_data, disease_data, gene_data, go_data, pathway_data, g4_data, protein_data


def generate_node_text(data, node_type):
    """将提取的节点数据生成文本"""
    texts = []
    for record in data:
        if node_type == "Drug":
            text = f"NodeType: Drug | Name: {record['Name']} | DrugId: {record['DrugId']}"
        elif node_type == "Disease":
            # 1️⃣ 获取 DiseaseSynonyms，并确保不会是 None
            synonyms = record.get('DiseaseSynonyms', "")
            # 2️⃣ 处理空值情况
            if not synonyms:  # None 或 空字符串
                synonyms_text = "N/A"  # 方法 1：使用 N/A 代替
            else:
                if isinstance(synonyms, str):
                    synonyms_list = synonyms.split('|')  # 处理字符串
                elif isinstance(synonyms, list):
                    synonyms_list = synonyms  # 直接使用已有列表
                else:
                    synonyms_list = []  # 处理其他异常情况
                # 3️⃣ 规范化文本，确保每个词都干净
                synonyms_text = ' ~ '.join([synonym.strip() for synonym in synonyms_list])
            text = f"NodeType: Disease | DiseaseId: {record['DiseaseId']} | DiseaseName: {record['DiseaseName']} | DiseaseSynonyms: {synonyms_text} | DiseaseSemanticType: {record['DiseaseSemanticType']}"
        elif node_type == "Gene":
            # 处理 Synonyms 字段，假设以 | 分隔
            synonyms = record['Synonyms']
            # 处理存储的形式，拆分并重新拼接
            synonyms_list = synonyms.split('|') if isinstance(synonyms, str) else synonyms
            # 清理掉任何额外的空白字符
            synonyms_text = ' ~ '.join([synonym.strip() for synonym in synonyms_list])
            text = f"NodeType: Gene | GeneSymbol: {record['GeneSymbol']} | FullName: {record['FullName']} | Synonyms: {synonyms_text} | GeneType: {record['GeneType']} | GeneId: {record['GeneId']}"
        elif node_type == "GO":
            text = f"NodeType: GO | GoTermName: {record['GoTermName']} | GoTermType: {record['GoTermType']} | GoId: {record['GoId']}"
        elif node_type == "Pathway":
            text = f"NodeType: Pathway | PathwayName: {record['PathwayName']} | PathwayId: {record['PathwayId']}"
        elif node_type == "G4":
            text = f"NodeType: G4 | G4Id: {record['G4Id']} | Strand: {record['Strand']} | Chr: {record['Chr']} | Location: {record['Location']} | SampleName: {record['SampleName']}"
        elif node_type == "Protein":
            text = f"NodeType: Protein | FullName: {record['FullName']} | EntryName: {record['EntryName']} | ProteinId: {record['ProteinId']}"
        texts.append(text)
    return texts


def extract_relationship_data():
    """从 Neo4j 中提取关系数据并拼接文本"""
    with driver.session() as session:
        # 查询关系数据
        query = """
        MATCH (d:Drug)-[r:DrugToProtein]->(p:Protein)
        RETURN DISTINCT r.RelationName AS RelationName, r.Action AS Action
        """
        drug_to_protein_data = list(session.run(query))

        query = """
        MATCH (d:Drug)-[r:DrugToDrug]->(d2:Drug)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        drug_to_drug_data = list(session.run(query))

        query = """
        MATCH (d:Drug)-[r:DrugToDisease]->(ds:Disease)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        drug_to_disease_data = list(session.run(query))

        query = """
        MATCH (d:Disease)-[r:DiseaseToDisease]->(d2:Disease)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        disease_to_disease_data = list(session.run(query))

        query = """
        MATCH (g:Gene)-[r:GeneToProtein]->(p:Protein)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        gene_to_protein_data = list(session.run(query))

        query = """
        MATCH (g:Gene)-[r:GeneToGO]->(go:GO)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        gene_to_go_data = list(session.run(query))

        query = """
        MATCH (g:Gene)-[r:GeneToDisease]->(d:Disease)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        gene_to_disease_data = list(session.run(query))

        query = """
        MATCH (g:Gene)-[r:GeneToPathway]->(p:Pathway)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        gene_to_pathway_data = list(session.run(query))

        query = """
        MATCH (g:Gene)-[r:GeneToGene]->(g2:Gene)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        gene_to_gene_data = list(session.run(query))

        query = """
        MATCH (g:GO)-[r:GOToGO]->(g2:GO)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        go_to_go_data = list(session.run(query))

        query = """
        MATCH (p:Pathway)-[r:PathwayToPathway]->(p2:Pathway)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        pathway_to_pathway_data = list(session.run(query))

        query = """
        MATCH (g:G4)-[r:G4ToGene]->(ge:Gene)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        g4_to_gene_data = list(session.run(query))

        query = """
        MATCH (g:G4)-[r:G4ToDisease]->(d:Disease)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        g4_to_disease_data = list(session.run(query))

        query = """
        MATCH (p:Protein)-[r:ProteinToPathway]->(pa:Pathway)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        protein_to_pathway_data = list(session.run(query))

        query = """
        MATCH (p:Protein)-[r:ProteinToGO]->(g:GO)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        protein_to_go_data = list(session.run(query))

        query = """
        MATCH (p:Protein)-[r:ProteinToProtein]->(p2:Protein)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        protein_to_protein_data = list(session.run(query))

        query = """
        MATCH (p:Protein)-[r:ProteinToG4]->(g:G4)
        RETURN DISTINCT r.RelationName AS RelationName
        """
        protein_to_g4_data = list(session.run(query))

        query = """
        MATCH (p:Protein)-[r:ProteinToGene]->(g:Gene)
        RETURN DISTINCT r.RelationName AS RelationName, r.Tissue AS Tissue
        """
        protein_to_gene_data = list(session.run(query))

        # 返回提取的数据
        return drug_to_protein_data, drug_to_drug_data, drug_to_disease_data, disease_to_disease_data, gene_to_protein_data, gene_to_go_data, gene_to_disease_data, gene_to_pathway_data, gene_to_gene_data, go_to_go_data, pathway_to_pathway_data, g4_to_gene_data, g4_to_disease_data, protein_to_pathway_data, protein_to_go_data, protein_to_protein_data, protein_to_g4_data, protein_to_gene_data


def generate_relationship_text(data, relationship_type):
    """将提取的关系数据生成文本"""
    texts = []
    for record in data:
        if relationship_type == "ProteinToGene":
            # 使用 get() 方法来避免 KeyError
            tissue = record.get('Tissue', 'N/A')  # 如果没有 Tissue 属性，则使用 'N/A'
            text = f"RelationshipType: ProteinToGene | RelationName: {record['RelationName']} | Tissue: {tissue}"
        elif relationship_type == "DrugToProtein":
            text = f"RelationshipType: DrugToProtein | RelationName: {record['RelationName']} | Action: {record['Action']}"
        elif relationship_type == "DrugToDrug":
            text = f"RelationshipType: DrugToDrug | RelationName: {record['RelationName']}"
        elif relationship_type == "DrugToDisease":
            text = f"RelationshipType: DrugToDisease | RelationName: {record['RelationName']}"
        elif relationship_type == "DiseaseToDisease":
            text = f"RelationshipType: DiseaseToDisease | RelationName: {record['RelationName']}"
        elif relationship_type == "GeneToProtein":
            text = f"RelationshipType: GeneToProtein | RelationName: {record['RelationName']}"
        elif relationship_type == "GeneToGO":
            text = f"RelationshipType: GeneToGO | RelationName: {record['RelationName']}"
        elif relationship_type == "GeneToDisease":
            text = f"RelationshipType: GeneToDisease | RelationName: {record['RelationName']}"
        elif relationship_type == "GeneToPathway":
            text = f"RelationshipType: GeneToPathway | RelationName: {record['RelationName']}"
        elif relationship_type == "GeneToGene":
            text = f"RelationshipType: GeneToGene | RelationName: {record['RelationName']}"
        elif relationship_type == "GOToGO":
            text = f"RelationshipType: GOToGO | RelationName: {record['RelationName']}"
        elif relationship_type == "PathwayToPathway":
            text = f"RelationshipType: PathwayToPathway | RelationName: {record['RelationName']}"
        elif relationship_type == "G4ToGene":
            text = f"RelationshipType: G4ToGene | RelationName: {record['RelationName']}"
        elif relationship_type == "G4ToDisease":
            text = f"RelationshipType: G4ToDisease | RelationName: {record['RelationName']}"
        elif relationship_type == "ProteinToPathway":
            text = f"RelationshipType: ProteinToPathway | RelationName: {record['RelationName']}"
        elif relationship_type == "ProteinToGO":
            text = f"RelationshipType: ProteinToGO | RelationName: {record['RelationName']}"
        elif relationship_type == "ProteinToProtein":
            text = f"RelationshipType: ProteinToProtein | RelationName: {record['RelationName']}"
        elif relationship_type == "ProteinToG4":
            text = f"RelationshipType: ProteinToG4 | RelationName: {record['RelationName']}"
        elif relationship_type == "ProteinToGene":
            text = f"RelationshipType: ProteinToGene | RelationName: {record['RelationName']} | Tissue: {record.get('Tissue', 'N/A')}"
        texts.append(text)
    return texts


def save_text_to_file(texts, filename, save_path):
    """将拼接的文本保存到指定路径的文件"""
    os.makedirs(save_path, exist_ok=True)  # 确保目标目录存在
    file_path = os.path.join(save_path, filename)  # 组合完整的文件路径
    with open(file_path, 'w', encoding='utf-8') as file:  # 使用utf-8编码
        for text in texts:
            file.write(text + '\n')


def main():
    # 提取节点数据并生成文本
    drug_data, disease_data, gene_data, go_data, pathway_data, g4_data, protein_data = extract_node_data()

    # 拼接节点文本
    drug_text = generate_node_text(drug_data, "Drug")
    disease_text = generate_node_text(disease_data, "Disease")
    gene_text = generate_node_text(gene_data, "Gene")
    go_text = generate_node_text(go_data, "GO")
    pathway_text = generate_node_text(pathway_data, "Pathway")
    g4_text = generate_node_text(g4_data, "G4")
    protein_text = generate_node_text(protein_data, "Protein")

    save_path_node = 'C:/Work/Pycharm Code/G4RAG/data/Node'
    # 保存节点文本到文件
    save_text_to_file(drug_text, 'drug_text.txt', save_path_node)
    save_text_to_file(disease_text, 'disease_text.txt', save_path_node)
    save_text_to_file(gene_text, 'gene_text.txt', save_path_node)
    save_text_to_file(go_text, 'go_text.txt', save_path_node)
    save_text_to_file(pathway_text, 'pathway_text.txt', save_path_node)
    save_text_to_file(g4_text, 'g4_text.txt', save_path_node)
    save_text_to_file(protein_text, 'protein_text.txt', save_path_node)

    # 提取关系数据并生成文本
    drug_to_protein_data, drug_to_drug_data, drug_to_disease_data, disease_to_disease_data, gene_to_protein_data, gene_to_go_data, gene_to_disease_data, gene_to_pathway_data, gene_to_gene_data, go_to_go_data, pathway_to_pathway_data, g4_to_gene_data, g4_to_disease_data, protein_to_pathway_data, protein_to_go_data, protein_to_protein_data, protein_to_g4_data, protein_to_gene_data = extract_relationship_data()

    # 拼接关系文本
    drug_to_protein_text = generate_relationship_text(drug_to_protein_data, "DrugToProtein")
    drug_to_drug_text = generate_relationship_text(drug_to_drug_data, "DrugToDrug")
    drug_to_disease_text = generate_relationship_text(drug_to_disease_data, "DrugToDisease")
    disease_to_disease_text = generate_relationship_text(disease_to_disease_data,"DiseaseToDisease")
    gene_to_protein_text = generate_relationship_text(gene_to_protein_data,"GeneToProtein")
    gene_to_go_text = generate_relationship_text(gene_to_go_data,"GeneToGO")
    gene_to_disease_text = generate_relationship_text(gene_to_disease_data,"GeneToDisease")
    gene_to_pathway_text = generate_relationship_text(gene_to_pathway_data,"GeneToPathway")
    gene_to_gene_text = generate_relationship_text(gene_to_gene_data,"GeneToGene")
    go_to_go_text = generate_relationship_text(go_to_go_data,"GOToGO")
    pathway_to_pathway_text = generate_relationship_text(pathway_to_pathway_data,"PathwayToPathway")
    g4_to_gene_text = generate_relationship_text(g4_to_gene_data,"G4ToGene")
    g4_to_disease_text = generate_relationship_text(g4_to_disease_data,"G4ToDisease")
    protein_to_pathway_text = generate_relationship_text(protein_to_pathway_data,"ProteinToPathway")
    protein_to_go_text = generate_relationship_text(protein_to_go_data,"ProteinToGO")
    protein_to_protein_text = generate_relationship_text(protein_to_protein_data,"ProteinToProtein")
    protein_to_g4_text = generate_relationship_text(protein_to_g4_data,"ProteinToG4")
    protein_to_gene_text = generate_relationship_text(protein_to_gene_data,"ProteinToGene")

    save_path_relationship = 'C:/Work/Pycharm Code/G4RAG/data/Relationship'
    # 保存关系文本到文件
    save_text_to_file(drug_to_protein_text, 'drug_to_protein_text.txt', save_path_relationship)
    save_text_to_file(drug_to_drug_text, 'drug_to_drug_text.txt', save_path_relationship)
    save_text_to_file(drug_to_disease_text, 'drug_to_disease_text.txt', save_path_relationship)
    save_text_to_file(disease_to_disease_text, 'disease_to_disease_text.txt', save_path_relationship)
    save_text_to_file(gene_to_protein_text, 'gene_to_protein_text.txt', save_path_relationship)
    save_text_to_file(gene_to_go_text, 'gene_to_go_text.txt', save_path_relationship)
    save_text_to_file(gene_to_disease_text, 'gene_to_disease_text.txt', save_path_relationship)
    save_text_to_file(gene_to_pathway_text, 'gene_to_pathway_text.txt', save_path_relationship)
    save_text_to_file(gene_to_gene_text, 'gene_to_gene_text.txt', save_path_relationship)
    save_text_to_file(go_to_go_text, 'go_to_go_text.txt', save_path_relationship)
    save_text_to_file(pathway_to_pathway_text, 'pathway_to_pathway_text.txt', save_path_relationship)
    save_text_to_file(g4_to_gene_text, 'g4_to_gene_text.txt', save_path_relationship)
    save_text_to_file(g4_to_disease_text, 'g4_to_disease_text.txt', save_path_relationship)
    save_text_to_file(protein_to_pathway_text, 'protein_to_pathway_text.txt', save_path_relationship)
    save_text_to_file(protein_to_go_text, 'protein_to_go_text.txt', save_path_relationship)
    save_text_to_file(protein_to_protein_text, 'protein_to_protein_text.txt', save_path_relationship)
    save_text_to_file(protein_to_g4_text, 'protein_to_g4_text.txt', save_path_relationship)
    save_text_to_file(protein_to_gene_text, 'protein_to_gene_text.txt', save_path_relationship)


if __name__ == "__main__":
    main()
