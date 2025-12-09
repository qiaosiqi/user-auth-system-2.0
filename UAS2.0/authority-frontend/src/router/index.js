import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import HomeLayout from '../layout/HomeLayout.vue' // 稍后创建
import { useUserStore } from '../stores/user' // 新增导入 Pinia store
import { ElMessage } from 'element-plus'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: { title: '用户登录' }
    },
    {
        path: '/',
        name: 'Home',
        component: HomeLayout,
        redirect: '/user', // 默认重定向到用户管理
        children: [
            {
                path: 'user',
                name: 'UserManagement',
                component: () => import('../views/UserManagement.vue'),
                meta: { title: '用户管理' }
            },
            // ... 更多路由，如 role, function

            // 🚨 新增：角色管理路由
            {
                path: 'role', // 完整的路径是 /role
                name: 'RoleManagement',
                component: () => import('../views/RoleManagement.vue'),
                meta: { title: '角色管理' }
            },
            // ... 其他子路由

        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 🚨 全局前置守卫：在每次路由跳转前执行
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    const token = userStore.token

    // 1. 设置页面标题
    if (to.meta.title) {
        document.title = `${to.meta.title} | 权限管理系统`
    }

    // 2. 判断是否需要登录 (排除登录页)
    if (to.path !== '/login') {
        if (token) {
            // 已登录，放行
            next()
        } else {
            // 未登录，提示并强制跳转到登录页
            ElMessage.warning('请先进行登录操作！')
            next('/login')
        }
    } else {
        // 如果目标是登录页，直接放行
        // (如果已登录且尝试访问 /login，可以考虑重定向到首页)
        if (token) {
            next('/')
        } else {
            next()
        }
    }
})

export default router