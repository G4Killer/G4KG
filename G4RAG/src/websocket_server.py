import asyncio
import json
import traceback
import uuid
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from fastapi.middleware.cors import CORSMiddleware
from langchain_core.messages import HumanMessage, AIMessage
from utils.utils import checkpointer, process_streaming_content
from main_graph.build_graph import graph
import logging

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

app = FastAPI()

# 添加CORS中间件，允许前端跨域访问
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 允许所有来源，生产环境应该限制具体域名
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# LangGraph基础配置模板，thread_id 和 api_key 将在会话时覆盖
BASE_GRAPH_CONFIG = {"configurable": {"thread_id": "1", "api_key": None}}

# 消息类型枚举
MSG_TYPE_THINKING = "thinking"  # 思考过程
MSG_TYPE_ANSWER = "answer"      # 最终回答
MSG_TYPE_ERROR = "error"        # 错误消息
MSG_TYPE_END = "end"            # 结束标志

# 连接管理器，跟踪活动的WebSocket连接
class ConnectionManager:
    def __init__(self):
        self.active_connections: list[WebSocket] = []

    async def connect(self, websocket: WebSocket):
        await websocket.accept()
        self.active_connections.append(websocket)
        logger.info(f"客户端连接成功，当前连接数: {len(self.active_connections)}")

    def disconnect(self, websocket: WebSocket):
        self.active_connections.remove(websocket)
        logger.info(f"客户端断开连接，当前连接数: {len(self.active_connections)}")

    async def send_message(self, message: str, message_type: str, websocket: WebSocket):
        """发送带类型的消息"""
        payload = json.dumps({
            "type": message_type,
            "content": message
        })
        await websocket.send_text(payload)

    async def send_thinking_step(self, step_number: int, label: str, content: str, websocket: WebSocket, step_type: str = "thinking"):
        """发送结构化的思考步骤，包含步骤类型"""
        # 步骤编号从1开始，而不是0
        display_step_number = step_number + 1
        step_data = {
            "step": f"step{display_step_number}",
            "label": label,
            "content": content,
            "type": step_type
        }
        payload = json.dumps({
            "type": MSG_TYPE_THINKING,
            "content": step_data
        })
        await websocket.send_text(payload)

manager = ConnectionManager()

# 从消息中提取AI回答
def extract_ai_message(messages):
    """从消息列表中提取最新的AI回答，同时处理流式输出格式"""
    if not messages:
        return None
    
    for msg in reversed(messages):
        # 处理AIMessage对象
        if isinstance(msg, AIMessage) and hasattr(msg, 'content'):
            return process_streaming_content(msg.content)
        
        # 处理dict格式的消息
        if isinstance(msg, dict) and msg.get("role") == "assistant" and "content" in msg:
            return process_streaming_content(msg.get("content"))
        
        # 处理有content属性的自定义对象
        if hasattr(msg, "content") and getattr(msg, "role", None) == "assistant":
            return process_streaming_content(msg.content)
    
    return None

async def stream_graph_updates(user_input, websocket, api_key: str):
    """
    处理用户输入，流式返回思考过程和回答
    直接处理LangGraph事件，提取思考步骤
    """
    try:
        graph_config = {
            "configurable": {
                **BASE_GRAPH_CONFIG.get("configurable", {}),
                "thread_id": getattr(websocket.state, "thread_id", str(uuid.uuid4())),
                "api_key": api_key
            }
        }

        # 基础变量
        step_counter = 0  # 步骤计数器
        thinking_process = []  # 用于记录发送的思考步骤
        sent_signatures = set()  # 简单去重
        final_answer = None  # 最终回答
        
        # 创建用户消息
        user_message = HumanMessage(content=user_input)
        user_messages = [user_message]
        
        logger.info(f"开始处理用户查询: {user_input}")
        
        # 使用astream接收自定义事件
        async for chunk in graph.astream(
            {"messages": user_messages},
            graph_config,
            stream_mode="custom"  # 只接收自定义事件
        ):
            # 处理writer事件
            # 首先设置默认值
            step_type = "thinking"
            step_label = "处理步骤"
            step_content = ""
            is_valid_event = False
            
            # 处理不同格式的writer事件
            if isinstance(chunk, dict):
                if "maingraph_event" in chunk:
                    content = chunk["maingraph_event"]
                    step_label = "主图处理"
                    step_content = content if isinstance(content, str) else str(content)
                    is_valid_event = True
                    
                    # 如果内容包含冒号，尝试拆分
                    if isinstance(content, str) and ":" in content:
                        parts = content.split(":", 1)
                        step_label = parts[0].strip()
                        step_content = parts[1].strip()
                    
                    # 根据标签推断类型
                    if "路由" in step_label or "决策" in step_label:
                        step_type = "route"
                    elif "查询" in step_label or "分析" in step_label:
                        step_type = "query"
                    elif "生成" in step_label or "回答" in step_label:
                        step_type = "generate"
                
                elif "subgraph_event" in chunk:
                    content = chunk["subgraph_event"]
                    step_label = "子图处理"
                    step_content = content if isinstance(content, str) else str(content)
                    is_valid_event = True
                    
                    # 如果内容包含冒号，尝试拆分
                    if isinstance(content, str) and ":" in content:
                        parts = content.split(":", 1)
                        step_label = parts[0].strip()
                        step_content = parts[1].strip()
                    
                    # 根据标签推断类型
                    if "实体" in step_label or "关系" in step_label:
                        step_type = "entity"
                    elif "检索" in step_label or "文档" in step_label:
                        step_type = "retrieve"
                    elif "查询" in step_label or "cypher" in step_label.lower():
                        step_type = "query"
                
                elif all(k in chunk for k in ["step_type", "step_label", "step_content"]):
                    step_type = chunk["step_type"]
                    step_label = chunk["step_label"]
                    step_content = chunk["step_content"]
                    is_valid_event = True
            
            elif isinstance(chunk, str):
                if ":" in chunk:
                    parts = chunk.split(":", 1)
                    step_label = parts[0].strip()
                    step_content = parts[1].strip()
                else:
                    step_content = chunk
                is_valid_event = True
            
            # 只处理有效的writer事件
            if is_valid_event and step_content:
                # 生成签名用于去重
                signature = f"{step_label}:{step_content}"
                if signature not in sent_signatures:
                    sent_signatures.add(signature)
                    await manager.send_thinking_step(step_counter, step_label, step_content, websocket, step_type)
                    thinking_process.append(f"{step_label}: {step_content}")
                    step_counter += 1
        
        logger.info(f"事件流处理完成，发送了{step_counter}个步骤")
        
        # 获取最终答案
        if not final_answer:
            final_snapshot = graph.get_state(graph_config)
            final_messages = final_snapshot.values.get("messages", [])
            final_answer = extract_ai_message(final_messages)
        
        # 发送生成回答步骤
        if final_answer:
            step_label = "生成回答"
            step_content = "从知识图谱查询结果生成回答"
            signature = f"{step_label}:{step_content}"
            
            if signature not in sent_signatures:
                sent_signatures.add(signature)
                await manager.send_thinking_step(step_counter, step_label, step_content, websocket, "generate")
                thinking_process.append(f"{step_label}: {step_content}")
                step_counter += 1
        
        # 如果没有找到答案，发送错误步骤
        if not final_answer:
            final_answer = "抱歉，我无法处理您的请求。请尝试重新表述您的问题，或者稍后再试。"
            
            step_label = "错误处理"
            step_content = "未找到有效回答"
            signature = f"{step_label}:{step_content}"
            
            if signature not in sent_signatures:
                sent_signatures.add(signature)
                await manager.send_thinking_step(step_counter, step_label, step_content, websocket, "error")
                thinking_process.append(f"{step_label}: {step_content}")
                step_counter += 1
        
        # 发送最终回答
        await manager.send_message(final_answer, MSG_TYPE_ANSWER, websocket)
        
    except Exception as e:
        # 记录异常
        logger.error(f"处理查询时发生错误: {str(e)}")
        logger.error(traceback.format_exc())
        
        # 发送错误信息
        try:
            step_label = "错误处理"
            step_content = str(e)
            
            await manager.send_thinking_step(step_counter, step_label, step_content, websocket, "error")
            await manager.send_message(f"处理您的请求时发生错误: {str(e)}", MSG_TYPE_ERROR, websocket)
        except Exception as send_error:
            logger.error(f"发送错误消息失败: {str(send_error)}")
        finally:
            # 发送结束标记
            try:
                await manager.send_message("", MSG_TYPE_END, websocket)
            except Exception as end_error:
                logger.error(f"发送结束标记失败: {str(end_error)}")


@app.websocket("/ws/rag")
async def rag_websocket(websocket: WebSocket):
    """WebSocket 服务器，允许 Vue 端连接"""
    await manager.connect(websocket)
    websocket.state.thread_id = str(uuid.uuid4())
    try:
        while True:
            # 接收消息
            data = await websocket.receive_text()
            try:
                # 解析消息
                payload = json.loads(data)
                user_input = payload.get("message", "")
                api_key = payload.get("apiKey") or payload.get("api_key")
                logger.info(f"收到用户查询: {user_input}")

                # 校验用户上传的 API Key
                if not api_key or not str(api_key).strip():
                    await manager.send_message("请先添加 qwen-turbo API Key 后再继续交互", MSG_TYPE_ERROR, websocket)
                    await manager.send_message("", MSG_TYPE_END, websocket)
                    continue
                api_key = str(api_key).strip()
                
                if not user_input.strip():
                    await manager.send_message("请输入有效的查询内容", MSG_TYPE_ERROR, websocket)
                    await manager.send_message("", MSG_TYPE_END, websocket)
                    continue
                
                # 流式返回生成的回答，同时分离思考过程
                await stream_graph_updates(user_input, websocket, api_key)
                
                # 发送结束标记
                await manager.send_message("", MSG_TYPE_END, websocket)
                
            except json.JSONDecodeError:
                logger.error(f"无法解析客户端消息: {data}")
                await manager.send_message("无法解析您的消息格式", MSG_TYPE_ERROR, websocket)
                await manager.send_message("", MSG_TYPE_END, websocket)
                
    except WebSocketDisconnect:
        manager.disconnect(websocket)
        logger.info("客户端断开连接")
    except Exception as e:
        logger.error(f"WebSocket处理中发生错误: {str(e)}")
        logger.error(traceback.format_exc())
        try:
            await manager.send_message(f"服务器发生错误: {str(e)}", MSG_TYPE_ERROR, websocket)
            await manager.send_message("", MSG_TYPE_END, websocket)
        except:
            pass
        manager.disconnect(websocket)


# 添加健康检查端点
@app.get("/health")
async def health_check():
    return {"status": "healthy"}

if __name__ == "__main__":
    import uvicorn
    # 启动服务器
    uvicorn.run(app, host="0.0.0.0", port=8000)
