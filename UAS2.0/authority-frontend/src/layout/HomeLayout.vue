<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import uasLogo from '../assets/uas-logo.png'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

const menuItems = reactive([
  { path: '/user', name: '用户管理', icon: 'UserFilled' },
  { path: '/role', name: '角色管理', icon: 'Avatar' },
  { path: '/permission', name: '权限管理', icon: 'Operation' },
  { path: '/deepseek/chat', name: '智能体问答', icon: 'MagicStick' }
])

const username = computed(() => userStore.userInfo?.username || 'Administrator')
const userInitial = computed(() => username.value.charAt(0).toUpperCase())

const handleLogout = async () => {
  userStore.clearToken()
  await router.push('/login')
  ElMessage.info('您已安全退出。')
}
</script>

<template>
  <el-container class="app-shell">
    <el-aside :width="isCollapse ? '76px' : '236px'" class="sidebar">
      <div class="brand-box" :class="{ collapsed: isCollapse }">
        <img :src="uasLogo" alt="UAS 2.0" />
        <div v-show="!isCollapse" class="brand-copy">
          <strong>UAS 2.0</strong>
          <span>ACCESS CONTROL</span>
        </div>
      </div>

      <p v-show="!isCollapse" class="menu-label">MANAGEMENT</p>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        router
        class="aside-menu"
      >
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.name }}</template>
        </el-menu-item>
      </el-menu>

      <div v-show="!isCollapse" class="sidebar-footer">
        <span class="status-dot"></span>
        <div>
          <strong>Service connected</strong>
          <span>Protected by JWT</span>
        </div>
      </div>
    </el-aside>

    <el-container class="workspace">
      <el-header class="header-box">
        <div class="left-content">
          <button class="collapse-button" aria-label="Toggle navigation" @click="isCollapse = !isCollapse">
            <el-icon><component :is="isCollapse ? 'Expand' : 'Fold'" /></el-icon>
          </button>
          <div>
            <p class="page-kicker">ADMINISTRATION</p>
            <h1>{{ route.meta.title || '管理控制台' }}</h1>
          </div>
        </div>

        <el-dropdown trigger="click">
          <div class="profile">
            <span class="avatar">{{ userInitial }}</span>
            <div class="profile-copy">
              <strong>{{ username }}</strong>
              <span>System administrator</span>
            </div>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="SwitchButton" @click="handleLogout">
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: #f3f6fa;
}

.sidebar {
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  color: #fff;
  background:
    radial-gradient(circle at 30% 90%, rgba(19, 157, 198, 0.18), transparent 30%),
    #091630;
  transition: width 0.22s ease;
}

.brand-box {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 82px;
  padding: 0 22px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
}

.brand-box.collapsed {
  justify-content: center;
  padding: 0;
}

.brand-box img {
  flex: 0 0 auto;
  width: 38px;
  height: 38px;
  object-fit: cover;
  border-radius: 10px;
}

.brand-copy {
  display: grid;
  gap: 3px;
  white-space: nowrap;
}

.brand-copy strong {
  font-size: 17px;
  letter-spacing: 0.03em;
}

.brand-copy span,
.menu-label {
  color: rgba(207, 224, 244, 0.45);
  font-size: 9px;
  font-weight: 750;
  letter-spacing: 0.18em;
}

.menu-label {
  margin: 28px 24px 10px;
}

.aside-menu {
  flex: 1;
  border: 0;
  background: transparent;
}

.aside-menu:not(.el-menu--collapse) {
  width: 236px;
}

.aside-menu :deep(.el-menu-item) {
  height: 48px;
  margin: 5px 12px;
  color: rgba(225, 236, 249, 0.72);
  border-radius: 9px;
}

.aside-menu :deep(.el-menu-item:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.07);
}

.aside-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: linear-gradient(100deg, rgba(15, 141, 183, 0.92), rgba(11, 174, 204, 0.72));
  box-shadow: 0 8px 18px rgba(0, 118, 157, 0.2);
}

.sidebar-footer {
  display: flex;
  align-items: center;
  gap: 11px;
  margin: 18px;
  padding: 14px;
  color: rgba(224, 237, 249, 0.75);
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.035);
}

.sidebar-footer div {
  display: grid;
  gap: 3px;
}

.sidebar-footer strong {
  font-size: 11px;
  font-weight: 600;
}

.sidebar-footer span:not(.status-dot) {
  color: rgba(224, 237, 249, 0.4);
  font-size: 9px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #42d69a;
  box-shadow: 0 0 0 4px rgba(66, 214, 154, 0.12);
}

.workspace {
  min-width: 0;
}

.header-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 82px;
  padding: 0 30px;
  border-bottom: 1px solid #e8edf4;
  background: rgba(255, 255, 255, 0.94);
}

.left-content {
  display: flex;
  align-items: center;
  gap: 17px;
}

.collapse-button {
  display: grid;
  place-items: center;
  width: 38px;
  height: 38px;
  padding: 0;
  color: #5a6680;
  border: 1px solid #e1e7ef;
  border-radius: 9px;
  background: #fff;
  cursor: pointer;
}

.page-kicker {
  margin: 0 0 3px;
  color: #97a1b3;
  font-size: 9px;
  font-weight: 750;
  letter-spacing: 0.17em;
}

h1 {
  margin: 0;
  color: #14203a;
  font-size: 20px;
  line-height: 1.2;
}

.profile {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 7px 9px;
  border-radius: 10px;
  cursor: pointer;
}

.profile:hover {
  background: #f5f7fa;
}

.avatar {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  border-radius: 10px;
  background: linear-gradient(145deg, #0d78a5, #13a6c8);
}

.profile-copy {
  display: grid;
  min-width: 132px;
  gap: 2px;
}

.profile-copy strong {
  color: #2d3850;
  font-size: 13px;
}

.profile-copy span {
  color: #929bad;
  font-size: 10px;
}

.main-content {
  min-width: 0;
  padding: 22px;
  background: #f3f6fa;
}

@media (max-width: 720px) {
  .profile-copy {
    display: none;
  }

  .header-box {
    padding: 0 16px;
  }

  .main-content {
    padding: 14px;
  }
}
</style>
