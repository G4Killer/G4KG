import logging
import json
from typing import Literal
import re

from langchain_core.runnables import RunnableConfig
from langgraph.graph import StateGraph, END, START
from langgraph.checkpoint.memory import logger
from langchain_neo4j.graphs.neo4j_graph import Neo4jGraph
from langgraph.config import get_stream_writer

from utils.utils import checkpointer, process_streaming_content
from src.chat_llm import ChatTongyiLLM, build_tongyi_llm, get_api_key_from_config
from sub_graph.subgraph_states import SubgraphState, SubRouter
from prompts.prompt_templates import SUBGRAPH_ROUTER_SYSTEM_PROMPT, EXTRACTION_SYSTEM_PROMPT, SUBGRAPH_RELATIONSHIP_REFINE_PROMPT, RELATIONSHIP_MAPPING_TEXT
from utils.utils import config
from utils.example_selector import few_shot_prompt

from utils.vectorstore_config import get_vectorstore_and_client

# 日志配置
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)


VECTORSTORE_COLLECTION = config["vectorstore"]["index_name"]
VECTORSTORE_TEXT_KEY = config["vectorstore"]["text_key"]
WEAVIATE_HTTP_HOST = config["weaviate"]["http_host"]
WEAVIATE_HTTP_PORT = config["weaviate"]["http_port"]
WEAVIATE_HTTP_SECURE = config["weaviate"]["http_secure"]
WEAVIATE_GRPC_HOST = config["weaviate"]["grpc_host"]
WEAVIATE_GRPC_PORT = config["weaviate"]["grpc_port"]
WEAVIATE_GRPC_SECURE = config["weaviate"]["grpc_secure"]
WEAVIATE_API = config["weaviate"]["auth_api_key"]

NEO_4J_RUL = config["neo4j"]["url"]
NEO_4J_USERNAME = config["neo4j"]["username"]
NEO_4J_PASSWORD = config["neo4j"]["password"]
NEO_4J_DATABASE = config["neo4j"]["database"]

Chat_tongyi = config["llm"]["model"]
TEMPERATURE = config["llm"]["temperature"]

# Neo4j 连接实例
neo4j_graph = Neo4jGraph(
    url=NEO_4J_RUL,
    username=NEO_4J_USERNAME,
    password=NEO_4J_PASSWORD,
    database=NEO_4J_DATABASE
)

# 1. 子图的分析和分类函数
async def analyze_and_route_subgraph_query(
    state: SubgraphState, *, config: RunnableConfig
) -> dict[str, SubRouter]:
    """
    分析用户查询并确定其类型：
    - attribute_query: 需要查询某个G4结构的属性
    - relationship_query: 需要查询G4结构之间的关系

    Returns a JSON response in the format:
    {
        "type": "attribute_query" | "relationship_query"
    }
    """
    # 使用诊断版本的writer获取
    writer = get_stream_writer()
    if writer:
        writer({"subgraph_event": "开始分析查询类型"})

    api_key = get_api_key_from_config(config)
    model = build_tongyi_llm(model=Chat_tongyi, temperature=TEMPERATURE, streaming=True, api_key=api_key)
    messages = [
        {"role": "system", "content": SUBGRAPH_ROUTER_SYSTEM_PROMPT},
        {"role": "human", "content": state.question},
    ]

    logger.info("--- Subgraph: Analyze and Route Query ---")
    response = await model.ainvoke(messages)

    # 使用公共方法处理流式输出内容
    raw_content = process_streaming_content(response.content)

    # 去掉 ```json ``` 
    cleaned_json = re.sub(r"^```json\s*|\s*```$", "", raw_content, flags=re.MULTILINE)

    # **🚀 解析 JSON**
    try:
        response_data = json.loads(cleaned_json)  # ✅ 解析清理后的 JSON
    except json.JSONDecodeError:
        logger.error(f"❌ Failed to decode JSON after cleaning: {cleaned_json}")
        response_data = {"type": "attribute_query"}  # ❗ 解析失败时返回默认值，防止 NoneType 错误

    # **🚀 赋值到 state**
    state.sub_router = response_data
    logger.info(f"✅ Subgraph router response: {state.sub_router}")

    # 仅在分析完成后发送一次结果
    if writer:
        writer({"subgraph_event": f"分析完成 - 确定为{state.sub_router['type']}查询"})

    return {"sub_router": state.sub_router}

# 2. 子图的路由函数，根据分类结果决定调用哪个查询链
def subgraph_route_query(state: SubgraphState) -> Literal["vector_search_chain", "extract_entities_and_relationships"]:
    """
    根据子图中的 sub_roter 判断查询类型：
      - 如果 type 为 "attribute_query"，则返回 "vector_search_chain"
      - 如果 type 为 "relationship_query"，则返回 "extract_entities_and_relationships"
    """
    _type = state.sub_router["type"]
    
    # 这个函数不需要再次发送事件，已经在上一步发送过了
    if _type == "attribute_query":
        return "vector_search_chain"
    elif _type == "relationship_query":
        return "extract_entities_and_relationships"
    else:
        raise ValueError(f"Unknown sub_router type: {_type}")

# 3. **向量搜索查询链**
def vector_search_chain(state: SubgraphState) -> dict:
    """执行向量搜索查询，基于用户输入的查询进行匹配"""
    
    # 1. 获取用户问题
    query = state.question  # 获取从主图传递过来的问题
    # 2. 使用配置的Weaviate向量存储实例执行查询
    vectorstore, client = get_vectorstore_and_client(index_name="KGDocument")  # 初始化Weaviate向量存储实例和客户端
    result = vectorstore.similarity_search(query, k=1)  # 获取最相关的1个结果
    # 3. 将查询结果和答案来源添加到state中
    state.documents = []
    for doc in result:
        # 提取文档中的 'page_content' 字段
        text_content = doc.page_content  # 直接使用page_content字段
        state.documents.append({"content": text_content})

    writer = get_stream_writer()    
    # 如果没有结果，打印提示，仅发送一次事件
    if writer:
        writer({"subgraph_event": f"向量搜索完成，找到{len(state.documents)}条结果"})

    logger.info(f"Vector Search Result: {state.documents}")
    # 设置答案来源为 vector_search
    state.answer_source = ["vector_search"] * len(result)  # 设置来源为 vector_search

    # 返回包含更新的state信息的字典，供后续节点使用
    return {
        "documents": state.documents,
        "answer_source": state.answer_source,
    }

# 4. **向量+CYPHER搜索查询链**
# ---------------- Step 1: 实体与关系提取 ----------------
async def extract_entities_and_relationships(state: SubgraphState, *, config: RunnableConfig) -> dict:
    
    api_key = get_api_key_from_config(config)
    model = build_tongyi_llm(model=Chat_tongyi, temperature=TEMPERATURE, streaming=False, api_key=api_key)
    messages = [
        {"role": "system", "content": EXTRACTION_SYSTEM_PROMPT},
        {"role": "human", "content": state.question},
    ]

    logger.info("--- Extract Entities and Relationships ---")
    response = await model.ainvoke(messages)

    # 使用公共方法处理流式输出内容
    raw_content = process_streaming_content(response.content)

    # 去掉 ```json ```
    cleaned_json = re.sub(r"^```json\s*|\s*```$", "", raw_content, flags=re.MULTILINE)

    # **直接解析 JSON，不使用 isinstance**
    try:
        response_data = json.loads(cleaned_json)  # ✅ 直接解析
    except json.JSONDecodeError:
        logger.error(f"Failed to decode JSON: {raw_content}")
        response_data = {"entities": [], "relationships": []}  # 解析失败返回默认值

    # **直接赋值 List[str]**
    state.entities = response_data.get("entities", [])
    state.relationships = response_data.get("relationships", [])

    # 简化日志输出，只输出实体和关系的数量
    logger.info(f"找到实体数量: {len(state.entities)}, 关系数量: {len(state.relationships)}")

    writer = get_stream_writer()
    # 发送简化结果
    if writer:
       writer({"subgraph_event": f"找到实体: {state.entities}, 关系: {state.relationships}"})

    return {
        "entities": state.entities,
        "relationships": state.relationships,
    }

# ---------------- Step 2: 实体精炼 ----------------
async def refine_entities(state: SubgraphState, *, config: RunnableConfig) -> dict:

    vectorstore, client = get_vectorstore_and_client(index_name="KGDocument")
    refined_entities = []

    for entity in state.entities:
        entity_query = entity
        result = vectorstore.similarity_search(entity_query, k=1)

        if result and len(result) > 0:
            refined_entity = result[0].page_content
            refined_entities.append(refined_entity)
            # 不再为每个实体发送消息，减少事件数量
        else:
            refined_entities.append(entity)

    state.entities_refined = refined_entities
    logger.info(f"Refined entities: {state.entities_refined}")
    writer = get_stream_writer()
    # 发送精炼结果 - 只发送一次总结性消息
    if writer:
        writer({"subgraph_event": f"精炼后的实体: {state.entities_refined}"})
    return {
        "entities_refined": state.entities_refined,
    }

# ---------------- Step 3: 关系精炼 ----------------
async def refine_relationships(state: SubgraphState, *, config: RunnableConfig) -> dict:
    """
    结合实体精炼后的结果和预定义的关系映射字典，对 state.relationships 进行精炼，
    直接使用 LLM 生成最终的关系名称，更新 state.relationships_refined。
    """

    prompt_text = SUBGRAPH_RELATIONSHIP_REFINE_PROMPT.format(
        rel_dict=RELATIONSHIP_MAPPING_TEXT,
        question=state.question,
        refined_entities=state.entities_refined,
        original_relationships=state.relationships
    )

    api_key = get_api_key_from_config(config)
    model = build_tongyi_llm(model=Chat_tongyi, temperature=TEMPERATURE, streaming=False, api_key=api_key)

    # **调用 LLM 让其返回单个关系名称，确保包含user角色的消息**
    response = await model.ainvoke([
        {"role": "system", "content": prompt_text},
        {"role": "user", "content": "请根据以上信息确定最合适的关系名称。"}
    ])

    # **直接使用 response.content 作为关系名称**
    refined_relationship = process_streaming_content(response.content) if response.content else "unknown"
    refined_relationship = refined_relationship.strip()

    # **更新状态**
    state.relationships_refined = [refined_relationship]  # ✅ 直接存入列表
    logger.info(f"LLM Returned Relationship: {refined_relationship}")  # 方便调试
    logger.info(f"Refined relationships: {state.relationships_refined}")

    writer = get_stream_writer()
    # 简化输出
    if writer:
        writer({"subgraph_event": f"精炼后的关系: {state.relationships_refined}"})

    return {
        "relationships_refined": state.relationships_refined
    }

# ---------------- Step 4: 生成精确的 Cypher 查询 ----------------
def _cleanup_cypher_code_fences(text: str) -> str:
    """
    先去掉三重反引号以及类似 ```cypher 的块标记，只保留内部的 Cypher 语句。
    这样如果 LLM 返回:
    ```cypher
    MATCH ...
    RETURN ...
    ```
    我们就能把三重反引号去掉，只留纯文本 MATCH ... RETURN ...
    """

    # 1) 去除形如 ```cypher 或 ```bash 等可能的语言标识
    #    这里用正则捕获三重反引号加可选单词字符
    text = re.sub(r"```[a-zA-Z_-]*", "", text)

    # 2) 再去除任何剩余的三重反引号 ```
    text = text.replace("```", "")

    # 最后 strip 一下
    return text.strip()


def _remove_enclosing_quotes(text: str) -> str:
    """只去掉最前面和最后面成对出现的引号（或三重引号），不动内部内容。"""
    text = text.strip()

    # 1) 如果是三重双引号包裹
    if len(text) >= 6 and text.startswith('"""') and text.endswith('"""'):
        return text[3:-3].strip()

    # 2) 如果是三重单引号包裹
    if len(text) >= 6 and text.startswith("'''") and text.endswith("'''"):
        return text[3:-3].strip()

    # 3) 如果是单个双引号包裹
    if len(text) >= 2 and text.startswith('"') and text.endswith('"'):
        return text[1:-1].strip()

    # 4) 如果是单个单引号包裹
    if len(text) >= 2 and text.startswith("'") and text.endswith("'"):
        return text[1:-1].strip()

    # 否则原样返回
    return text


async def generate_cypher_query(state: SubgraphState, *, config: RunnableConfig) -> dict:
    """
    利用 KG schema、用户查询、精炼后的实体和关系信息生成精确的 Cypher 查询语句，
    更新 state.cypher_query。
    """

    # 1. 用 FewShotPromptTemplate 渲染 Prompt
    prompt = few_shot_prompt.format(
        schema=state.schema,
        question=state.question,
        refined_entities=", ".join(state.entities_refined),
        refined_relationships=", ".join(state.relationships_refined)
    )

    # 2. 调用 LLM 获取回复，确保包含user角色的消息
    api_key = get_api_key_from_config(config)
    model = build_tongyi_llm(model=Chat_tongyi, temperature=TEMPERATURE, streaming=False, api_key=api_key)
    response = await model.ainvoke([
        {"role": "system", "content": prompt},
        {"role": "user", "content": "请根据以上信息生成一个合适的Cypher查询语句。"}
    ])

    # 3. 原始的 LLM 输出，确保处理流式输出
    raw_cypher = process_streaming_content(response.content)

    # 4. 去掉三重反引号代码块 (```cypher ...```)
    without_fences = _cleanup_cypher_code_fences(raw_cypher)

    # 5. 去掉最外层引号
    cleaned_cypher = _remove_enclosing_quotes(without_fences)

    # 6. 存到 state 并日志记录
    state.cypher_query = cleaned_cypher
    state.answer_source = ["cypher_search"]  # 设置来源为 cypher_search
    logger.info(f"Generated Cypher Query: {state.cypher_query}")

    writer = get_stream_writer()
    # 发送查询结果 - 只发送一次总结消息
    if writer:
        writer({"subgraph_event": f"Cypher查询生成完成: {state.cypher_query}"})

    return {
        "cypher_query": state.cypher_query,
        "answer_source": state.answer_source,
    }

# ---------------- Step 5: 执行 Cypher 查询 ----------------
async def execute_cypher_query(state: SubgraphState, *, config: RunnableConfig) -> dict:

    # 获取流式写入器
    writer = get_stream_writer()
    # 直接执行整条查询
    results = neo4j_graph.query(state.cypher_query)

    state.documents = results
    logging.info(f"Executed Cypher Query: {state.cypher_query}")
    logging.info(f"Query Results: {results}")

    # 发送执行结果 - 仅发送一次总结
    if writer:
        writer({"subgraph_event": f"查询执行完成: 找到{len(results)}条结果"})
            
    return {
        "documents": state.documents,
        "cypher_query": state.cypher_query,
    }


subgraph_checkpointer = checkpointer

# 3. **子图构建和节点定义**
builder = StateGraph(SubgraphState)

# 1. **添加节点**
builder.add_node(analyze_and_route_subgraph_query)
builder.add_node(vector_search_chain)
builder.add_node(extract_entities_and_relationships)
builder.add_node(refine_entities)
builder.add_node(refine_relationships)
builder.add_node(generate_cypher_query)
builder.add_node(execute_cypher_query)

# 2. **添加边**
builder.add_edge(START, "analyze_and_route_subgraph_query")
builder.add_conditional_edges("analyze_and_route_subgraph_query", subgraph_route_query)
builder.add_edge("vector_search_chain", END)

builder.add_edge("extract_entities_and_relationships", "refine_entities")
builder.add_edge("refine_entities", "refine_relationships")
builder.add_edge("refine_relationships", "generate_cypher_query")
builder.add_edge("generate_cypher_query","execute_cypher_query")
builder.add_edge("execute_cypher_query", END)

# 3. **编译图**
search_graph = builder.compile(checkpointer=subgraph_checkpointer)

