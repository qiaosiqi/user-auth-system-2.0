<script setup>
import { ref, nextTick } from 'vue'
// 🚨 修正：使用相对路径导入，确保找到 src/api/deepseek.js
import { chatWithDeepseek } from '../../api/deepseek'
import { ElMessage } from 'element-plus'

const userInput = ref('')
const loading = ref(false)
const chatHistory = ref([
  { role: 'system', content: '您好，我是专注于用户权限系统的智能助手，请问有什么可以帮您的？' }
])
const chatContainer = ref(null)

// 提交问题
const handleSubmit = async () => {
  if (loading.value) return; // 防止重复点击
  if (!userInput.value.trim()) {
    return ElMessage.warning('请输入问题')
  }

  const prompt = userInput.value.trim()
  userInput.value = '' // 清空输入框
  loading.value = true

  // 1. 添加用户消息
  chatHistory.value.push({ role: 'user', content: prompt })
  // 2. 添加 AI 占位符
  chatHistory.value.push({ role: 'system', content: 'AI 正在思考中...' })
  scrollToBottom()

  try {
    const res = await chatWithDeepseek(prompt)

    // 3. 替换 AI 占位符为真实回答
    const lastIndex = chatHistory.value.length - 1
    if (res.code === 200 && res.data) {
      chatHistory.value[lastIndex].content = res.data
    } else {
      chatHistory.value[lastIndex].content = `[错误] ${res.msg || '调用失败'}`
    }
  } catch (error) {
    const lastIndex = chatHistory.value.length - 1
    chatHistory.value[lastIndex].content = `[请求异常] ${error.message || '网络或服务器错误'}`
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}
</script>

<template>
  <div class="deepseek-chat-container">
    <el-card shadow="never" class="chat-card">

      <div ref="chatContainer" class="chat-messages">
        <div
            v-for="(msg, index) in chatHistory"
            :key="index"
            :class="['message-item', msg.role]"
        >
          <el-tag :type="msg.role === 'user' ? 'success' : 'info'" size="small">
            {{ msg.role === 'user' ? '你' : 'AI' }}
          </el-tag>
          <div class="content">{{ msg.content }}</div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
            v-model="userInput"
            placeholder="请输入您的问题..."
            :rows="4"
            type="textarea"
            @keyup.enter.prevent="handleSubmit"
        />
        <el-button
            type="primary"
            :loading="loading"
            @click="handleSubmit"
            style="margin-top: 10px;"
        >
          发送
        </el-button>
      </div>

    </el-card>
  </div>
</template>

<style scoped>
/* 样式与之前的 QwenChat.vue 保持一致 */
.deepseek-chat-container {
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
}

.chat-card {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
}

.chat-messages {
  flex-grow: 1;
  overflow-y: auto;
  padding: 10px;
  border-bottom: 1px solid var(--el-border-color-light);
  margin-bottom: 10px;
}

.message-item {
  display: flex;
  margin-bottom: 15px;
  max-width: 80%;
}

.message-item.user {
  justify-content: flex-end;
  margin-left: auto;
}

.message-item .content {
  background-color: var(--el-color-info-light-9);
  padding: 8px 12px;
  border-radius: 8px;
  margin-left: 10px;
  margin-right: 10px;
  word-break: break-word;
  white-space: pre-wrap;
}

.message-item.user .content {
  background-color: var(--el-color-success-light-9);
}
</style>