<script setup>
import { ref, onMounted } from 'vue'

import { ElMessage, ElMessageBox } from 'element-plus'

// 🚨 确保从 role.js 中导入所有需要的角色接口
import { getRoleList, saveOrUpdateRole, deleteRole } from '../api/role'
import PermissionDialog from './PermissionDialog.vue'
import RoleDialog from './RoleDialog.vue' // 🚨 新增导入 RoleDialog
import { Plus, Search, Edit, Delete } from '@element-plus/icons-vue' // 确保 Delete 图标也被导入

// --- 状态 ---
const loading = ref(false)
const tableData = ref([])

// 权限分配弹窗状态
const permissionDialogVisible = ref(false)
const currentRole = ref({ id: null, roleName: '' }) // 存储当前角色信息

// 🚨 新增：角色新增/编辑弹窗状态
const roleDialogVisible = ref(false)
const isEditMode = ref(false)
const currentEditRole = ref({ id: null, roleName: '', roleCode: '' })


// --- 数据获取 ---
const fetchRoleList = async () => {
  loading.value = true
  try {
    // 调用 /api/role/list 接口
    const res = await getRoleList()
    tableData.value = res.data || []
  } catch (error) {
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}


// --- CRUD 处理 ---
const handleAdd = () => {
  isEditMode.value = false
  currentEditRole.value = { id: null, roleName: '', roleCode: '' } // 确保清空
  roleDialogVisible.value = true
}

const handleEdit = (row) => {
  isEditMode.value = true
  // 传递当前行数据作为初始值
  currentEditRole.value = { ...row }
  roleDialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
      `确定要删除角色 [${row.roleName}] 吗? 删除后关联用户的角色也会被清除。`,
      '警告',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          await deleteRole(row.id) // 调用删除 API
          ElMessage.success('角色删除成功！')
          fetchRoleList() // 刷新列表
        } catch (error) {
          ElMessage.error('删除失败，请检查是否有用户关联此角色')
        }
      })
      .catch(() => {
        // 用户取消删除
      })
}



// --- 权限分配处理 ---
const handleAssignPermission = (row) => {
  // 传递角色信息给弹窗
  currentRole.value = {
    id: row.id,
    roleName: row.roleName
  }
  permissionDialogVisible.value = true
}


onMounted(() => {
  fetchRoleList()
})
</script>

<template>
  <div class="role-management-container">
    <el-card shadow="never">
      <div class="header-container">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="description" label="描述" width="180" />

        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" :icon="Delete" @click="handleDelete(row)">删除</el-button>

            <el-button link type="warning" size="small" @click="handleAssignPermission(row)">
              分配权限
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <PermissionDialog
          v-model:visible="permissionDialogVisible"
          :role-id="currentRole.id"
          :role-name="currentRole.roleName"
          @success="() => { ElMessage.success('角色权限更新成功'); /* 无需刷新角色列表 */ }"
      />

      <RoleDialog
          v-model:visible="roleDialogVisible"
          :is-edit-mode="isEditMode"
          :initial-data="currentEditRole"
          @success="fetchRoleList"
      />

    </el-card>
  </div>
</template>