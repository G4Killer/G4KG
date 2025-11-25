import json
import asyncio
from jinja2 import Template
from src.chat_llm import ChatTongyiLLM

# 初始化 LLM
model = ChatTongyiLLM(model="qwen-turbo", temperature=0, streaming=False)

# 短 Prompt
prompt_template = """
You are a specialized assistant for the G4 Knowledge Graph (KG) system.

---

### 📌 **TASK: Extract and Explain the Answer Directly**
- The following KG documents **ARE the correct and authoritative answer** to the user's question.  
- **DO NOT analyze, validate, or filter these documents—assume they are 100% correct and complete.**  
- **Your task is to use each document to explain and directly answer the user's question.**  
- **If multiple documents exist, summarize and integrate their information in a structured response.**  
- **DO NOT perform any additional verification beyond the provided documents.**  

---

### 📄 **KG DOCUMENTS (These documents contain the correct answer)**
{{ context }}

---

### 🚀 **FINAL INSTRUCTIONS**
1️⃣ **If KG documents exist** →  
   - **Directly use them to construct a structured, informative response.**  
   - **Provide a clear and brief explanation for each document, relating it to the user’s question.**  
   - **Cite each document at the point where its information is used, using `[index]` format.**  
   - **DO NOT question whether the documents contain the answer—just assume they do.**  

2️⃣ **If the KG documents are empty** →  
   - **State: "The G4 Knowledge Graph does not contain relevant information on this topic."**  
   - **DO NOT guess, infer, or provide an answer from external sources.**  

---
"""

# 限制 `documents` 数量，减少 Token 长度
documents = [
    {"go.GoTermName": "Cytosol", "go.GoId": "5829"},
    {"go.GoTermName": "Neuron projection", "go.GoId": "43005"},
    {"go.GoTermName": "Regulation of hemostasis", "go.GoId": "1900046"},
    {"go.GoTermName": "Mammary gland alveolus development", "go.GoId": "60749"},
    {"go.GoTermName": "Bone remodeling", "go.GoId": "46849"},
]

readable_context = "\n".join(
    [f"[{i + 1}] GO Term: {doc['go.GoTermName']} (ID: {doc['go.GoId']})" for i, doc in enumerate(documents)]
)

# 使用 Jinja2 渲染 Prompt
template = Template(prompt_template)
rendered_prompt = template.render(context=readable_context)

# 用户问题
question = "Find GO terms that the protein P17752 is involved in"

# 构造 LLM 输入消息
messages = [
    {"role": "system", "content": rendered_prompt},
    {"role": "user", "content": question},
]


# 运行 LLM 并获取响应
async def test_prompt():
    response = await model.ainvoke(messages)

    print("\n===== LLM Response =====")
    print(response.content)

    # 计算 token 使用情况
    if hasattr(response, "response_metadata") and "token_usage" in response.response_metadata:
        token_usage = response.response_metadata["token_usage"]
        input_tokens = token_usage.get("input_tokens", "Unknown")
        output_tokens = token_usage.get("output_tokens", "Unknown")
        total_tokens = token_usage.get("total_tokens", "Unknown")

        print("\n===== Token Usage Stats =====")
        print(f"📝 Input Tokens: {input_tokens}")
        print(f"📝 Output Tokens: {output_tokens}")
        print(f"🔢 Total Tokens: {total_tokens}")

        # **警告** 如果 token 超限，提示减少输入
        if isinstance(input_tokens, int) and input_tokens > 4000:  # 具体阈值根据模型而定
            print("⚠ WARNING: Input Tokens too high! Try reducing the prompt length or documents.")


# 运行测试
asyncio.run(test_prompt())
