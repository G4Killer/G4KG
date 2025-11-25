import asyncio

from main_graph.graph_states import Router
from prompts.prompt_templates import SUBGRAPH_ROUTER_SYSTEM_PROMPT, ROUTER_SYSTEM_PROMPT
from src.chat_llm import ChatTongyiLLM
from sub_graph.subgraph_states import SubRouter


async def test_router():
    model = ChatTongyiLLM(model="qwen-turbo", temperature=0)
    messages = [
        {"role": "system", "content": SUBGRAPH_ROUTER_SYSTEM_PROMPT},
        {"role": "human", "content": "hello"},
    ]

    response = await model.ainvoke(messages)
    print("LLM Response:", response)

asyncio.run(test_router())
