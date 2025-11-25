<template>
    <div class="chatbot-container">
      <!-- 页面标题 -->
      <div class="header-content">
        <h1>G4KG 智能助手</h1>
        <p>这是基于RAG技术的智能问答系统，可以回答关于G4KG数据库相关的问题</p>
      </div>
  
      <!-- 聊天主体区域 -->
      <div class="chat-section">
        <!-- 聊天框 -->
        <div class="chat-container">
          <!-- 消息显示区域 -->
          <div class="chat-messages" ref="messagesContainer">
            <!-- 欢迎消息 -->
            <div v-if="messages.length === 0" class="welcome-message">
              <h3>欢迎使用G4KG智能助手</h3>
              <p>您可以询问有关G4KG数据库、G-四联体结构以及相关知识的问题。</p>
              <div class="example-queries">
                <h4>示例问题：</h4>
                <ul>
                  <li @click="setExampleQuery('G-四联体结构是什么？')">G-四联体结构是什么？</li>
                  <li @click="setExampleQuery('G4KG数据库包含哪些主要实体类型？')">G4KG数据库包含哪些主要实体类型？</li>
                  <li @click="setExampleQuery('如何使用G4KG数据库进行查询？')">如何使用G4KG数据库进行查询？</li>
                </ul>
              </div>
            </div>
  
            <!-- 对话消息 -->
            <div
                v-for="(msg, index) in messages"
                :key="index"
                :class="[
                'message',
                msg.sender === 'user' ? 'user-message' : 'bot-message'
              ]"
            >
              <p class="message-content">{{ msg.text }}</p>
  
              <!-- 思考过程部分（仅在机器人消息且有思考过程时显示） -->
              <div v-if="msg.sender === 'bot' && msg.thinking && msg.thinking.length > 0" class="thinking-process">
                <div class="thinking-header" @click="toggleThinking(index)">
                  <span>查看详细推理过程 ({{ msg.thinking.length }}步)</span>
                  <i :class="['thinking-icon', showThinking[index] ? 'expanded' : '']">▼</i>
                </div>
                <div v-if="showThinking[index]" class="thinking-content">
                  <div v-for="(step, stepIndex) in msg.thinking" :key="stepIndex" class="thinking-step">
                    {{ step }}
                  </div>
                </div>
              </div>
            </div>
  
            <!-- 加载指示器 -->
            <div v-if="loading" class="loading-indicator">
              <span></span><span></span><span></span>
            </div>
          </div>
  
          <!-- 输入和发送按钮 -->
          <div class="chat-input">
            <el-input
                v-model="userMessage"
                placeholder="请输入您的问题..."
                :disabled="loading"
                clearable
                class="input-field"
                @keyup.enter="sendMessage"
            />
            <el-button
                type="primary"
                @click="sendMessage"
                :disabled="loading || !userMessage.trim()"
                :loading="loading"
            >
              发送
            </el-button>
            <el-button
                type="default"
                @click="clearMessages"
                :disabled="messages.length === 0"
            >
              清空对话
            </el-button>
          </div>
        </div>
  
        <!-- 侧边帮助信息 -->
        <div class="side-info">
          <div class="info-card">
            <h3>关于此智能助手</h3>
            <p>此智能助手基于检索增强生成（RAG）技术，能够根据G4KG知识库内容回答相关问题。</p>
          </div>
          <div class="info-card">
            <h3>知识范围</h3>
            <ul class="knowledge-scope">
              <li><i class="el-icon-document"></i> G-四联体结构和功能</li>
              <li><i class="el-icon-document"></i> G4KG数据库内容和使用方法</li>
              <li><i class="el-icon-document"></i> 相关基因、蛋白质和生物学通路</li>
              <li><i class="el-icon-document"></i> 数据分析和解释方法</li>
            </ul>
          </div>
        </div>
      </div>
  
      <!-- 连接状态指示器 -->
      <div v-if="connectionStatus" :class="['connection-status', connectionClass]">
        {{ connectionStatus }}
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, nextTick, onBeforeUnmount, onMounted, computed } from 'vue';
  
  // 数据、方法
  const userMessage = ref('');
  const messages = ref([]);
  const loading = ref(false);
  const messagesContainer = ref(null);
  const showThinking = reactive({});  // 控制每条消息是否显示思考过程
  const currentThinkingSteps = ref([]); // 当前消息的思考步骤（改为ref类型）
  const wsState = ref(0); // WebSocket连接状态
  const pendingMessage = ref(null); // 待发送的消息
  const connectionStatus = ref(''); // 连接状态提示
  const connectionTimer = ref(null); // 用于临时显示连接状态的定时器
  const maxReconnectAttempts = 3; // 最大重连次数
  const reconnectAttempts = ref(0); // 当前重连次数
  let ws = null;
  
  // 连接状态样式
  const connectionClass = computed(() => {
    if (wsState.value === 1) return 'connected';
    if (wsState.value === 0) return 'connecting';
    return 'disconnected';
  });
  
  // 显示连接状态
  const showConnectionStatus = (message, duration = 3000) => {
    connectionStatus.value = message;
  
    // 清除之前的定时器
    if (connectionTimer.value) {
      clearTimeout(connectionTimer.value);
    }
  
    // 设置新的定时器
    connectionTimer.value = setTimeout(() => {
      connectionStatus.value = '';
    }, duration);
  };
  
  // 设置示例查询到输入框
  const setExampleQuery = (query) => {
    userMessage.value = query;
  };
  
  // 切换显示/隐藏思考过程
  const toggleThinking = (messageIndex) => {
    if (showThinking[messageIndex]) {
      delete showThinking[messageIndex];
    } else {
      showThinking[messageIndex] = true;
    }
  };
  
  // 初始化WebSocket连接
  const initWebSocket = () => {
    if (!ws || ws.readyState !== WebSocket.OPEN) {
      // 关闭之前的连接
      if (ws) {
        try {
          ws.close();
        } catch (e) {
          console.error("关闭旧连接时出错:", e);
        }
      }
  
      wsState.value = 0; // 连接中
      showConnectionStatus('正在连接服务器...');
  
      ws = new WebSocket("ws://localhost:8000/ws/rag");
  
      ws.onopen = () => {
        console.log("WebSocket连接已建立");
        wsState.value = 1; // 已连接
        showConnectionStatus('已连接到服务器', 1500);
        reconnectAttempts.value = 0; // 重置重连次数
  
        // 如果有待发送的消息，连接成功后发送
        if (pendingMessage.value) {
          sendMessageToServer(pendingMessage.value);
          pendingMessage.value = null;
        }
      };
  
      ws.onmessage = (event) => {
        try {
          console.log("收到WebSocket消息:", event.data);
          const data = JSON.parse(event.data);
          const messageType = data.type;
          const content = data.content;
  
          // 根据消息类型处理
          switch (messageType) {
            case 'thinking':
              // 收集思考过程
              console.log("收到思考过程:", content);
              currentThinkingSteps.value.push(content);
              break;
  
            case 'answer':
              // 添加AI回复，并附带收集到的思考步骤
              if (content) {
                console.log(`添加回答，思考步骤数量: ${currentThinkingSteps.value.length}`);
                console.log("思考步骤内容:", [...currentThinkingSteps.value]);
                const newIndex = messages.value.length;
                messages.value.push({
                  text: content,
                  sender: 'bot',
                  thinking: [...currentThinkingSteps.value] // 复制收集到的思考步骤
                });
                // 默认展开思考过程
                if (currentThinkingSteps.value.length > 0) {
                  showThinking[newIndex] = true;
                }
                scrollToBottom();
              }
              currentThinkingSteps.value = []; // 清空思考步骤
              break;
  
            case 'error':
              // 显示错误消息
              messages.value.push({
                text: content || "发生错误，请重试",
                sender: 'bot',
                thinking: [...currentThinkingSteps.value],
                isError: true
              });
              currentThinkingSteps.value = [];
              scrollToBottom();
              break;
  
            case 'end':
              // 接收到结束标记，结束加载状态
              loading.value = false;
              break;
  
            default:
              console.warn(`未知消息类型: ${messageType}`);
          }
        } catch (error) {
          console.error("解析WebSocket消息时出错:", error, event.data);
  
          // 兼容旧版格式（纯文本）
          if (event.data === "[END]") {
            loading.value = false;
          } else {
            // 如果不是JSON或END标记，视为普通文本回复
            messages.value.push({
              text: event.data,
              sender: 'bot',
              thinking: []
            });
            scrollToBottom();
          }
        }
      };
  
      ws.onerror = (error) => {
        console.error("WebSocket错误:", error);
        wsState.value = 3; // 错误
        showConnectionStatus('连接服务器出错，尝试重新连接...', 5000);
  
        if (loading.value) {
          messages.value.push({
            text: "连接服务器时出现错误，请稍后再试。",
            sender: 'bot',
            thinking: currentThinkingSteps.value.length > 0 ? [...currentThinkingSteps.value] : [],
            isError: true
          });
          currentThinkingSteps.value = [];
          loading.value = false;
          scrollToBottom();
        }
      };
  
      ws.onclose = (event) => {
        console.log("WebSocket连接已关闭", event);
        wsState.value = 3; // 已关闭
  
        // 如果不是正常关闭且重连次数未超限，尝试重连
        if (event.code !== 1000 && reconnectAttempts.value < maxReconnectAttempts) {
          const delay = Math.min(1000 * Math.pow(2, reconnectAttempts.value), 10000);
          reconnectAttempts.value++;
  
          showConnectionStatus(`连接已断开，${delay/1000}秒后重试 (${reconnectAttempts.value}/${maxReconnectAttempts})`, delay + 500);
  
          setTimeout(() => {
            console.log(`尝试第${reconnectAttempts.value}次重连...`);
            initWebSocket();
          }, delay);
        } else if (reconnectAttempts.value >= maxReconnectAttempts) {
          showConnectionStatus('连接已断开，请刷新页面重试', 0);
        }
      };
    }
  
    return ws;
  };
  
  // 发送消息
  const sendMessage = async () => {
    if (!userMessage.value.trim() || loading.value) return;
  
    loading.value = true;
  
    // 添加用户消息到对话
    messages.value.push({
      text: userMessage.value.trim(),
      sender: 'user',
      thinking: []
    });
  
    // 准备保存当前AI回复的思考步骤
    currentThinkingSteps.value = [];
  
    // 保存用户消息
    const messageToSend = userMessage.value.trim();
  
    // 清空输入框
    userMessage.value = '';
    scrollToBottom();
  
    // 确保WebSocket连接
    const websocket = initWebSocket();
  
    // 如果WebSocket已连接，直接发送；否则保存等待连接成功后发送
    if (websocket && websocket.readyState === WebSocket.OPEN) {
      sendMessageToServer(messageToSend);
    } else {
      pendingMessage.value = messageToSend;
      showConnectionStatus('正在连接服务器...', 5000);
    }
  };
  
  // 向服务器发送消息
  const sendMessageToServer = (message) => {
    if (ws && ws.readyState === WebSocket.OPEN) {
      console.log("发送消息到服务器:", message);
      ws.send(JSON.stringify({ message: message }));
    } else {
      console.error("WebSocket未连接，无法发送消息");
      pendingMessage.value = message;
      initWebSocket(); // 尝试重新连接
    }
  };
  
  // 清空对话
  const clearMessages = () => {
    messages.value = [];
    // 正确清空reactive对象
    Object.keys(showThinking).forEach(key => {
      delete showThinking[key];
    });
    // 清空思考步骤
    currentThinkingSteps.value = [];
  };
  
  // 滚动到底部
  const scrollToBottom = async () => {
    await nextTick();
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  };
  
  // 组件挂载时的操作
  onMounted(() => {
    // 初始化WebSocket连接
    initWebSocket();
  });
  
  // 组件卸载前关闭WebSocket连接
  onBeforeUnmount(() => {
    if (connectionTimer.value) {
      clearTimeout(connectionTimer.value);
    }
  
    if (ws) {
      ws.onclose = null; // 移除onclose处理程序，防止组件卸载时触发重连
      ws.close();
      ws = null;
    }
  });
  </script>
  
  <style scoped>
  .chatbot-container {
    margin: 0;
    padding: 20px;
    min-height: calc(100vh - 110px);
    background: url('@/assets/photo/background.jpg') no-repeat center center fixed;
    background-size: cover;
    display: flex;
    flex-direction: column;
    align-items: center;
    color: white;
    position: relative;
  }
  
  /* 连接状态指示器 */
  .connection-status {
    position: fixed;
    top: 10px;
    right: 10px;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 12px;
    z-index: 1000;
    opacity: 0.8;
    transition: opacity 0.3s;
  }
  
  .connection-status.connected {
    background-color: rgba(46, 125, 50, 0.7);
    color: white;
  }
  
  .connection-status.connecting {
    background-color: rgba(245, 124, 0, 0.7);
    color: white;
  }
  
  .connection-status.disconnected {
    background-color: rgba(211, 47, 47, 0.7);
    color: white;
  }
  
  /* 页面标题样式 */
  .header-content {
    margin-top: 10px;
    margin-bottom: 20px;
    text-align: center;
    width: 80%;
  }
  
  .header-content h1 {
    font-size: 2.5rem;
    margin-bottom: 10px;
    font-weight: bold;
  }
  
  .header-content p {
    font-size: 1.2rem;
    line-height: 1.6;
  }
  
  /* 聊天部分布局 */
  .chat-section {
    display: flex;
    width: 90%;
    max-width: 1400px;
    gap: 20px;
  }
  
  /* 聊天框主体 */
  .chat-container {
    flex: 3;
    background-color: rgba(255, 255, 255, 0.9);
    border-radius: 10px;
    box-shadow: 0 2px 15px rgba(0, 0, 0, 0.2);
    display: flex;
    flex-direction: column;
    padding: 20px;
    height: 70vh;
    overflow: hidden;
  }
  
  /* 侧边信息区域 */
  .side-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .info-card {
    background-color: rgba(255, 255, 255, 0.9);
    border-radius: 10px;
    padding: 15px;
    box-shadow: 0 2px 15px rgba(0, 0, 0, 0.2);
    color: #333;
  }
  
  .info-card h3 {
    color: #2c6a7f;
    border-bottom: 1px solid #eee;
    padding-bottom: 8px;
    margin-bottom: 10px;
  }
  
  .knowledge-scope {
    padding-left: 15px;
  }
  
  .knowledge-scope li {
    margin-bottom: 8px;
    display: flex;
    align-items: center;
  }
  
  /* 消息显示区域，支持滚动 */
  .chat-messages {
    flex: 1;
    overflow-y: auto;
    margin-bottom: 16px;
    padding: 10px 15px;
    display: flex;
    flex-direction: column;
    color: #333;
  }
  
  /* 欢迎消息 */
  .welcome-message {
    background-color: rgba(44, 106, 127, 0.1);
    border-left: 4px solid #2c6a7f;
    padding: 15px;
    margin-bottom: 20px;
    border-radius: 4px;
  }
  
  .welcome-message h3 {
    color: #2c6a7f;
    margin-bottom: 10px;
  }
  
  .example-queries {
    margin-top: 15px;
  }
  
  .example-queries h4 {
    margin-bottom: 8px;
    font-weight: normal;
    color: #666;
  }
  
  .example-queries ul {
    list-style: none;
    padding: 0;
  }
  
  .example-queries li {
    background-color: #f0f5f7;
    padding: 10px;
    margin-bottom: 5px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;
  }
  
  .example-queries li:hover {
    background-color: #e0edf2;
  }
  
  /* 每条消息的公共样式 */
  .message {
    margin-bottom: 15px;
    max-width: 80%;
    padding: 12px 16px;
    border-radius: 8px;
    line-height: 1.5;
    word-wrap: break-word;
    white-space: pre-wrap;
    position: relative;
    animation: fadeIn 0.3s ease-out;
  }
  
  @keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
  }
  
  /* 用户消息样式 */
  .user-message {
    align-self: flex-end;
    background-color: #2c6a7f;
    color: #fff;
    border-top-right-radius: 0;
  }
  
  /* 机器人消息样式 */
  .bot-message {
    align-self: flex-start;
    background-color: #f2f2f2;
    color: #333;
    border-top-left-radius: 0;
  }
  
  /* 错误消息样式 */
  .bot-message.isError {
    background-color: #fff0f0;
    border-left: 3px solid #d32f2f;
  }
  
  /* 思考过程样式 */
  .thinking-process {
    margin-top: 10px;
    border-top: 1px dashed #ddd;
    font-size: 0.9em;
    background-color: rgba(240, 248, 255, 0.5);
    border-radius: 6px;
    padding: 5px;
  }
  
  .thinking-header {
    padding: 5px 0;
    color: #2c6a7f;
    font-weight: bold;
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .thinking-icon {
    font-size: 12px;
    transition: transform 0.3s;
  }
  
  .thinking-icon.expanded {
    transform: rotate(180deg);
  }
  
  .thinking-content {
    background-color: rgba(240, 245, 249, 0.5);
    border-radius: 6px;
    padding: 8px;
    margin-top: 5px;
  }
  
  .thinking-step {
    padding: 3px 0;
    font-family: monospace;
    color: #555;
  }
  
  /* 加载指示器 */
  .loading-indicator {
    align-self: flex-start;
    display: flex;
    margin: 10px 0;
  }
  
  .loading-indicator span {
    width: 10px;
    height: 10px;
    margin: 0 3px;
    background-color: #2c6a7f;
    border-radius: 50%;
    display: inline-block;
    animation: loading 1.4s infinite ease-in-out both;
  }
  
  .loading-indicator span:nth-child(1) {
    animation-delay: -0.32s;
  }
  
  .loading-indicator span:nth-child(2) {
    animation-delay: -0.16s;
  }
  
  @keyframes loading {
    0%, 80%, 100% { transform: scale(0); }
    40% { transform: scale(1.0); }
  }
  
  /* 输入框和发送按钮区域 */
  .chat-input {
    display: flex;
    align-items: center;
    gap: 10px;
    padding-top: 10px;
    border-top: 1px solid #eee;
  }
  
  .input-field {
    flex: 1;
  }
  
  /* 响应式设计 */
  @media (max-width: 992px) {
    .chat-section {
      flex-direction: column;
    }
  
    .chat-container {
      width: 100%;
    }
  
    .side-info {
      width: 100%;
      flex-direction: row;
      max-height: 200px;
    }
  
    .info-card {
      flex: 1;
    }
  }
  
  @media (max-width: 768px) {
    .side-info {
      flex-direction: column;
      max-height: none;
    }
  
    .chat-container {
      height: 60vh;
    }
  
    .header-content h1 {
      font-size: 2rem;
    }
  }
  </style>