<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { userLogin } from '../api/user'
import { useUserStore } from '../stores/user'
import uasLogo from '../assets/uas-logo.png'

const userStore = useUserStore()
const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const submitForm = async () => {
  if (!loginFormRef.value) return

  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const response = await userLogin(loginForm.username, loginForm.password)

    if (!response.token) {
      ElMessage.error(response.msg || '登录失败，请检查账户信息。')
      return
    }

    userStore.setToken(response.token)
    if (response.userInfo) {
      userStore.setUserInfo(response.userInfo)
    }
    ElMessage.success('登录成功，欢迎回来。')
    await router.push('/')
  } catch (error) {
    // Request errors are presented by the shared Axios interceptor.
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="login-page">
    <section class="brand-panel" aria-label="Platform overview">
      <div class="brand-content">
        <div class="brand-mark">
          <img :src="uasLogo" alt="UAS 2.0" />
          <span>UAS 2.0</span>
        </div>

        <p class="eyebrow">ROLE-BASED ACCESS CONTROL</p>
        <h1>权限治理，<br />清晰而可靠。</h1>
        <p class="intro">
          统一管理用户、角色与操作权限，用结构化授权模型构建可审计的管理体验。
        </p>

        <div class="feature-grid">
          <div class="feature-item">
            <el-icon><UserFilled /></el-icon>
            <div>
              <strong>身份管理</strong>
              <span>集中维护用户与角色关系</span>
            </div>
          </div>
          <div class="feature-item">
            <el-icon><Lock /></el-icon>
            <div>
              <strong>细粒度授权</strong>
              <span>面向资源与操作的权限控制</span>
            </div>
          </div>
          <div class="feature-item">
            <el-icon><DataAnalysis /></el-icon>
            <div>
              <strong>一致的数据模型</strong>
              <span>从设计文档到系统实现</span>
            </div>
          </div>
        </div>
      </div>
      <div class="ambient ambient-one"></div>
      <div class="ambient ambient-two"></div>
    </section>

    <section class="form-panel">
      <div class="form-shell">
        <div class="mobile-brand">
          <img :src="uasLogo" alt="" />
          <span>UAS 2.0</span>
        </div>
        <p class="eyebrow form-eyebrow">SECURE CONSOLE</p>
        <h2>欢迎登录</h2>
        <p class="form-subtitle">请输入您的账户信息以进入管理控制台。</p>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          label-position="top"
          class="login-form"
          @keyup.enter="submitForm"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="loginForm.username"
              size="large"
              placeholder="请输入用户名"
              autocomplete="username"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              size="large"
              placeholder="请输入密码"
              autocomplete="current-password"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="submit-button"
            @click="submitForm"
          >
            进入管理平台
          </el-button>
        </el-form>

        <p class="security-note">
          <el-icon><CircleCheck /></el-icon>
          登录会话由 JWT 令牌保护
        </p>
      </div>
    </section>
  </main>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: minmax(480px, 1.08fr) minmax(420px, 0.92fr);
  background: #f7f9fc;
}

.brand-panel {
  position: relative;
  display: flex;
  align-items: center;
  min-height: 100vh;
  padding: 72px clamp(56px, 7vw, 112px);
  overflow: hidden;
  color: #fff;
  background:
    radial-gradient(circle at 75% 18%, rgba(0, 190, 255, 0.23), transparent 28%),
    linear-gradient(145deg, #07132f 0%, #0b2551 58%, #073a62 100%);
}

.brand-panel::after {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.035) 1px, transparent 1px);
  background-size: 46px 46px;
  content: "";
  mask-image: linear-gradient(to bottom right, black, transparent 75%);
}

.brand-content {
  position: relative;
  z-index: 2;
  width: min(620px, 100%);
}

.brand-mark,
.mobile-brand {
  display: flex;
  align-items: center;
  gap: 14px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.brand-mark {
  margin-bottom: clamp(72px, 10vh, 116px);
  font-size: 20px;
}

.brand-mark img,
.mobile-brand img {
  width: 44px;
  height: 44px;
  object-fit: cover;
  border-radius: 12px;
}

.eyebrow {
  margin: 0 0 20px;
  color: #54d6ff;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.22em;
}

h1 {
  margin: 0;
  font-size: clamp(46px, 5.4vw, 76px);
  font-weight: 750;
  line-height: 1.13;
  letter-spacing: -0.045em;
}

.intro {
  max-width: 540px;
  margin: 30px 0 46px;
  color: rgba(236, 246, 255, 0.74);
  font-size: 17px;
  line-height: 1.8;
}

.feature-grid {
  display: grid;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.feature-item > .el-icon {
  flex: 0 0 auto;
  width: 42px;
  height: 42px;
  color: #67dcff;
  font-size: 20px;
  border: 1px solid rgba(103, 220, 255, 0.25);
  border-radius: 13px;
  background: rgba(61, 186, 232, 0.08);
}

.feature-item div {
  display: grid;
  gap: 4px;
}

.feature-item strong {
  font-size: 15px;
}

.feature-item span {
  color: rgba(236, 246, 255, 0.58);
  font-size: 13px;
}

.ambient {
  position: absolute;
  z-index: 1;
  border: 1px solid rgba(97, 218, 255, 0.16);
  border-radius: 50%;
}

.ambient-one {
  right: -170px;
  bottom: -190px;
  width: 520px;
  height: 520px;
}

.ambient-two {
  right: -70px;
  bottom: -90px;
  width: 320px;
  height: 320px;
}

.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 52px;
}

.form-shell {
  width: min(420px, 100%);
}

.mobile-brand {
  display: none;
  margin-bottom: 56px;
  color: #0b2551;
}

.form-eyebrow {
  margin-bottom: 16px;
  color: #168eb9;
}

h2 {
  margin: 0;
  color: #101a32;
  font-size: 38px;
  line-height: 1.25;
  letter-spacing: -0.035em;
}

.form-subtitle {
  margin: 14px 0 38px;
  color: #6e7890;
  font-size: 15px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-form-item__label) {
  padding-bottom: 9px;
  color: #36415a;
  font-weight: 650;
}

.login-form :deep(.el-input__wrapper) {
  min-height: 50px;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #dce3ed inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #158fbd inset, 0 0 0 4px rgba(21, 143, 189, 0.1);
}

.submit-button {
  width: 100%;
  height: 52px;
  margin-top: 6px;
  border: 0;
  border-radius: 10px;
  font-weight: 650;
  background: linear-gradient(100deg, #0877a6, #0a9bc5);
  box-shadow: 0 12px 24px rgba(11, 129, 170, 0.2);
}

.security-note {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  margin: 26px 0 0;
  color: #8a93a7;
  font-size: 12px;
}

@media (min-width: 901px) and (max-height: 800px) {
  .brand-panel {
    padding-top: 36px;
    padding-bottom: 36px;
  }

  .brand-mark {
    margin-bottom: 38px;
  }

  h1 {
    font-size: 52px;
  }

  .intro {
    margin: 20px 0 26px;
  }

  .feature-grid {
    gap: 12px;
  }

  .feature-item > .el-icon {
    width: 36px;
    height: 36px;
  }

  .form-panel {
    padding-top: 36px;
    padding-bottom: 36px;
  }

  .form-subtitle {
    margin-bottom: 26px;
  }
}

@media (max-width: 900px) {
  .login-page {
    display: block;
  }

  .brand-panel {
    display: none;
  }

  .form-panel {
    padding: 40px 24px;
    background:
      radial-gradient(circle at 100% 0, rgba(41, 184, 222, 0.12), transparent 35%),
      #f7f9fc;
  }

  .mobile-brand {
    display: flex;
  }
}

@media (max-width: 480px) {
  .form-panel {
    align-items: flex-start;
    padding-top: 32px;
  }

  .mobile-brand {
    margin-bottom: 72px;
  }

  h2 {
    font-size: 32px;
  }
}
</style>
