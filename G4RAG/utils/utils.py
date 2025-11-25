import yaml
from langgraph.checkpoint.memory import MemorySaver
import logging

def load_config(file_path="config.yaml"):
    """Loads the configuration from the YAML file."""
    config_path = "C:/Work/Pycharm Code/G4RAG/config.yaml"  # 直接给出配置文件的完整路径
    with open(config_path, 'r', encoding='utf-8') as file:
        config = yaml.safe_load(file)
    return config

# 处理流式输出内容的公共方法
def process_streaming_content(content):
    """
    处理通义千问等LLM的流式输出内容
    
    Args:
        content: LLM返回的内容，可能是字符串或包含'text'键的字典列表
        
    Returns:
        str: 处理后的字符串内容
    """
    if content is None:
        return ""
        
    if isinstance(content, list):
        # 流式输出模式：列表中每个元素是包含'text'键的字典
        try:
            return ''.join([item.get('text', '') for item in content])
        except Exception as e:
            logging.error(f"处理流式内容错误: {e}")
            # 如果处理失败，尝试转换为字符串
            return str(content)
    
    # 如果是字符串，直接返回strip后的结果
    if hasattr(content, 'strip'):
        return content.strip()
    
    # 其他情况转为字符串
    return str(content)

# Load the configuration file and make it available globally
config = load_config()

checkpointer = MemorySaver()