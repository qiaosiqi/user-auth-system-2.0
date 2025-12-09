<script setup>
import { ref, watch } from 'vue'
// 🚨 修正前：
// import { assignUserRole, getRoleList } from '../api/user'

// 🚨 修正后：
import { assignUserRole } from '../api/user' // 保持 assignUserRole 导入不变
import { getRoleList } from '../api/role'   // 从新的 role.js 中导入 getRoleList

import { ElMessage } from 'element-plus'
const props = defineProps({
  visible: { type: Boolean, default: false },
  userId: { type: [Number, String], default: null }, // 当前用户ID
  username: { type: String, default: '' } // 用户名（仅显示）
})

const emit = defineEmits(['update:visible', 'success'])

// --- 状态 ---
const loading = ref(false)
const roleOptions = ref([]) // 所有角色列表
const currentRoleId = ref(null) // 当前选中的角色ID (用于 El-Select)


// --- 数据获取 ---

// 1. 获取所有角色列表
const fetchRoleOptions = async () => {
  try {
    // 假设 getRoleList 调用 /api/role/list
    const res = await getRoleList()
    // 假设后端返回 roles 列表，包含 id 和 roleName
    roleOptions.value = res.data || []

    // 2. 获取当前用户角色（需要在后端添加 /api/user/roles/{userId} 接口并调用）
    // 暂时跳过获取当前角色逻辑，直接在 watch 中处理。

  } catch (error) {
    ElMessage.error('加载角色列表失败')
  }
}


// --- 监听和重置 ---
watch(() => props.visible, (newVal) => {
  if (newVal && props.userId) {
    loading.value = true

    // 🚨 临时设置默认值：通常应该调用 API 获取用户当前的角色ID
    // 假设我们省略了获取当前角色的 API，在实际项目中，你需要调用 getUserRoleId(props.userId) 来初始化 currentRoleId
    currentRoleId.value = null;

    // 同时加载所有可选角色
    fetchRoleOptions().finally(() => {
      loading.value = false
    })

  } else if (!newVal) {
    currentRoleId.value = null
    emit('update:visible', false)
  }
})


// --- 提交逻辑 ---
const handleSubmit = async () => {
  if (!props.userId || currentRoleId.value === null) {
    return ElMessage.warning('请选择要分配的角色')
  }

  loading.value = true
  try {
    const data = {
      userId: props.userId,
      roleId: currentRoleId.value
    }

    // 调用 /api/user/assignRole 接口
    await assignUserRole(data)

    ElMessage.success(`用户 ${props.username} 角色分配成功！`)
    emit('success') // 通知父组件刷新列表
    emit('update:visible', false)

  } catch (error) {
    console.error('角色分配失败:', error)
    ElMessage.error('角色分配失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-dialog
      :model-value="visible"
      :title="`为用户 [${username}] 分配角色`"
      width="400px"
      @close="emit('update:visible', false)"
  >
    <el-form v-loading="loading" label-width="80px">
      <el-form-item label="选择角色">
        <el-select v-model="currentRoleId" placeholder="请选择角色">
          <el-option
              v-for="role in roleOptions"
              :key="role.id"
              :label="role.roleName"
              :value="role.id"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="emit('update:visible', false)">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          确认分配
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>