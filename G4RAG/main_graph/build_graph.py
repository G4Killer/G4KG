import json
import re
from typing import Literal, cast, Any

from jinja2 import Template
from langchain_core.messages import BaseMessage
from langchain_core.runnables import RunnableConfig
from langgraph.graph import StateGraph, END, START
from utils.utils import checkpointer, process_streaming_content
import logging
from sub_graph.subgraph_states import SubgraphState
from utils.utils import config
from prompts.prompt_templates import ROUTER_SYSTEM_PROMPT, MORE_INFO_SYSTEM_PROMPT, GENERAL_SYSTEM_PROMPT, \
    RESPONSE_SYSTEM_PROMPT, REWRITE_QUERY_PROMPT, G4KG_GENERAL_SYSTEM_PROMPT
from src.chat_llm import ChatTongyiLLM, build_tongyi_llm, get_api_key_from_config
from main_graph.graph_states import AgentState, Router, InputState
from sub_graph.build_subgraph import search_graph
from langgraph.config import get_stream_writer

# 日志配置
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# 假设从配置文件中获取模型信息
Chat_tongyi = config["llm"]["model"]
#CUSTOM_EMBEDDING_MODEL = config["llm"]["custom_embedding_model"]
TEMPERATURE = config["llm"]["temperature"]


# 1. **分析并路由用户查询 (`analyze_and_route_query`)**
async def analyze_and_route_query(state: AgentState, *, config: RunnableConfig) -> dict[str, Router]:
    """
    Analyze the user's query and determine its category:
    - KG_query: Requires information retrieval from the Knowledge Graph (KG).
    - more-info: The user's input is incomplete and requires additional details.
    - general: The query is not related to the Knowledge Graph.

    Returns a JSON response in the format:
    {
        "logic": "Reason for classification",
        "type": "KG_query" | "more-info" | "general"
    }
    """
    # 获取用户最后一条消息内容
    user_query = ""
    for msg in reversed(state.messages):
        if isinstance(msg, dict) and msg.get("role") == "user" and "content" in msg:
            user_query = msg.get("content", "")
            break
        elif hasattr(msg, "content") and getattr(msg, "role", None) == "user":
            user_query = msg.content
            break
    
    # 获取流式写入器
    writer = get_stream_writer()
    if writer:
        writer({"maingraph_event": "开始分析用户查询类型..."})
    
    api_key = get_api_key_from_config(config)
    model = build_tongyi_llm(model=Chat_tongyi, temperature=TEMPERATURE, streaming=True, api_key=api_key)
    template = Template(ROUTER_SYSTEM_PROMPT)
    prompt = template.render(schema=state.schema)
    messages = [{"role": "system", "content": prompt}] + state.messages

    logging.info("--- Analyzing and Routing Query ---")

    response = await model.ainvoke(messages)

    # 使用公共方法处理流式输出内容
    raw_content = process_streaming_content(response.content)

    # Handle Markdown-style JSON blocks and unexpected whitespace
    cleaned_json = re.sub(r"^\s*json\s*", "", raw_content, flags=re.MULTILINE)  # Remove leading `json\n`
    cleaned_json = re.sub(r"^```json\s*|\s*```$", "", cleaned_json, flags=re.MULTILINE)  # Remove ` ```json ` blocks
    cleaned_json = cleaned_json.strip()  # Final cleanup

    # JSON Parsing with Fallback
    try:
        response_data = json.loads(cleaned_json)  # Parse cleaned JSON
    except json.JSONDecodeError:
        logging.error(f"❌ JSON decoding failed, returning default classification: {cleaned_json}")
        response_data = {"logic": "Unable to parse response, default classification", "type": "general"}
        # 流式写入解析失败消息
        if writer:
            try:
                writer({"maingraph_event": "路由决策解析失败，使用默认分类"})
            except:
                pass

    # Assign to state
    state.router = response_data
    logging.info(f"✅ Router Response: {state.router}")
    # 流式写入路由决策结果
    if writer:
        router_type = state.router.get("type", "未知")
        # 显示路由类型、摘要逻辑和用户查询
        writer({"maingraph_event": f"路由决策: 确定查询类型为{router_type}"})

    return {"router": state.router}

# 2. **查询路由 (`route_query`)**
def route_query(state: AgentState) -> Literal["rewrite_query", "ask_for_more_info", "respond_to_general_query", "respond_to_g4kg_general_query"]:
    """根据查询分类决定接下来的步骤"""
    _type = state.router["type"]
    if _type == "KG_query":
        return "rewrite_query"
    elif _type == "more-info":
        return "ask_for_more_info"
    elif _type == "general":
        return "respond_to_general_query"
    elif _type == "G4KG-General":
        return "respond_to_g4kg_general_query"
    else:
        raise ValueError(f"Unknown router type {_type}")

async def rewrite_query(state: AgentState, *, config: RunnableConfig) -> dict:
    """
    重新生成清晰的问题，以便传递到子图，确保完整的上下文。
    """
    # 获取流式写入器
    writer = get_stream_writer()
    if writer:
        writer({"maingraph_event": "生成最终回答: 处理查询结果并生成回复"})

    api_key = get_api_key_from_config(config)
    model = build_tongyi_llm(model=Chat_tongyi, temperature=TEMPERATURE, streaming=False, api_key=api_key)
    prompt = REWRITE_QUERY_PROMPT.format(context=state.messages)
    
    # 获取用户最后一条消息内容，如果没有则使用空字符串
    user_query = ""
    for msg in reversed(state.messages):
        if isinstance(msg, dict) and msg.get("role") == "user" and "content" in msg:
            user_query = msg.get("content", "")
            break
        elif hasattr(msg, "content") and getattr(msg, "role", None) == "user":
            user_query = msg.content
            break
    
    # 确保包含一个user角色的消息
    messages = [
        {"role": "system", "content": prompt},
        {"role": "user", "content": user_query or "请重写上述查询，使其更清晰"}
    ]
    
    response = await model.ainvoke(messages)
    
    # 使用公共方法处理响应内容
    rewritten_question = process_streaming_content(response.content)
    logging.info(f"🔄 Rewritten Query: {rewritten_question}")

    return {"processed_query": rewritten_question}


async def conduct_search(state: AgentState, *, config: RunnableConfig) -> dict[str, Any]:
    """
    调用子图进行查询，并返回查询结果
    """
    # 获取流式写入器
    writer = get_stream_writer()
    if writer:
        writer({"maingraph_event": "开始图谱查询..."})
    logging.info("🔍 Calling subgraph...")
    logging.debug(f"使用查询: {state.processed_query}")
    
    # 添加更多日志以便调试
    logging.debug(f"使用配置: {config}")

    # 让子图继承主图的 `Checkpoint`
    subgraph_config = config  # 直接使用传入的 `config`，不要手动创建新 `thread_id`

    # 执行子图查询，使用astream而不是ainvoke，以支持事件流
    last_result = None
    subgraph_received = False
    
    # 收集子图事件
    async for event in search_graph.astream(
            SubgraphState(question=state.processed_query),
            config=subgraph_config,  # 传递相同的 config，确保 memory 共享
            stream_mode="custom"  # 使用custom模式以支持实时事件传递
    ):
        # 记录收到的每个事件以辅助调试
        logging.debug(f"收到子图事件: {event}")
        
        # 检查是否包含子图自定义事件
        if isinstance(event, dict) and "subgraph_event" in event:
            subgraph_received = True
            logging.info(f"收到子图推理事件: {event['subgraph_event']}")
            # 这些事件会自动通过stream_mode传递给WebSocket服务器
            continue
            
        # 保存最后一个结果用于返回
        last_result = event

    # 记录子图的 Checkpoint，并用它回填缺失的流式结果
    checkpoint_data = checkpointer.get(subgraph_config)
    logging.info(f"📌 Subgraph Checkpoint: {checkpoint_data}")
    channel_values = checkpoint_data.get("channel_values", {}) if isinstance(checkpoint_data, dict) else {}

    # 没有通过流事件拿到结果时，优先尝试从checkpoint回填
    if not last_result:
        if channel_values:
            logging.warning("子图流事件缺少最终结果，使用 checkpoint 回填")
            last_result = channel_values
        else:
            logging.warning("子图未返回任何结果")
            last_result = {}
            # 流式写入子图结果为空的消息
            if writer:
                writer({"maingraph_event": "子图未返回结果"})
    else:
        # 如果流事件缺少部分字段，用checkpoint补全
        if isinstance(last_result, dict) and channel_values:
            for key, value in channel_values.items():
                last_result.setdefault(key, value)
    
    # 将subgraph_received检查放在循环后，避免错误的"未收到子图事件"警告
    # 只在DEBUG级别记录这个信息，避免作为错误显示
    if not subgraph_received:
        logging.debug("子图诊断: 未收到任何子图事件流，可能stream_mode配置问题，但子图处理已完成")
        # 不再向前端发送此警告，因为这是内部诊断信息，不影响正常结果

    # 处理查询结果
    docs = last_result.get("documents", [])
    cypher = last_result.get("cypher_query", "")
    state.documents = docs
    state.answer_source = last_result.get("answer_source", [])
    state.cypher_query = cypher
    logging.info(f"Cypher query executed: {state.cypher_query}")
    
    # 添加子图处理完成的信息
    logging.info(f"子图处理完成，文档数量: {len(docs)}")

    # 确保 `messages` 被追加到 `state.messages`
    if "messages" in last_result:
        state.messages.extend(last_result["messages"])

    return {
        "documents": state.documents,
        "answer_source": state.answer_source,
        "cypher_query": state.cypher_query
    }

# 3. **生成请求更多信息的回答 (`ask_for_more_info`)**
async def ask_for_more_info(
    state: AgentState, *, config: RunnableConfig
) -> dict[str, list[BaseMessage]]:
    """生成询问用户更多信息的响应"""
        
    api_key = get_api_key_from_config(config)
    model = build_tongyi_llm(model=Chat_tongyi, temperature=TEMPERATURE, streaming=True, api_key=api_key)
    system_prompt = MORE_INFO_SYSTEM_PROMPT.format(logic=state.router["logic"])
    messages = [{"role": "system", "content": system_prompt}] + state.messages
    
    logging.info("---ASKING FOR MORE INFORMATION---")
    
    response = await model.ainvoke(messages)
    
    # 注意：这里不处理response.content，直接返回response对象
    # 因为后续处理会自动处理BaseMessage对象
    return {"messages": [response]}

# 5. **普通问题响应 (`respond_to_general_query`)**
async def respond_to_general_query(
    state: AgentState, *, config: RunnableConfig
) -> dict[str, list[BaseMessage]]:
    """生成普通查询的回答"""
        
    api_key = get_api_key_from_config(config)
    model = build_tongyi_llm(model=Chat_tongyi, temperature=TEMPERATURE, streaming=True, api_key=api_key)
    system_prompt = GENERAL_SYSTEM_PROMPT.format(logic=state.router["logic"])
    logging.info("---GENERATE GENERAL RESPONSE---")
    messages = [{"role": "system", "content": system_prompt}] + state.messages
    
    logging.info(f"General Query Messages: {messages}")
            
    response = await model.ainvoke(messages)
    
    # 输出日志但不修改response对象
    logging.info(f"General Response Type: {type(response)}")
    if hasattr(response, 'content'):
        logging.info(f"General Response Content Type: {type(response.content)}")
    
    # 直接返回response对象，不处理content
    return {"messages": [response]}

# 新增G4KG通用问题回答函数
async def respond_to_g4kg_general_query(
    state: AgentState, *, config: RunnableConfig
) -> dict[str, list[BaseMessage]]:
    """生成关于G4KG数据库的通用问题回答"""
        
    api_key = get_api_key_from_config(config)
    model = build_tongyi_llm(model=Chat_tongyi, temperature=TEMPERATURE, streaming=True, api_key=api_key)
    system_prompt = G4KG_GENERAL_SYSTEM_PROMPT.format(logic=state.router["logic"], schema=state.schema)
    logging.info("---GENERATE G4KG GENERAL RESPONSE---")
    messages = [{"role": "system", "content": system_prompt}] + state.messages
    
    logging.info(f"G4KG General Query Messages: {messages}")
            
    response = await model.ainvoke(messages)
    
    # 输出日志但不修改response对象
    logging.info(f"G4KG General Response Type: {type(response)}")
    if hasattr(response, 'content'):
        logging.info(f"G4KG General Response Content Type: {type(response.content)}")
    
    # 直接返回response对象，不处理content
    return {"messages": [response]}

async def respond(state: AgentState, *, config: RunnableConfig) -> dict[str, list[BaseMessage]]:
    """
    生成最后回答，不再做任何特定字段解析，而是把 documents 里的字典原样 JSON 序列化
    """
    logging.info("--- RESPONSE GENERATION STEP ---")
        
    api_key = get_api_key_from_config(config)
    model = build_tongyi_llm(model=Chat_tongyi, temperature=TEMPERATURE, streaming=True, api_key=api_key)

    # 1. 构造可读文档字符串
    try:
        # 确保documents有效
        documents = state.documents if state.documents else []
        
        # 重新格式化 context，使其更容易解析
        readable_context = "\n".join(
            [f"[{i + 1}] {json.dumps(doc, ensure_ascii=False)}" for i, doc in enumerate(documents)]
        )
        
        if not readable_context.strip():
            readable_context = "未找到相关文档"
            
        logging.info(f"Readable context prepared, length: {len(readable_context)}")
    except Exception as e:
        logging.error(f"构造文档字符串时出错: {e}")
        readable_context = "处理文档时出错"
    # 2. 放到 Prompt
    try:
        template = Template(RESPONSE_SYSTEM_PROMPT)
        cypher_query = state.cypher_query if state.cypher_query else ""
        prompt = template.render(context=readable_context, cypher=cypher_query)
        messages = [{"role": "system", "content": prompt}] + state.messages
    except Exception as e:
        logging.error(f"构建提示时出错: {str(e)}")
        # 使用简单提示作为后备
        messages = [{"role": "system", "content": "请根据用户问题提供有帮助的回答。"}] + state.messages

    # 3. 调用 LLM
    logging.info(f"Invoking LLM for final response")

    response = await model.ainvoke(messages)
    
    # 记录响应类型信息但不修改对象
    logging.info(f"Final Response Type: {type(response)}")
    if hasattr(response, 'content'):
        logging.info(f"Final Response Content Type: {type(response.content)}")
    
    return {"messages": [response]}

# 6. **状态图构建**

builder = StateGraph(AgentState, input=InputState)
builder.add_node(analyze_and_route_query)
builder.add_edge(START, "analyze_and_route_query")
builder.add_conditional_edges("analyze_and_route_query", route_query)

builder.add_node(conduct_search)
builder.add_node(ask_for_more_info)
builder.add_node(respond_to_general_query)
builder.add_node(respond_to_g4kg_general_query)
builder.add_node(rewrite_query)
builder.add_node("respond", respond)

builder.add_edge("rewrite_query","conduct_search")
builder.add_edge("conduct_search", "respond")
builder.add_edge("respond", END)
builder.add_edge("respond_to_g4kg_general_query", END)
builder.add_edge("respond_to_general_query", END)
builder.add_edge("ask_for_more_info", END)

# 通过`builder.compile()`编译图
graph = builder.compile(checkpointer=checkpointer)
# 模拟用户输入并执行

