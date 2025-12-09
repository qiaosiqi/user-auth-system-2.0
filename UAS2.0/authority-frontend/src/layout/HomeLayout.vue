<script setup>
import { useUserStore } from '../stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'; // 🚨 确保这里导入了 ref！

const userStore = useUserStore()
const router = useRouter()
const isCollapse = ref(false) // 侧边栏是否折叠

// 模拟菜单数据 (后续将被后端接口数据替换)
const menuItems = reactive([
  { path: '/user', name: '用户管理', icon: 'UserFilled' },
  { path: '/role', name: '角色管理', icon: 'Avatar' },
  { path: '/permission', name: '权限管理', icon: 'Operation' }
])

// 登出操作
const handleLogout = () => {
  userStore.clearToken()
  router.push('/login')
  ElMessage.info('您已安全退出。')
}

// 获取当前用户名
const username = userStore.userInfo?.username || 'Admin' // 应该在登录时存储用户信息，这里先简化
</script>

<template>
  <el-container class="common-layout">
    <el-aside :width="isCollapse ? '64px' : '200px'">
      <el-menu
          :default-active="$route.path"
          :collapse="isCollapse"
          router
          class="aside-menu"
      >
        <div class="logo-box">
          <img src="../assets/vue.svg" alt="logo" />
          <span v-show="!isCollapse">权限管理系统</span>
        </div>

        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.name }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header-box">
        <div class="left-content">
          <el-icon class="collapse-icon" @click="isCollapse = !isCollapse">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item>首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="right-content">
          <el-dropdown>
            <span class="el-dropdown-link">
              {{ username }}
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.common-layout {
  height: 100vh;
}
.aside-menu {
  height: 100%;
  border-right: none; /* 移除菜单边框 */
}
.aside-menu:not(.el-menu--collapse) {
  width: 200px;
}
.logo-box {
  height: 60px;
  line-height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;
  background-color: #333744;
}
.logo-box img {
  width: 28px;
  height: 28px;
  margin-right: 10px;
}

/* Header 样式 */
.header-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  background-color: #fff;
}
.left-content {
  display: flex;
  align-items: center;
}
.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}
.el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
}
.main-content {
  background-color: #f0f2f5;
  padding: 10px;
}
</style>