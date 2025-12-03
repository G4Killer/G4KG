# chat_llm.py
from typing import Optional

from langchain_community.chat_models.tongyi import ChatTongyi
from langchain_core.runnables import RunnableConfig


class ChatTongyiLLM(ChatTongyi):
    @property
    def _llm_type(self) -> str:
        return "chat-tongyi"


def get_api_key_from_config(runnable_config: Optional[RunnableConfig]) -> Optional[str]:
    """
    从 LangGraph 的 runnable config 中提取用户上传的 API Key。
    约定放在 configurable.api_key 下，缺失则返回 None。
    """
    if not runnable_config:
        return None
    try:
        configurable = runnable_config.get("configurable", {})
        return configurable.get("api_key")
    except Exception:
        return None


def build_tongyi_llm(model: str, temperature: float, streaming: bool, api_key: Optional[str] = None) -> ChatTongyiLLM:
    """
    构造带可选 dashscope_api_key 的通义千问聊天模型。
    """
    params = {
        "model": model,
        "temperature": temperature,
        "streaming": streaming,
    }
    if api_key:
        params["dashscope_api_key"] = api_key
    return ChatTongyiLLM(**params)

