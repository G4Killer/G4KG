import json
import re

test_responses = [
    'json\n{\n  "logic": "The query is about a disease.",\n  "type": "KG_query"\n}',
    '```json\n{\n  "logic": "This is a vague question.",\n  "type": "more-info"\n}\n```',
    '{\n  "logic": "A general inquiry.",\n  "type": "general"\n}',
    'invalid json string'
]

for test in test_responses:
    print("\n🔍 原始 JSON:")
    print(test)

    cleaned = re.sub(r"^```json\s*|\s*```$", "", test, flags=re.MULTILINE)  # 去除 ```json
    cleaned = re.sub(r"^\s*json\s*", "", cleaned, flags=re.MULTILINE)  # 去除 json\n
    cleaned = cleaned.strip()  # 去除空格

    print("\n✅ 清理后的 JSON:")
    print(cleaned)

    try:
        parsed = json.loads(cleaned)
        print(f"\n✅ 解析成功: {parsed}")
    except json.JSONDecodeError:
        print(f"\n❌ JSON 解析失败: {cleaned}")
