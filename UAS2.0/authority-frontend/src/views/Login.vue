<script setup>
import { reactive, ref } from 'vue'
import { useUserStore } from '../stores/user'
import { userLogin } from '../api/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

// 导入 Pinia store 和 Router
const userStore = useUserStore()
const router = useRouter()

// 表单数据
const loginForm = reactive({
  username: 'admin', // 默认填充 admin 方便测试
  password: '123456'  // 默认填充密码
})

// 表单引用和加载状态
const loginFormRef = ref(null)
const loading = ref(false)

// 表单校验规则
const rules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

// 提交登录
const submitForm = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 1. 调用登录接口
        const response = await userLogin(loginForm.username, loginForm.password)

        // 🚨 修正 Token 访问路径：直接访问 response.token
        const token = response.token

        if (token) {
          // 2. 存储 Token
          userStore.setToken(token)

          // 3. 提示成功并跳转
          ElMessage.success('登录成功！欢迎回来。')
          router.push('/') // 跳转到首页
        } else {
          // 这部分通常不会执行，因为后端成功时总是会返回 token
          ElMessage.error(response.msg || '登录失败，服务器未返回Token！')
        }

      } catch (error) {
        // ... (省略 catch 块)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>权限管理系统登录</span>
        </div>
      </template>

      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          label-width="80px"
          @keyup.enter="submitForm"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密 码" prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
              type="primary"
              :loading="loading"
              @click="submitForm"
              style="width: 100%;"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}

.login-card {
  width: 400px;
  max-width: 90%;
}
</style>