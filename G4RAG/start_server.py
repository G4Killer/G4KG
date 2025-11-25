#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
启动WebSocket服务器，为Vue3前端提供RAG查询服务
"""

import uvicorn
import logging
from src.websocket_server import app

# 配置日志
logging.basicConfig(
    level=logging.INFO,  # 保持INFO级别
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler("server.log")
    ]
)

# 减少第三方库的日志输出
logging.getLogger("uvicorn").setLevel(logging.WARNING)
logging.getLogger("uvicorn.access").setLevel(logging.WARNING)
logging.getLogger("weaviate").setLevel(logging.WARNING)
logging.getLogger("langchain").setLevel(logging.WARNING)
logging.getLogger("httpx").setLevel(logging.WARNING)
logging.getLogger("neo4j").setLevel(logging.WARNING)

logger = logging.getLogger(__name__)

def main():
    """启动FastAPI应用服务器"""
    logger.info("启动WebSocket RAG服务器...")
    
    # 使用uvicorn启动FastAPI应用
    uvicorn.run(
        app,
        host="0.0.0.0",  # 监听所有网络接口
        port=8000,       # 使用8000端口
        reload=False,    # 生产环境关闭自动重载功能
        log_level="warning"  # 降低uvicorn日志级别
    )

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        logger.info("服务器被手动停止")
    except Exception as e:
        logger.error(f"服务器发生错误: {str(e)}", exc_info=True) 