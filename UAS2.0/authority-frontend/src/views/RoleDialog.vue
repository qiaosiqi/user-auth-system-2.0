// src/views/RoleDialog.vue

<script setup>
import { ref, watch } from 'vue'
import { saveOrUpdateRole } from '../api/role' // 确保导入正确的 API
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  isEditMode: { type: Boolean, default: false }, // 是否为编辑模式
  initialData: { // 编辑时传入的角色初始数据
    type: Object,
    default: () => ({ id: null, roleName: '', roleCode: '' })
  }
})

const emit = defineEmits(['update:visible', 'success'])

// --- 状态 ---
const dialogTitle = ref('新增角色')
const loading = ref(false)

// 表单数据，使用 ref 确保响应式
const roleForm = ref({
  id: null,
  roleName: '',
  roleCode: ''
})

// 表单校验规则 (可根据需求完善)
const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色标识', trigger: 'blur' }]
}

const formRef = ref(null)

// --- 监听和数据初始化 ---
watch(() => props.visible, (newVal) => {
  if (newVal) {
    // 弹窗打开时初始化数据
    if (props.isEditMode) {
      dialogTitle.value = `编辑角色: ${props.initialData.roleName}`
      // 深度拷贝，避免直接修改父组件数据
      roleForm.value = { ...props.initialData }
    } else {
      dialogTitle.value = '新增角色'
      // 重置表单
      roleForm.value = { id: null, roleName: '', roleCode: '' }
      // 重置表单校验状态
      formRef.value?.resetFields()
    }
  } else {
    // 弹窗关闭时，通知父组件更新 visible 状态
    emit('update:visible', false)
  }
})


// --- 提交逻辑 ---
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const actionText = props.isEditMode ? '更新' : '新增'

        // 调用新增/更新接口
        await saveOrUpdateRole(roleForm.value)

        ElMessage.success(`${actionText}成功！`)
        emit('success') // 通知父组件刷新列表
        emit('update:visible', false) // 关闭弹窗

      } catch (error) {
        console.error('角色操作失败:', error)
        ElMessage.error('角色操作失败，请检查后端服务')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<template>
  <el-dialog
      :model-value="visible"
      :title="dialogTitle"
      width="400px"
      @close="emit('update:visible', false)"
  >
    <el-form
        ref="formRef"
        :model="roleForm"
        :rules="formRules"
        v-loading="loading"
        label-width="90px"
    >
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="roleForm.roleName" placeholder="如：管理员" />
      </el-form-item>

      <el-form-item label="角色标识" prop="roleCode">
        <el-input v-model="roleForm.roleCode" placeholder="如：ROLE_ADMIN" />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="emit('update:visible', false)">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ isEditMode ? '保存修改' : '确认新增' }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>