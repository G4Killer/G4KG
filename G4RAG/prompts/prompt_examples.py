cypher_examples = [
    # 1. 单跳查询
    # ✅ ** Gene ↔ Disease **
    {
        "question": "Does gene MIR4467 have relationship with Alzheimer's disease.",
        "cypher": "MATCH (g:Gene {GeneSymbol: 'MIR4467'})-[:GeneToDisease]->(d:Disease {DiseaseName: 'Alzheimer\'s Disease'}) RETURN g.GeneSymbol, g.GeneId LIMIT 5"
    },
    {
        "question": "Which genes are related to Breast Cancer?",
        "cypher": "MATCH (g:Gene)-[:GeneToDisease {RelationName: 'Relate to'}]->(d:Disease {DiseaseName: 'Malignant neoplasm of breast'}) RETURN g.GeneSymbol, g.GeneId LIMIT 5"
    },
    {
        "question": "Which genes are related to Breast Cancer?",
        "cypher": "MATCH (g:Gene)-[:GeneToDisease {RelationName: 'Relate to'}]->(d:Disease {DiseaseName: 'Malignant neoplasm of breast'}) RETURN g.GeneSymbol, g.GeneId LIMIT 5"
    },
    # ✅ ** Gene ↔ GO **
    {
        "question": "Find GO terms that the gene TP53 is involved in.",
        "cypher": "MATCH (g:Gene {GeneSymbol: 'TP53'})-[:GeneToGO {RelationName: 'Involved in'}]->(go:GO) RETURN go.GoTermName, go.GoId LIMIT 5"
    },
    {
        "question": "Which GO terms are associated with the gene BRCA1?",
        "cypher": "MATCH (g:Gene {GeneSymbol: 'BRCA1'})-[:GeneToGO]->(go:GO) RETURN go.GoTermName, go.GoId LIMIT 5"
    },
    # ✅ ** Gene ↔ Pathway **
    {
        "question": "Find genes involved in the pathway 'Apoptosis'.",
        "cypher": "MATCH (g:Gene)-[:GeneToPathway {RelationName: 'Associate with'}]->(p:Pathway {PathwayName: 'Apoptosis'}) RETURN g.GeneSymbol, g.GeneId LIMIT 5"
    },
    # ✅ ** Gene ↔ Protein **
    {
        "question": "Find proteins coded by the gene TP53.",
        "cypher": "MATCH (g:Gene {GeneSymbol: 'TP53'})-[:GeneToProtein {RelationName: 'Code'}]->(p:Protein) RETURN p.FullName, p.ProteinId LIMIT 5"
    },
    # ✅ ** Gene ↔ Gene **
    {
        "question": "Find genes that have a synthetic lethality relationship with TP53.",
        "cypher": "MATCH (g1:Gene {GeneSymbol: 'TP53'})-[:GeneToGene {RelationName: 'Synthetic lethality'}]->(g2:Gene) RETURN g2.GeneSymbol, g2.GeneId LIMIT 5"
    },
    {
        "question": "Which genes have relationships with BRCA1?",
        "cypher": "MATCH (g1:Gene {GeneSymbol: 'BRCA1'})-[:GeneToGene]->(g2:Gene) RETURN g2.GeneSymbol, g2.GeneId LIMIT 5"
    },
    # ✅ ** Drug ↔ Protein **
    {
        "question": "Find drugs that target the protein EGFR.",
        "cypher": "MATCH (d:Drug)-[:DrugToProtein {RelationName: 'Target'}]->(p:Protein {EntryName: 'EGFR_HUMAN'}) RETURN d.Name, d.DrugId LIMIT 5"
    },
    # ✅ ** Drug ↔ Disease **
    {
        "question": "Find drugs that are used as indications for Lung Cancer.",
        "cypher": "MATCH (d:Drug)-[:DrugToDisease {RelationName: 'Indication'}]->(di:Disease {DiseaseName: 'Non-Small Cell Lung Carcinoma'}) RETURN d.Name, d.DrugId LIMIT 5"
    },
    {
        "question": "Find drugs that are contraindicated for Heart Disease.",
        "cypher": "MATCH (d:Drug)-[:DrugToDisease {RelationName: 'Contraindication'}]->(di:Disease {DiseaseName: 'Heart Diseases'}) RETURN d.Name, d.DrugId LIMIT 5"
    },
    # ✅ ** Drug ↔ Drug
    {
        "question": "Find drugs that interact with Aspirin.",
        "cypher": "MATCH (d1:Drug {Name: 'Acetylsalicylic acid'})-[:DrugToDrug {RelationName: 'Interact with'}]->(d2:Drug) RETURN d2.Name, d2.DrugId LIMIT 5"
    },
    # ✅ ** Protein ↔ GO **
    {
        "question": "Find GO terms that the protein P17752 is involved in.",
        "cypher": "MATCH (p:Protein {ProteinId: 'P17752'})-[:ProteinToGO {RelationName: 'Involved in'}]->(go:GO) RETURN go.GoTermName, go.GoId LIMIT 5"
    },
    # ✅ ** Protein ↔ Pathway **
    {
        "question": "Find pathways that involve the protein BRCA1.",
        "cypher": "MATCH (p:Protein {EntryName: 'BRCA1_HUMAN'})-[:ProteinToPathway {RelationName: 'Associate with'}]->(path:Pathway) RETURN path.PathwayName, path.PathwayId LIMIT 5"
    },
    {
        "question": "Which pathways are linked to the protein TP53?",
        "cypher": "MATCH (p:Protein {EntryName: 'TP53B_HUMAN'})-[:ProteinToPathway]->(path:Pathway) RETURN path.PathwayName, path.PathwayId LIMIT 5"
    },
    # ✅ ** Protein ↔ Protein **
    {
        "question": "Find proteins that physically associate with P53.",
        "cypher": "MATCH (p1:Protein {EntryName: 'P53_HUMAN'})-[:ProteinToProtein {RelationName: 'Physical association'}]->(p2:Protein) RETURN p2.FullName, p2.ProteinId LIMIT 5"
    },
    {
        "question": "Which proteins colocalize with BRCA1?",
        "cypher": "MATCH (p1:Protein {EntryName: 'BRCA1_HUMAN'})-[:ProteinToProtein {RelationName: 'Colocalization'}]->(p2:Protein) RETURN p2.FullName, p2.ProteinId LIMIT 5"
    },
    # ✅ ** GO ↔ GO **
    {
        "question": "Find GO terms that are subclasses of 'Cellular pigmentation'.",
        "cypher": "MATCH (child:GO)-[:GOToGO {RelationName: 'Is subclass of'}]->(parent:GO {GoTermName: 'Cellular pigmentation'}) RETURN child.GoId, child.GoTermName LIMIT 5"
    },
    # ✅ ** Pathway ↔ Pathway **
    {
        "question": "Find pathways that are parents of 'Apoptosis'.",
        "cypher": "MATCH (p1:Pathway)-[:PathwayToPathway {RelationName: 'Is parent of'}]->(p2:Pathway {PathwayName: 'Apoptosis'}) RETURN p1.PathwayName, p1.PathwayId LIMIT 5"
    },
    # ✅ ** G4 ↔ Disease **
    {
        "question": "Find diseases associated with G4 structures.",
        "cypher": "MATCH (g4:G4)-[:G4ToDisease {RelationName: 'Relate to'}]->(d:Disease) RETURN d.DiseaseName, d.DiseaseId LIMIT 5"
    },
    {
        "question": "Which diseases have potential links with G4 formations?",
        "cypher": "MATCH (g4:G4)-[:G4ToDisease]->(d:Disease) RETURN d.DiseaseName, d.DiseaseId LIMIT 5"
    },
    {
        "question": "Which diseases are related to G4 that are from HEK293T_Flavopiridol.CUT.Tag_DMSO?",
        "cypher": "MATCH (g4:G4 {SampleName: 'HEK293T_Flavopiridol.CUT.Tag_DMSO'})-[:G4ToDisease {RelationName: 'Relate to'}]->(d:Disease) RETURN d.DiseaseName, d.DiseaseId LIMIT 5"
    },
    {
        "question": "Can you tell me the G4 with more than 60 score and related to the disease Breast Cancer?",
        "cypher": "MATCH (g4:G4)-[:G4ToDisease {RelationName: 'Relate to'}]->(d:Disease {DiseaseName: 'Malignant neoplasm of breast'}) WHERE g4.Score > 60 RETURN g4.G4Id, g4.Score, g4.ConfidenceLevel, g4.Chr, g4.Location LIMIT 5"
    },
    # ✅ ** G4 ↔ Gene **
    {
        "question": "can you tell me the G4 which are related to the Gene BCR1?",
        "cypher":  "MATCH (g4:G4)-[:G4ToGene {RelationName: 'Relate to'}]->(g:Gene {GeneSymbol: 'BCR1'}) RETURN g4.G4Id, g4.Strand, g4.Chr, g4.Location, g4.SampleName LIMIT 5"
    },
    {
        "question": "Which genes are regulated by G4 structures?",
        "cypher": "MATCH (g4:G4)-[:G4ToGene {RelationName: 'Regulate'}]->(g:Gene) RETURN g.GeneSymbol, g.GeneId LIMIT 5"
    },
    {
        "question": "Find the Top 5 genes that are related to G4, which are ordered by G4 score",
        "cypher": "MATCH (g4:G4)-[:G4ToGene]->(g:Gene) RETURN g4.G4Id, g.GeneSymbol, g.GeneId, g4.Score ORDER BY g4.Score DESC LIMIT 5"
    },
    # ✅ ** G4 ↔ Protein **
    {
        "question": "Find proteins that bind to G4 structures.",
        "cypher": "MATCH (p:Protein)-[:ProteinToG4 {RelationName: 'Bind on'}]->(g4:G4) RETURN p.FullName, p.ProteinId LIMIT 5"
    },
    {
        "question": "Which proteins interact as transcription factors with G4?",
        "cypher": "MATCH (p:Protein)-[:ProteinToG4 {RelationName: 'As TF bind on'}]->(g4:G4) RETURN p.FullName, p.ProteinId LIMIT 5"
    },
    # 2. 多跳查询
    # ✅ **具体关系多跳查询**
    {
        "question": "Find diseases affected by drugs targeting EGFR.",
        "cypher": "MATCH (d:Drug)-[:DrugToProtein {RelationName: 'Target'}]->(p:Protein {EntryName: 'EGFR_HUMAN'})-[:ProteinToDisease]->(di:Disease) RETURN di.DiseaseName, di.DiseaseId LIMIT 5"
    },
    {
        "question": "Find genes that regulate G4 structures linked to the disease Alzheimer's.",
        "cypher": "MATCH (g:Gene)-[:GeneToDisease]->(d:Disease {DiseaseName: 'Alzheimer\'s Disease'}), (g4:G4)-[:G4ToGene]->(g) RETURN g.GeneSymbol, g.GeneId LIMIT 5 "
    },
    {
        "question": "Find drugs that act on proteins which regulate genes associated with breast cancer.",
        "cypher": "MATCH (d:Drug)-[:DrugToProtein]->(p:Protein)-[:ProteinToGene]->(g:Gene)-[:GeneToDisease {RelationName: 'Relate to'}]->(di:Disease {DiseaseName: 'Malignant neoplasm of breast'}) RETURN d.Name, d.DrugId LIMIT 5"
    },
    {
        "question": "Find GO terms involved in biological processes where G4 structures regulate the gene TP53.",
        "cypher": "MATCH (g4:G4)-[:G4ToGene {RelationName: 'Regulate'}]->(g:Gene {GeneSymbol: 'TP53'}) MATCH (g)-[:GeneToGO {RelationName: 'Involved in'}]->(go:GO) RETURN  DISTINCT go.GoTermName, go.GoId LIMIT 5"
    },
    # ✅ **模糊关系多跳查询**
    {
        "question": "Find pathways associated with genes involved in cancer.",
        "cypher": "MATCH (g:Gene)-[:GeneToDisease]->(d:Disease) WHERE d.DiseaseSemanticType = 'Neoplastic Process' MATCH (g)-[:GeneToPathway]->(p:Pathway) RETURN DISTINCT p.PathwayName, p.PathwayId LIMIT 5"
    },
    {
        "question": "Find proteins that interact with pathways related to metabolic processes.",
        "cypher": "MATCH (p:Pathway)-[:PathwayToPathway]->(p2:Pathway) WHERE p.PathwayName =~ '(?i).*metabolic.*' MATCH (p2)-[:ProteinToPathway]->(pr:Protein) RETURN DISTINCT pr.FullName, pr.ProteinId LIMIT 5"
    },
    {
        "question": "Find genes associated with diseases that are part of neurological disorders.",
        "cypher": "MATCH (g:Gene)-[:GeneToDisease]->(d:Disease) WHERE d.DiseaseName CONTAINS 'neurological' RETURN g.GeneSymbol, g.GeneId LIMIT 5"
    },
    # ✅ **具体实体多跳查询**
    {
        "question": "Find pathways that involve proteins interacting with the gene TP53.",
        "cypher": "MATCH (g:Gene {GeneSymbol: 'TP53'})-[:GeneToProtein]->(p:Protein)-[:ProteinToPathway]->(path:Pathway) RETURN path.PathwayName, path.PathwayId LIMIT 5"
    },
    {
        "question": "Find drugs that target proteins which are part of pathways involved in cell cycle regulation.",
        "cypher": "MATCH (p:Pathway {PathwayName: 'Cell cycle regulation'})<-[:ProteinToPathway]-(pr:Protein) MATCH (d:Drug)-[:DrugToProtein {RelationName: 'Target'}]->(pr) RETURN d.Name, d.DrugId LIMIT 5"
    },

    # ✅ **模糊实体多跳查询**
    {
        "question": "Find diseases associated with genes involved in biological processes.",
        "cypher": "MATCH (g:Gene)-[:GeneToGO]->(go:GO) WHERE go.GoTermType = 'Biological_process' MATCH (g)-[:GeneToDisease]->(d:Disease) RETURN DISTINCT d.DiseaseName, d.DiseaseId LIMIT 5"
    },
    {
        "question": "Find proteins that are involved in biological processes related to immunity.",
        "cypher": "MATCH (g:Gene)-[:GeneToGO]->(go:GO) WHERE go.GoTermName CONTAINS 'immune' MATCH (g)-[:GeneToProtein]->(p:Protein) RETURN p.FullName, p.ProteinId LIMIT 5"
    },
    # ✅ **G4 相关多跳查询**
    {
        "question": "Find genes regulated by G4 structures that are related to Breast Cancer.",
        "cypher": "MATCH (g4:G4)-[:G4ToGene {RelationName: 'Regulate'}]->(g:Gene)-[:GeneToDisease {RelationName: 'Relate to'}]->(d:Disease {DiseaseName: 'Malignant neoplasm of breast'}) RETURN DISTINCT g.GeneSymbol, g.GeneId LIMIT 5"
    },
    {
        "question": "Find proteins interacting with genes regulated by G4 structures.",
        "cypher": "MATCH (g4:G4)-[:G4ToGene {RelationName: 'Regulate'}]->(g:Gene)-[:GeneToProtein]->(p:Protein) RETURN DISTINCT p.FullName, p.ProteinId LIMIT 5"
    },
    {
        "question": "Find pathways associated with genes regulated by G4 structures.",
        "cypher": "MATCH (g4:G4)-[:G4ToGene {RelationName: 'Regulate'}]->(g:Gene)-[:GeneToPathway]->(p:Pathway) RETURN DISTINCT p.PathwayName, p.PathwayId LIMIT 5"
    },
    {
        "question": "Find drugs that treat diseases linked to genes regulated by G4 structures.",
        "cypher": "MATCH (g4:G4)-[:G4ToGene {RelationName: 'Regulate'}]->(g:Gene)-[:GeneToDisease {RelationName: 'Relate to'}]->(d:Disease)<-[:DrugToDisease {RelationName: 'Indication'}]-(drug:Drug) RETURN DISTINCT drug.Name, drug.DrugId LIMIT 5"
    },
    # 3. 查询路径信息
    {
        "question": "Find the shortest path from TP53 to Breast Cancer.",
        "cypher": "MATCH path = shortestPath((g:Gene {GeneSymbol: 'TP53'})-[*]-(d:Disease {DiseaseName: 'Malignant neoplasm of breast'})) RETURN path LIMIT 1"
    },
    {
        "question": "Find the complete path linking G4 structures to drugs treating related diseases.",
        "cypher": "MATCH path = (g4:G4)-[:G4ToGene {RelationName: 'Regulate'}]->(g:Gene)-[:GeneToDisease {RelationName: 'Relate to'}]->(d:Disease)<-[:DrugToDisease {RelationName: 'Indication'}]-(drug:Drug) RETURN path LIMIT 5"
    },
    {
        "question": "How many steps does it take for the gene TP53 to connect to Alzheimer's Disease?",
        "cypher": "MATCH path = shortestPath((g:Gene {GeneSymbol: 'TP53'})-[*]-(d:Disease {DiseaseName: 'Alzheimer\'s Disease'})) RETURN length(path) AS path_length LIMIT 1"
    },
    {
        "question": "Find all paths connecting the gene BRCA1 to the pathway Apoptosis.",
        "cypher": "MATCH path = (g:Gene {GeneSymbol: 'BRCA1'})-[*1..4]-(p:Pathway {PathwayName: 'Apoptosis'}) RETURN path LIMIT 5"
    },
    {
        "question": "Is there any connection between BRCA1 and Alzheimer's Disease?",
        "cypher": "MATCH path = (g:Gene {GeneSymbol: 'BRCA1'})-[*]-(d:Disease {DiseaseName: 'Alzheimer\'s Disease'}) RETURN CASE WHEN path IS NOT NULL THEN 'Yes' ELSE 'No' END AS Exists LIMIT 1"
    },
    # 4. 统计查询
    {
        "question": "How many genes, diseases, and drugs exist in the database?",
        "cypher": "MATCH (g:Gene) RETURN 'Gene' AS EntityType, COUNT(g) AS Count UNION ALL MATCH (d:Disease) RETURN 'Disease' AS EntityType, COUNT(d) AS Count UNION ALL MATCH (drug:Drug) RETURN 'Drug' AS EntityType, COUNT(drug) AS Count"
    },
    {
        "question": "How many cancer diseases in the database?",
        "cypher": "MATCH (d:Disease) WHERE d.DiseaseSemanticType = 'Neoplastic Process' RETURN COUNT(d) AS DiseaseCount"
    },
    {
        "question": "How many Drug-Disease pairs exist for indications?",
        "cypher": "MATCH (d:Drug)-[:DrugToDisease {RelationName: 'Indication'}]->(di:Disease) RETURN COUNT(DISTINCT [d, di]) AS DrugDiseasePairs"
    },
    {
        "question": "Which genes are associated with the most diseases?",
        "cypher": "MATCH (g:Gene)-[:GeneToDisease]->(d:Disease) RETURN g.GeneSymbol, COUNT(d) AS DiseaseCount ORDER BY DiseaseCount DESC LIMIT 5"
    },
    {
        "question": "Which GO terms are most frequently associated with genes?",
        "cypher": "MATCH (g:Gene)-[:GeneToGO]->(go:GO) RETURN go.GoTermName, COUNT(g) AS GeneCount ORDER BY GeneCount DESC LIMIT 5"
    },
    {
        "question": "Which diseases have the most drugs indicated for treatment?",
        "cypher": "MATCH (d:Drug)-[:DrugToDisease {RelationName: 'Indication'}]->(di:Disease) RETURN di.DiseaseName, COUNT(d) AS DrugCount ORDER BY DrugCount DESC LIMIT 5"
    },
    {
        "question": "What is the average, minimum, and maximum number of genes associated with diseases in the 'Neoplastic Process' category?",
        "cypher": "MATCH (g:Gene)-[:GeneToDisease]->(d:Disease) WHERE d.DiseaseSemanticType = 'Neoplastic Process' WITH d, COUNT(g) AS GeneCount RETURN AVG(GeneCount) AS AvgGenes, MIN(GeneCount) AS MinGenes, MAX(GeneCount) AS MaxGenes"
    },
    {
        "question": "How many different types of GeneToGene relationships exist, and how frequent are they?",
        "cypher": "MATCH (g1:Gene)-[r:GeneToGene]->(g2:Gene) RETURN r.RelationName AS RelationType, COUNT(r) AS Count ORDER BY Count DESC"
    },
    {
        "question": "What are the most common types of Protein-Protein interactions?",
        "cypher": "MATCH (p1:Protein)-[r:ProteinToProtein]->(p2:Protein) RETURN r.RelationName AS InteractionType, COUNT(r) AS Count ORDER BY Count DESC LIMIT 5"
    },
    {
        "question": "What is the average number of connections per Gene node?",
        "cypher": "MATCH (g:Gene)-[r]-() WHERE NOT g:Isolated WITH g, COUNT(r) AS Degree RETURN AVG(Degree) AS AvgConnections"
    },
    {
        "question": "Which genes have the highest number of relationships?",
        "cypher": "MATCH (g:Gene)-[r]-() RETURN g.GeneSymbol, COUNT(r) AS ConnectionCount ORDER BY ConnectionCount DESC LIMIT 5"
    },
    {
        "question": "How many distinct diseases are related to G4 structures?",
        "cypher": "MATCH (g4:G4)-[:G4ToDisease]->(d:Disease) RETURN COUNT(DISTINCT d.DiseaseName) AS DiseaseCount"
    },
    {
        "question": "How many unique Genes, Diseases, and Proteins are associated with G4 structures?",
        "cypher": "MATCH (g4:G4) OPTIONAL MATCH (g4)-[:G4ToGene]->(g:Gene) OPTIONAL MATCH (g4)-[:G4ToDisease]->(d:Disease) OPTIONAL MATCH (g4)<-[:ProteinToG4]-(p:Protein) RETURN COUNT(DISTINCT g) AS GeneCount,  COUNT(DISTINCT d) AS DiseaseCount,  COUNT(DISTINCT p) AS ProteinCount"
    },
    # 5. 复杂查询
    {
        "question": "Find drugs that target proteins related to genes affected by G4 structures.",
        "cypher": "MATCH (g4:G4)-[:G4ToGene {RelationName: 'Regulate'}]->(g:Gene)-[:GeneToProtein]->(p:Protein)<-[:DrugToProtein {RelationName: 'Target'}]-(d:Drug) RETURN DISTINCT d.Name, d.DrugId, p.FullName, p.ProteinId, g.GeneSymbol, g.GeneId LIMIT 5"
    },
    {
        "question": "Find all possible biological pathways connected to Lung Cancer through genes.",
        "cypher": "MATCH path = (d:Disease {DiseaseName: 'Non-Small Cell Lung Carcinoma'})<-[:GeneToDisease]-(g:Gene)-[:GeneToPathway]->(p:Pathway) RETURN path LIMIT 5"
    },
    {
        "question": "Find the number of diseases and their associated drugs grouped by DiseaseSemanticType.",
        "cypher": "MATCH (d:Disease)-[:DrugToDisease {RelationName: 'Indication'}]-(dr:Drug) RETURN d.DiseaseSemanticType, COUNT(DISTINCT d) AS DiseaseCount, COUNT(DISTINCT dr) AS DrugCount ORDER BY DiseaseCount DESC"
    },
    {
        "question": "Find proteins that are targeted by the highest number of different drugs.",
        "cypher": "MATCH (d:Drug)-[:DrugToProtein {RelationName: 'Target'}]->(p:Protein) RETURN p.FullName, p.ProteinId, COUNT(DISTINCT d) AS DrugCount ORDER BY DrugCount DESC LIMIT 5"
    },
    {
        "question": "Find genes affected by proteins binding to G4 structures and linked to cancer.",
        "cypher": "MATCH (p:Protein)-[:ProteinToG4 {RelationName: 'Bind on'}]->(g4:G4) MATCH (p)-[:ProteinToGene]->(g:Gene)-[:GeneToDisease]->(d:Disease) WHERE d.DiseaseSemanticType = 'Neoplastic Process' RETURN DISTINCT g.GeneSymbol, g.GeneId, d.DiseaseName, d.DiseaseId LIMIT 5"
    },
]
