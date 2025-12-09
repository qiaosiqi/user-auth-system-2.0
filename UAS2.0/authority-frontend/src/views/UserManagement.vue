<script setup>

import { Search, Refresh, Plus, Edit } from '@element-plus/icons-vue' // 🚨 新增导入 Edit 图标
import { reactive, ref, onMounted } from 'vue'
import { getUserList, deleteUser } from '../api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import UserDialog from './UserDialog.vue' // 🚨 新增导入弹窗组件
// 🚨 移除 PermissionDialog，新增 AssignRoleDialog
import AssignRoleDialog from './AssignRoleDialog.vue'


// --- 数据状态 ---
const tableData = ref([]) // 用户列表数据
const total = ref(0)       // 总记录数
const loading = ref(true)  // 加载状态

// --- 弹窗状态 ---
const dialogVisible = ref(false)
const isEditMode = ref(false)
const currentEditUser = ref({})
// --- 弹窗状态 ---
// ... dialogVisible, isEditMode, currentEditUser 保持不变 (用于新增/编辑用户)
// 🚨 新增：角色分配弹窗状态
const assignRoleDialogVisible = ref(false)
const currentUser = ref({ id: null, username: '', roleId: null })

// // --- 权限分配状态 ---
// const permissionDialogVisible = ref(false)
// const currentRole = ref({ id: null, roleName: '' }) // 存储当前用户的角色信息

// 查询表单和分页参数
const queryParams = reactive({
  username: '',   // 用户名搜索关键词
  pageNum: 1,     // 当前页码
  pageSize: 10    // 每页大小
})

// --- 核心方法 ---

// 1. 获取用户列表数据
const fetchUserList = async () => {
  loading.value = true
  try {
    const response = await getUserList(queryParams)

    // 假设后端返回的数据结构是 {code: 200, data: {list: [], total: 100}}
    // 如果你的后端返回结构不同，请修改这里！
    if (response.data) {
      tableData.value = response.data.list
      total.value = response.data.total
    } else {
      tableData.value = response.list // 兼容后端直接返回 list/total 的情况
      total.value = response.total
    }

  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 2. 处理页码变化
const handleCurrentChange = (newPage) => {
  queryParams.pageNum = newPage
  fetchUserList()
}

// 3. 处理每页大小变化
const handleSizeChange = (newSize) => {
  queryParams.pageSize = newSize
  queryParams.pageNum = 1 // 切换每页大小时，重置到第一页
  fetchUserList()
}

// 4. 处理查询/搜索
const handleSearch = () => {
  queryParams.pageNum = 1 // 搜索时重置到第一页
  fetchUserList()
}

// 5. 处理删除操作
const handleDelete = (row) => {
  ElMessageBox.confirm(
      `确定删除用户 [${row.username}] 吗？删除后不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(async () => {
    try {
      // 调用删除接口
      await deleteUser(row.id)

      ElMessage.success('用户删除成功！')
      // 刷新列表
      fetchUserList()

    } catch (error) {
      console.error('删除失败:', error)
      // 错误提示已在 request.js 中处理
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 6. 处理新增按钮点击
const handleAdd = () => {
  isEditMode.value = false
  currentEditUser.value = {} // 清空当前编辑用户数据
  dialogVisible.value = true
}

// 7. 处理编辑按钮点击
const handleEdit = (row) => {
  isEditMode.value = true
  // 将当前行数据深拷贝给弹窗，并转换 roleId 为 number (如果需要)
  currentEditUser.value = {
    ...row,
    roleId: Number(row.roleId) // 确保类型正确，以便 El-Select 正确选中
  }
  dialogVisible.value = true
}

// 8. 处理分配角色按钮点击
const handleAssignRole = (row) => {
  // 准备当前用户信息用于弹窗
  currentUser.value = {
    id: row.id,
    username: row.username,
    roleId: row.roleId // 假设 row 中有当前用户的角色ID
  }
  assignRoleDialogVisible.value = true
}

// 页面加载时执行
onMounted(() => {
  fetchUserList()
})
</script>

<template>
  <div class="user-management-container">
    <el-card shadow="never">
      <div class="header-tools">
        <el-input
            v-model="queryParams.username"
            placeholder="输入用户名搜索"
            clearable
            style="width: 200px; margin-right: 10px;"
            @keyup.enter="handleSearch"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="fetchUserList">重置/刷新</el-button>

        <el-button type="success" :icon="Plus" @click="handleAdd">新增用户</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
            <el-button link type="warning" size="small" @click="handleAssignRole(row)">分配角色</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          :current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
          style="margin-top: 20px; float: right;"
      />

    </el-card>

    <UserDialog
        v-model:visible="dialogVisible"
        :is-edit="isEditMode"
        :initial-data="currentEditUser"
        @success="fetchUserList"
    />

    <AssignRoleDialog
        v-model:visible="assignRoleDialogVisible"
        :user-id="currentUser.id"
        :username="currentUser.username"
        @success="fetchUserList"
    />

  </div>
</template>



<style scoped>
.header-tools {
  display: flex;
  align-items: center;
}
.user-management-container {
  padding: 0;
}
</style>