#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
WebSocket客户端测试脚本，用于测试RAG WebSocket服务器
"""

import asyncio
import json
import websockets
import logging

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)
# 减少websockets库的日志输出
logging.getLogger("websockets").setLevel(logging.WARNING)

# WebSocket服务器地址
WS_URL = "ws://localhost:8000/ws/rag"

async def test_connection():
    """测试WebSocket连接"""
    try:
        async with websockets.connect(WS_URL) as websocket:
            logger.info(f"已连接到服务器: {WS_URL}")
            
            # 测试消息
            test_queries = [
                "G-四联体结构是什么？",
                "G4KG数据库包含哪些主要实体类型？",
                "如何使用G4KG数据库进行查询？"
            ]
            
            # 选择一个测试查询
            test_query = test_queries[0]
            logger.info(f"发送测试查询: {test_query}")
            
            # 发送消息
            await websocket.send(json.dumps({"message": test_query}))
            
            # 接收并处理响应
            complete_response = []
            while True:
                response = await websocket.recv()
                if response == "[END]":
                    logger.info("接收完成 [END] 标记")
                    break
                # 减少日志输出，只输出收到响应的消息，不输出响应内容
                logger.info("收到响应片段...")
                complete_response.append(response)
            
            # 输出完整响应
            full_response = "".join(complete_response)
            logger.info(f"响应总长度: {len(full_response)} 字符")
            logger.info("测试完成")
    
    except ConnectionRefusedError:
        logger.error(f"无法连接到服务器: {WS_URL}")
        logger.error("请确保WebSocket服务器已启动")
    except Exception as e:
        logger.error(f"测试过程中发生错误: {str(e)}", exc_info=True)

if __name__ == "__main__":
    try:
        # 运行测试
        asyncio.run(test_connection())
    except KeyboardInterrupt:
        logger.info("测试被手动终止")
    except Exception as e:
        logger.error(f"发生错误: {str(e)}", exc_info=True) 