import asyncio
import traceback

from langchain_core.messages import trim_messages, HumanMessage

from utils.utils import checkpointer
from main_graph.build_graph import graph  # 确保 `graph` 正确初始化
from utils.vectorstore_config import close_weaviate

config = {"configurable": {"thread_id": "1"}}
MAX_MESSAGES = 5  # ✅ 只保留最近 5 轮对话


async def stream_graph_updates(user_input: str):
    """官方方式实现多轮对话，且修剪历史消息条数"""

    # ✅ 获取历史 state
    snapshot = graph.get_state(config)
    old_messages = snapshot.values.get("messages", [])

    # ✅ 追加新的用户消息
    all_messages = old_messages + [HumanMessage(content=user_input)]

    # ✅ 修剪历史对话（仅保留最近 5 条）
    trimmed_messages = trim_messages(
        all_messages,
        strategy="last",
        token_counter=len,  # ✅ 计算消息数
        max_tokens=5,  # ✅ 限制最多 5 条消息
        start_on="human",
        end_on=("human",),
        include_system=False,
    )

    # ✅ **调试输出**
    # print(f"🔹 修剪前对话记录: {len(all_messages)} 条")
    # for msg in all_messages:
    #    print(f"    [{msg.type}] {msg.content}")  # ✅ 修正访问方式

    # print(f"✅ 修剪后对话记录: {len(trimmed_messages)} 条")
    # for msg in trimmed_messages:
    #    print(f"    [{msg.type}] {msg.content}")  # ✅ 修正访问方式

    # ✅ 传递修剪后的消息
    async for event in graph.astream(
            {"messages": trimmed_messages}, config, stream_mode="values"
    ):
        last_event = event  # 取最后的 AI 回复

    last_event["messages"][-1].pretty_print()  # ✅ 确保 AI 回复输出


async def main():
    """主交互循环"""
    try:
        while True:
            user_input = input("User: ").strip()
            if user_input.lower() in ["quit", "exit", "q"]:
                print("Goodbye!")
                break

            await stream_graph_updates(user_input)  # 调用更新函数

    except Exception:
        print("An error occurred!")
        traceback.print_exc()
    finally:
        close_weaviate()  # 关闭 Weaviate 连接

if __name__ == "__main__":
    asyncio.run(main())
