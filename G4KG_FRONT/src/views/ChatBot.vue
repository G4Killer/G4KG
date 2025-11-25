<template>
  <div class="chatbot-container">
    <!-- Page Title -->
    <div class="header-content">
      <h1>G4KG Intelligent Assistant</h1>
      <p>A RAG-based intelligent Q&A system that answers questions about G4KG database</p>
    </div>

    <!-- Chat Main Area -->
    <div class="chat-section">
      <!-- Chat Box -->
      <div class="chat-container">
        <!-- Message Display Area -->
        <div class="chat-messages" ref="messagesContainer">
          <!-- Welcome Message -->
          <div v-if="messages.length === 0" class="welcome-message">
            <h3>Welcome to G4KG Intelligent Assistant</h3>
            <p>You can ask questions about G4KG database, G-quadruplex structures, and related knowledge.</p>
            <div class="example-queries">
              <ul>
                <li v-for="(query, index) in exampleQueries" :key="index" @click="setExampleQuery(query)">{{ query }}</li>
              </ul>
            </div>
          </div>

          <!-- Conversation Messages -->
          <template v-for="(msg, index) in messages" :key="index">
            <!-- User Message -->
            <div v-if="msg.sender === 'user'" class="message user-message">
              <p>{{ msg.text }}</p>
            </div>

            <!-- Current Live Reasoning Process -->
            <div v-if="index === messages.length - 1 && msg.sender === 'user' && liveThinkingProcess.length > 0"
                 class="thinking-process live" ref="thinkingContainer">
              <div class="thinking-header">Reasoning Process ({{ liveThinkingProcess.length }} steps)</div>
              <div class="thinking-steps">
                <div v-for="(step, stepIndex) in liveThinkingProcess" :key="stepIndex"
                     class="thinking-step" :class="[getStepTypeClass(step), step.type ? `step-${step.type}` : '']">
                  <div class="step-header">
                    <span class="step-number">{{ step.step }}</span>
                    <span class="step-name">{{ step.label }}</span>
                  </div>
                  <div class="step-content">{{ step.content }}</div>
                </div>
              </div>
            </div>

            <!-- Bot Response -->
            <div v-if="msg.sender === 'bot'" class="message bot-message" :class="{'error': msg.isError}">
              <p>{{ msg.text }}</p>

              <!-- Reasoning Process Section -->
              <div v-if="msg.thinking?.length > 0" class="thinking-toggle">
                <div class="toggle-header" @click="toggleThinking(index)">
                  <span>Reasoning Process ({{ msg.thinking.length }} steps)</span>
                  <i :class="['toggle-icon', showThinking[index] ? 'expanded' : '']">▼</i>
                </div>
                <div v-if="showThinking[index]" class="thinking-steps">
                  <div v-for="(step, stepIndex) in msg.thinking" :key="stepIndex"
                       class="thinking-step" :class="[getStepTypeClass(step), step.type ? `step-${step.type}` : '']">
                    <div class="step-header">
                      <span class="step-number">{{ step.step }}</span>
                      <span class="step-name">{{ step.label }}</span>
                    </div>
                    <div class="step-content">{{ step.content }}</div>
                  </div>
                </div>
              </div>
            </div>
          </template>

          <!-- Loading Indicator -->
          <div v-if="loading && liveThinkingProcess.length === 0" class="loading-indicator">
            <span></span><span></span><span></span>
          </div>
        </div>

        <!-- Input and Send Buttons -->
        <div class="input-area">
          <el-input v-model="userMessage" placeholder="Enter your question..." :disabled="loading" 
                  clearable @keyup.enter="sendMessage" />
          <el-button type="primary" @click="sendMessage" :disabled="loading || !userMessage.trim()" :loading="loading">Send</el-button>
          <el-button @click="clearMessages" :disabled="messages.length === 0">Clear</el-button>
        </div>
      </div>
    </div>

    <!-- New Thinking Step Indicator -->
    <div v-if="showNewThinkingIndicator" class="new-thinking-indicator" @click="scrollToThinking">
      <span>New reasoning steps</span> <i>↓</i>
    </div>
    
    <!-- Connection Status Indicator -->
    <div v-if="connectionStatus" :class="['connection-status', connectionClass]">
      {{ connectionStatus }}
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onBeforeUnmount, onMounted, computed } from 'vue';

// Basic Data
const userMessage = ref('');
const messages = ref([]);
const loading = ref(false);
const messagesContainer = ref(null);
const thinkingContainer = ref(null);
const showThinking = reactive({});
const currentThinkingSteps = ref([]);
const liveThinkingProcess = ref([]);
const showNewThinkingIndicator = ref(false);
const exampleQueries = ['What is a G-quadruplex structure?', 'What main entity types are in G4KG database?', 'How to query the G4KG database?'];

// WebSocket Related
const wsState = ref(0); // 0=connecting, 1=connected, 3=error/closed
const pendingMessage = ref(null);
const connectionStatus = ref('');
const connectionTimer = ref(null);
const reconnectAttempts = ref(0);
const maxReconnectAttempts = 3;
let ws = null;
const WS_URL = "ws://localhost:8000/ws/rag";
const MSG_TYPE = { THINKING: "thinking", ANSWER: "answer", ERROR: "error", END: "end" };

// Computed Properties
const connectionClass = computed(() => {
  if (wsState.value === 1) return 'connected';
  if (wsState.value === 0) return 'connecting';
  return 'disconnected';
});

// Utility Functions
const handleScroll = () => liveThinkingProcess.value.length > 0 && checkThinkingVisibility();

const showConnectionStatus = (message, duration = 3000) => {
  connectionStatus.value = message;
  if (connectionTimer.value) clearTimeout(connectionTimer.value);
  connectionTimer.value = setTimeout(() => connectionStatus.value = '', duration);
};

const setExampleQuery = (query) => userMessage.value = query;

const toggleThinking = (index) => showThinking[index] = !showThinking[index];

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

const scrollToThinking = () => {
  if (thinkingContainer.value && messagesContainer.value) {
    messagesContainer.value.scrollTop = thinkingContainer.value.offsetTop - 20;
    showNewThinkingIndicator.value = false;
  }
};

const checkThinkingVisibility = () => {
  try {
    if (liveThinkingProcess.value.length > 0 && messagesContainer.value && thinkingContainer.value) {
      const containerRect = messagesContainer.value.getBoundingClientRect();
      const thinkingRect = thinkingContainer.value.getBoundingClientRect();
      showNewThinkingIndicator.value = thinkingRect.bottom < containerRect.top || thinkingRect.top > containerRect.bottom;
    } else {
      showNewThinkingIndicator.value = false;
    }
  } catch (e) {
    console.error('Error checking thinking process visibility:', e);
    showNewThinkingIndicator.value = false;
  }
};

// Get step type CSS class
const getStepTypeClass = (step) => {
  // Use type directly from step object
  if (step && step.type) {
    return `step-${step.type}`;
  }
  
  // If no type field, infer from label
  const label = step && step.label ? step.label : step;
  const labelStr = String(label || '').toLowerCase();
  
  if (labelStr.includes('query') || labelStr.includes('analysis')) {
    return 'step-query';
  } else if (labelStr.includes('generate') || labelStr.includes('answer')) {
    return 'step-generate';
  } else if (labelStr.includes('retrieve') || labelStr.includes('document')) {
    return 'step-retrieve';
  } else if (labelStr.includes('error') || labelStr.includes('handle')) {
    return 'step-error';
  } else if (labelStr.includes('route') || labelStr.includes('decision')) {
    return 'step-route';
  } else if (labelStr.includes('entity') || labelStr.includes('relation')) {
    return 'step-entity';
  }
  
  // Default return thinking type
  return 'step-thinking';
};

// WebSocket Message Handling
const handleWebSocketMessage = (event) => {
  try {
    const data = JSON.parse(event.data);
    const messageType = data.type;
    const content = data.content;

    switch (messageType) {
      case MSG_TYPE.THINKING:
        if (content) {
          // Check if content is structured thinking step object
          if (typeof content === 'object' && content.step && content.label) {
            // Add step type field
            let stepObj = { ...content };
            
            // If message includes step_type, use it as type
            if (content.step_type) {
              stepObj.type = content.step_type;
            }
            
            currentThinkingSteps.value.push(stepObj);
            liveThinkingProcess.value.push(stepObj);
          } else {
            // Compatible with old format - create structured object
            const stepCount = currentThinkingSteps.value.length + 1;
            let label = 'Processing Step';
            let stepContent = content;
            let stepType = '';
            
            // Try to extract step name and content from string
            if (typeof content === 'string' && content.includes(':')) {
              const [extractedLabel, ...rest] = content.split(':');
              label = extractedLabel.trim();
              stepContent = rest.join(':').trim();
              
              // Infer type based on label
              if (label.toLowerCase().includes('query') || label.toLowerCase().includes('analysis')) {
                stepType = 'query';
              } else if (label.toLowerCase().includes('generate') || label.toLowerCase().includes('answer')) {
                stepType = 'generate';
              } else if (label.toLowerCase().includes('retrieve') || label.toLowerCase().includes('document')) {
                stepType = 'retrieve';
              } else if (label.toLowerCase().includes('error')) {
                stepType = 'error';
              } else if (label.toLowerCase().includes('route')) {
                stepType = 'route';
              } else if (label.toLowerCase().includes('entity') || label.toLowerCase().includes('relation')) {
                stepType = 'entity';
              } else {
                stepType = 'thinking'; // Default type
              }
            }
            
            const stepObj = {
              step: `step${stepCount}`,
              label: label,
              content: stepContent,
              type: stepType
            };
            
            currentThinkingSteps.value.push(stepObj);
            liveThinkingProcess.value.push(stepObj);
          }
          
          nextTick(() => {
            scrollToBottom();
            if (thinkingContainer.value) {
              thinkingContainer.value.scrollTop = thinkingContainer.value.scrollHeight;
            }
            checkThinkingVisibility();
          });
        }
        break;

      case MSG_TYPE.ANSWER:
        if (content) {
          const newIndex = messages.value.length;
          messages.value.push({
            text: content,
            sender: 'bot',
            thinking: [...currentThinkingSteps.value],
            timestamp: new Date().toISOString()
          });
          
          liveThinkingProcess.value = [];
          if (currentThinkingSteps.value.length > 0) {
            showThinking[newIndex] = true;
          }
          currentThinkingSteps.value = [];
          nextTick(() => scrollToBottom());
        }
        break;

      case MSG_TYPE.ERROR:
        messages.value.push({
          text: content || "An error occurred, please try again",
          sender: 'bot',
          thinking: [...currentThinkingSteps.value],
          isError: true,
          timestamp: new Date().toISOString()
        });
        currentThinkingSteps.value = [];
        liveThinkingProcess.value = [];
        nextTick(() => scrollToBottom());
        break;

      case MSG_TYPE.END:
        loading.value = false;
        break;

      default:
        console.warn(`Unknown message type: ${messageType}`);
    }
  } catch (error) {
    console.error("Error parsing WebSocket message:", error);
    if (event.data === "[END]") {
      loading.value = false;
    } else {
      messages.value.push({
        text: event.data,
        sender: 'bot',
        thinking: [],
        timestamp: new Date().toISOString()
      });
      scrollToBottom();
    }
  }
};

// WebSocket Connection Management
const initWebSocket = () => {
  if (!ws || ws.readyState !== WebSocket.OPEN) {
    if (ws) {
      try { ws.close(); } catch (e) { console.error("Error closing old connection:", e); }
    }

    wsState.value = 0;
    showConnectionStatus('Connecting to server...');

    ws = new WebSocket(WS_URL);

    ws.onopen = () => {
      wsState.value = 1;
      showConnectionStatus('Connected to server', 1500);
      reconnectAttempts.value = 0;
      if (pendingMessage.value) {
        sendMessageToServer(pendingMessage.value);
        pendingMessage.value = null;
      }
    };

    ws.onmessage = handleWebSocketMessage;

    ws.onerror = () => {
      wsState.value = 3;
      showConnectionStatus('Error connecting to server', 5000);
      if (loading.value) {
        messages.value.push({
          text: "Connection error, please try again later.",
          sender: 'bot',
          thinking: [],
          isError: true,
          timestamp: new Date().toISOString()
        });
        currentThinkingSteps.value = [];
        liveThinkingProcess.value = [];
        loading.value = false;
        scrollToBottom();
      }
    };

    ws.onclose = (event) => {
      wsState.value = 3;
      if (event.code !== 1000 && reconnectAttempts.value < maxReconnectAttempts) {
        const delay = Math.min(1000 * Math.pow(2, reconnectAttempts.value), 10000);
        reconnectAttempts.value++;
        showConnectionStatus(`Connection lost, retrying in ${delay/1000} seconds`, delay + 500);
        setTimeout(() => initWebSocket(), delay);
      } else if (reconnectAttempts.value >= maxReconnectAttempts) {
        showConnectionStatus('Connection lost, please refresh the page', 0);
      }
    };
  }
  return ws;
};

// Message Sending
const sendMessageToServer = (message) => {
  if (ws && ws.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify({ message: message }));
  } else {
    pendingMessage.value = message;
    initWebSocket();
  }
};

const sendMessage = async () => {
  if (!userMessage.value.trim() || loading.value) return;
  loading.value = true;
  messages.value.push({
    text: userMessage.value.trim(),
    sender: 'user',
    thinking: [],
    timestamp: new Date().toISOString()
  });
  
  currentThinkingSteps.value = [];
  liveThinkingProcess.value = [];
  const messageToSend = userMessage.value.trim();
  userMessage.value = '';
  scrollToBottom();

  const websocket = initWebSocket();
  if (websocket && websocket.readyState === WebSocket.OPEN) {
    sendMessageToServer(messageToSend);
  } else {
    pendingMessage.value = messageToSend;
    showConnectionStatus('Connecting to server...', 5000);
  }
};

// Clear Conversation
const clearMessages = () => {
  messages.value = [];
  Object.keys(showThinking).forEach(key => delete showThinking[key]);
  currentThinkingSteps.value = [];
  liveThinkingProcess.value = [];
};

// Lifecycle Hooks
onMounted(() => {
  initWebSocket();
  window.addEventListener('scroll', handleScroll, true);
  if (messagesContainer.value) {
    messagesContainer.value.addEventListener('scroll', checkThinkingVisibility);
  }
});

onBeforeUnmount(() => {
  if (ws) {
    ws.close();
    ws = null;
  }
  if (connectionTimer.value) {
    clearTimeout(connectionTimer.value);
  }
  window.removeEventListener('scroll', handleScroll, true);
  if (messagesContainer.value) {
    messagesContainer.value.removeEventListener('scroll', checkThinkingVisibility);
  }
});
</script>

<style scoped>
.chatbot-container {
  margin: 0;
  padding: 0;
  min-height: 100vh;
  background: url('@/assets/photo/background.jpg') no-repeat center center fixed;
  background-size: cover;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
  position: relative;
}

/* Layout Components */
.header-content {
  margin-top: 20px;
  margin-bottom: 30px;
  text-align: center;
  width: 80%;
}

.header-content h1 {
  font-size: 3rem;
  margin-bottom: 10px;
  font-weight: bold;
}

.header-content p {
  font-size: 1.5rem;
  line-height: 1.6;
}

.chat-section {
  display: flex;
  width: 80%;
  max-width: 1400px;
  margin-bottom: 20px;
}

.chat-container {
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  padding: 20px;
  height: 70vh;
  overflow: hidden;
  position: relative;
  flex: 1;
  color: #333;
}

/* Message Display Area */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 16px;
  padding: 10px 15px;
}

/* Welcome Message */
.welcome-message {
  background-color: rgba(44, 106, 127, 0.1);
  border-left: 4px solid #2c6a7f;
  padding: 15px;
  margin-bottom: 20px;
  border-radius: 4px;
}

.welcome-message h3 {
  font-size: 1.8rem;
  margin-bottom: 10px;
}

.welcome-message p {
  font-size: 1.2rem;
  margin-bottom: 15px;
}

.example-queries li {
  background-color: #f0f5f7;
  padding: 10px;
  margin: 5px 0;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s ease;
}

.example-queries li:hover {
  background-color: #e0edf2;
}

/* Message Styles */
.message {
  margin-bottom: 15px;
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 8px;
  line-height: 1.5;
  word-wrap: break-word;
  white-space: pre-wrap;
  animation: fadeIn 0.3s;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.user-message {
  align-self: flex-end;
  background-color: #2c6a7f;
  color: #fff;
  border-top-right-radius: 0;
  margin-left: auto;
}

.bot-message {
  align-self: flex-start;
  background-color: #f2f2f2;
  color: #333;
  border-top-left-radius: 0;
  margin-right: auto;
}

.bot-message.error {
  background-color: #fff0f0;
  border-left: 3px solid #d32f2f;
}

/* Thinking Process Styles */
.thinking-process, .thinking-toggle {
  margin-top: 10px;
  font-size: 0.9em;
  background-color: #f5f5f5;
  border-radius: 4px;
  padding: 8px;
  max-height: 300px;
  overflow-y: auto;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.thinking-process.live {
  border-left: 3px solid #409eff;
}

.thinking-header, .toggle-header {
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
  padding-bottom: 5px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  cursor: pointer;
}

.toggle-icon {
  transition: transform 0.3s;
}

.toggle-icon.expanded {
  transform: rotate(180deg);
}

.thinking-steps {
  display: flex;
  flex-direction: column;
  gap: 6px;
  background-color: #fafafa;
  border-radius: 4px;
  padding: 6px;
}

.thinking-step {
  padding: 8px;
  background-color: #fff;
  border-radius: 4px;
  margin-bottom: 6px;
  border-left: 2px solid #2c6a7f;
  transition: transform 0.2s ease;
}

.thinking-step:hover {
  transform: translateX(2px);
}

.step-header {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.step-number {
  font-weight: bold;
  color: #2c6a7f;
  margin-right: 5px;
}

.step-name {
  font-weight: 500;
  color: #333;
}

.step-content {
  color: #555;
  font-size: 0.9em;
  margin-left: 5px;
  padding-left: 5px;
  border-left: 1px solid #eee;
}

/* Step Type Styles */
.step-query { border-left-color: #409eff; }
.step-generate { border-left-color: #67C23A; }
.step-retrieve { border-left-color: #E6A23C; }
.step-error { border-left-color: #F56C6C; }
.step-route { border-left-color: #909399; }
.step-entity { border-left-color: #00ACC1; }
.step-thinking { border-left-color: #409eff; }

/* Enhanced Styles - Visual distinction for different step types */
.step-thinking .step-header { color: #409eff; }
.step-entity .step-header { color: #00ACC1; }
.step-error .step-header { color: #F56C6C; }
.step-query .step-header { color: #409eff; }
.step-generate .step-header { color: #67C23A; }
.step-retrieve .step-header { color: #E6A23C; }

/* Special styles for step content */
.step-error .step-content {
  border-left-color: #ffcdd2;
  background-color: #fff5f5;
}

.step-entity .step-content {
  border-left-color: #b2ebf2;
  background-color: #e0f7fa;
}

/* Loading Indicator */
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

.loading-indicator span:nth-child(1) { animation-delay: -0.32s; }
.loading-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes loading {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1.0); }
}

/* Input Area */
.input-area {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.input-area .el-input {
  flex: 1;
}

.input-area .el-button {
  font-size: 1rem;
  padding: 12px 20px;
}

/* New Thinking Step Indicator */
.new-thinking-indicator {
  position: fixed;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  background-color: #409eff;
  color: white;
  padding: 8px 15px;
  border-radius: 20px;
  font-size: 0.85em;
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  z-index: 100;
}

.new-thinking-indicator i {
  animation: bounce 1s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(3px); }
}

/* Connection Status Indicator */
.connection-status {
  position: fixed;
  top: 10px;
  right: 10px;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  z-index: 1000;
}

.connection-status.connected { background-color: rgba(46, 125, 50, 0.7); }
.connection-status.connecting { background-color: rgba(245, 124, 0, 0.7); }
.connection-status.disconnected { background-color: rgba(211, 47, 47, 0.7); }

/* Responsive */
@media (max-width: 768px) {
  .chat-container { height: 60vh; }
  .header-content { width: 95%; }
  .chat-section { width: 95%; }
}
</style>