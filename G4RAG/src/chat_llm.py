# chat_llm.py
from langchain_community.chat_models.tongyi import ChatTongyi

class ChatTongyiLLM(ChatTongyi):
    @property
    def _llm_type(self) -> str:
        return "chat-tongyi"


