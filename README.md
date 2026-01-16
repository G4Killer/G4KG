# G4KG - G4四链体知识图谱系统

<div align="center">

**G-Quadruplex Knowledge Graph System**

一个基于 Neo4j 的 G4 四链体知识图谱可视化与智能问答平台

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.2-brightgreen)](https://vuejs.org/)
[![Python](https://img.shields.io/badge/Python-3.9+-blue)](https://www.python.org/)

</div>

---

## 项目简介

G4KG 是一个专注于 G4 四链体（G-Quadruplex）的知识图谱系统，提供：

- **属性检索**：按 G4Id、染色体位置、样本名称等条件检索 G4 属性
- **关系检索**：基于基因、疾病、蛋白、通路等实体探索与 G4 的关联关系
- **子图可视化**：力导向图展示知识图谱局部结构
- **功能分析**：GO/Pathway 分布统计与富集网络分析
- **智能问答**：基于 RAG 的知识图谱问答机器人

---

## 系统架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                          G4KG 系统架构                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌──────────────┐         ┌──────────────┐         ┌─────────────┐ │
│  │   Vue 3      │────────▶│  Spring Boot │────────▶│    Neo4j    │ │
│  │   前端应用    │  HTTP   │   后端服务   │  Bolt  │  图数据库   │ │
│  │   (:8080)    │         │   (:8081)    │         │   (:7687)   │ │
│  └──────────────┘         └──────────────┘         └─────────────┘ │
│         │                                                        │ │
│         │ WebSocket                                             │ │
│         ▼                                                        │ │
│  ┌──────────────┐                                               │ │
│  │   FastAPI    │───▶ Weaviate (向量库)                          │ │
│  │   G4RAG服务  │───▶ DashScope (LLM)                            │ │
│  │   (:8000)    │                                                │ │
│  └──────────────┘                                                │ │
│                                                                   │ │
└───────────────────────────────────────────────────────────────────┘
```

---

## 目录结构

```
G4KG_ProjectCode/
├── G4KG_BACKEND/          # Spring Boot 后端服务
│   └── g4kg-demo/
│       ├── src/main/java/ # Java 源码
│       └── src/main/resources/
│           └── application.properties
├── G4KG_FRONT/            # Vue 3 前端应用
│   ├── src/               # Vue 组件与资源
│   ├── public/            # 静态资源
│   └── vue.config.js
├── G4RAG/                 # FastAPI RAG 服务
│   ├── main_graph/        # LangGraph 主图
│   ├── sub_graph/         # LangGraph 子图
│   ├── prompts/           # Prompt 模板
│   ├── src/               # 核心逻辑
│   └── config.yaml        # 配置文件
├── scripts/               # 工具脚本
├── AGENTS.md              # 开发指南
└── README.md              # 本文件
```

---

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 运行环境 |
| Spring Boot | 3.3.5 | 应用框架 |
| Spring Data Neo4j | - | Neo4j ORM |
| Lombok | - | 简化代码 |
| JUnit5 | - | 单元测试 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.2.13 | 前端框架 |
| Vue Router | 4.5.0 | 路由管理 |
| Element Plus | - | UI 组件库 |
| ECharts | 5 | 数据可视化 |
| Axios | - | HTTP 客户端 |

### RAG 服务
| 技术 | 说明 |
|------|------|
| FastAPI | WebSocket 服务 |
| LangGraph | RAG 工作流编排 |
| LangChain | LLM/向量库集成 |
| Weaviate | 向量数据库 |
| DashScope/Qwen | LLM & Embedding |

---

## 快速开始

### 前置要求

- JDK 17+
- Node.js 16+
- Python 3.9+
- Neo4j 4.4+ (含 APOC 插件)
- Weaviate 1.x+

### 1. 后端服务

```bash
cd G4KG_BACKEND/g4kg-demo

# 修改配置文件
vim src/main/resources/application.properties
# 配置 Neo4j 连接信息

# 启动服务
./gradlew bootRun

# 访问 http://localhost:8081
```

### 2. 前端应用

```bash
cd G4KG_FRONT

# 安装依赖
npm install

# 启动开发服务器
npm run serve

# 访问 http://localhost:8080
```

### 3. RAG 服务

```bash
cd G4RAG

# 创建虚拟环境
python -m venv .venv
.venv\Scripts\activate  # Windows
# source .venv/bin/activate  # Linux/Mac

# 安装依赖
pip install -r requirements_websocket.txt

# 修改配置文件
vim config.yaml
# 配置 Weaviate、Neo4j、LLM 连接信息

# 启动服务
python start_server.py

# WebSocket 服务运行在 ws://localhost:8000/ws/rag
```

---

## 功能模块

### 属性检索
支持按以下条件组合查询 G4 属性：
- G4Id
- 染色体
- 位置
- 样本名称
- 置信度
- 分数

### 关系检索
从指定实体（基因、疾病、蛋白、通路等）出发，探索与 G4 的多跳关联路径。

### 子图可视化
以力导向图形式展示知识图谱局部结构，支持：
- 节点类型过滤
- 深度控制
- 节点数量限制
- 交互式导航

### 功能分析
- **GO 分布分析**：展示与 G4 相关的 Gene Ontology 统计
- **Pathway 分布分析**：展示相关通路分布
- **富集网络**：可视化功能富集网络

### 智能问答 ChatBot
基于知识图谱的 RAG 问答系统，支持：
- 自然语言提问
- 思考过程可视化
- 实体识别与关系查询
- 向量检索增强

---

## 外部依赖

| 服务 | 端口 | 说明 |
|------|------|------|
| Neo4j | 7687 (Bolt), 7474 (Browser) | 图数据库 |
| Weaviate | 8080 (HTTP), 50051 (gRPC) | 向量数据库 |
| DashScope API | - | 通义千问 LLM |

---

## 配置说明

### 后端配置 (`application.properties`)
```properties
# Neo4j 配置
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=your_password

# 服务端口
server.port=8081
```

### RAG 配置 (`config.yaml`)
```yaml
weaviate:
  http_host: localhost
  http_port: 8080
  grpc_port: 50051

neo4j:
  uri: bolt://localhost:7687
  username: neo4j
  password: your_password

llm:
  model: qwen-plus
  temperature: 0.1
```

---

## 开发指南

详细的开发指南请参考 [AGENTS.md](./AGENTS.md)

---

## 数据模型

### 节点类型
| 类型 | 主要属性 |
|------|----------|
| **G4** | G4Id, Chr, Location, Strand, Score, ConfidenceLevel |
| **Gene** | GeneId, GeneSymbol, FullName, Description |
| **Disease** | DiseaseId, DiseaseName, Description |
| **Protein** | ProteinId, EntryName, FullName |
| **GO** | GoId, GoTermName, GoTermType |
| **Pathway** | PathwayId, PathwayName |
| **Drug** | DrugId, Name |

### 关系类型
- G4ToGene, G4ToDisease
- GeneToProtein, GeneToGO, GeneToPathway
- ProteinToGO, ProteinToPathway
- DrugToProtein, DrugToDisease
- 更多关系见 `G4KG_BACKEND/g4kg-demo/src/main/java/com/example/g4kgdemo/model/`

---

## 许可证

本项目采用 MIT 许可证。

---

## 贡献

欢迎提交 Issue 和 Pull Request！

---

## 联系方式

如有问题，请通过 GitHub Issues 联系。
